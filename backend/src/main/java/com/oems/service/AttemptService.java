package com.oems.service;

import com.oems.dto.*;
import com.oems.exception.ApiException;
import com.oems.model.*;
import com.oems.repository.ExamAttemptRepository;
import com.oems.repository.QuestionRepository;
import com.oems.repository.StudentAnswerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttemptService {

    private final ExamAttemptRepository attemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final QuestionRepository questionRepository;
    private final ExamService examService;
    private final MapperService mapperService;

    public AttemptService(ExamAttemptRepository attemptRepository,
                          StudentAnswerRepository studentAnswerRepository,
                          QuestionRepository questionRepository,
                          ExamService examService,
                          MapperService mapperService) {
        this.attemptRepository = attemptRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.questionRepository = questionRepository;
        this.examService = examService;
        this.mapperService = mapperService;
    }

    @Transactional
    public AttemptResponse startAttempt(User student, String examId, List<String> enrolledCourseIds) {
        Exam exam = examService.getExam(examId);
        validateStudentExamVisibility(exam, enrolledCourseIds);

        Optional<ExamAttempt> existing = attemptRepository.findFirstByStudentIdAndExamIdAndStatusOrderByStartedAtDesc(
                student.getId(), examId, AttemptStatus.IN_PROGRESS
        );
        if (existing.isPresent()) {
            return buildAttemptResponse(existing.get());
        }

        String questionOrder = examService.buildQuestionOrderForAttempt(exam);
        LocalDateTime now = LocalDateTime.now();
        ExamAttempt attempt = ExamAttempt.builder()
                .student(student)
                .exam(exam)
                .startedAt(now)
                .deadline(now.plusMinutes(exam.getDurationMinutes()))
                .status(AttemptStatus.IN_PROGRESS)
                .questionOrder(questionOrder)
                .build();
        attempt = attemptRepository.save(attempt);
        return buildAttemptResponse(attempt);
    }

    public AttemptResponse getAttempt(User student, String attemptId) {
        ExamAttempt attempt = getOwnedAttempt(student, attemptId);
        return buildAttemptResponse(attempt);
    }

    @Transactional
    public void saveAnswer(User student, String attemptId, SaveAnswerRequest request) {
        ExamAttempt attempt = getOwnedAttempt(student, attemptId);
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Attempt is not in progress");
        }
        Set<Long> allowed = parseQuestionIds(attempt.getQuestionOrder());
        if (!allowed.contains(request.questionId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Question does not belong to attempt");
        }
        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Question not found"));

        StudentAnswer answer = studentAnswerRepository.findByAttemptIdAndQuestionId(attemptId, request.questionId())
                .orElse(StudentAnswer.builder().attempt(attempt).question(question).build());
        answer.setSelectedOption(request.selectedOption());
        studentAnswerRepository.save(answer);
    }

    @Transactional
    public SubmitResultResponse submit(User student, String attemptId) {
        ExamAttempt attempt = getOwnedAttempt(student, attemptId);
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            return buildSubmitResponse(attempt);
        }

        LocalDateTime now = LocalDateTime.now();
        attempt.setSubmittedAt(now);
        attempt.setStatus(now.isAfter(attempt.getDeadline()) ? AttemptStatus.TIMED_OUT : AttemptStatus.SUBMITTED);

        List<Question> questions = examService.getQuestionsInStoredOrder(attempt);
        Map<String, StudentAnswer> answerByQuestion = studentAnswerRepository.findByAttemptId(attempt.getId())
                .stream().collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a));

        int score = 0;
        for (Question q : questions) {
            StudentAnswer answer = answerByQuestion.get(q.getId());
            if (answer == null) {
                answer = StudentAnswer.builder()
                        .attempt(attempt)
                        .question(q)
                        .selectedOption(null)
                        .build();
            }
            boolean correct = answer.getSelectedOption() != null && answer.getSelectedOption() == q.getCorrectOption();
            int marks = correct ? q.getMarks() : 0;
            answer.setCorrect(correct);
            answer.setMarksAwarded(marks);
            studentAnswerRepository.save(answer);
            score += marks;
        }

        int total = attempt.getExam().getTotalMarks() == null ? 0 : attempt.getExam().getTotalMarks();
        double percentage = total == 0 ? 0 : ((double) score / total) * 100;
        boolean passed = percentage >= attempt.getExam().getPassPercentage();
        attempt.setScore(score);
        attempt.setTotalMarks(total);
        attempt.setPassed(passed);
        attemptRepository.save(attempt);

        return buildSubmitResponse(attempt);
    }

    public List<AttemptHistoryItemResponse> history(User student) {
        return attemptRepository.findByStudentIdOrderByStartedAtDesc(student.getId())
                .stream().map(mapperService::toAttemptHistoryItem).toList();
    }

    public ResultDetailResponse resultDetail(User student, String attemptId) {
        ExamAttempt attempt = getOwnedAttempt(student, attemptId);
        if (attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Attempt is still in progress");
        }
        List<QuestionResultResponse> questionResults = studentAnswerRepository.findByAttemptId(attemptId)
                .stream()
                .map(a -> new QuestionResultResponse(
                        a.getQuestion().getId(),
                        a.getQuestion().getQuestionText(),
                        a.getSelectedOption(),
                        a.getQuestion().getCorrectOption(),
                        a.getMarksAwarded() == null ? 0 : a.getMarksAwarded(),
                        a.getQuestion().getMarks()
                ))
                .toList();

        int total = attempt.getTotalMarks() == null ? 0 : attempt.getTotalMarks();
        int score = attempt.getScore() == null ? 0 : attempt.getScore();
        double pct = total == 0 ? 0 : (double) score * 100 / total;

        return new ResultDetailResponse(
                attempt.getId(),
                attempt.getExam().getId(),
                attempt.getExam().getTitle(),
                score,
                total,
                pct,
                Boolean.TRUE.equals(attempt.getPassed()),
                attempt.getStatus(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt(),
                questionResults
        );
    }

    private AttemptResponse buildAttemptResponse(ExamAttempt attempt) {
        List<Question> questions = examService.getQuestionsInStoredOrder(attempt);
        List<StudentQuestionResponse> questionDtos = questions.stream().map(mapperService::toStudentQuestionResponse).toList();
        List<AnswerStateResponse> answers = studentAnswerRepository.findByAttemptId(attempt.getId())
                .stream().map(a -> new AnswerStateResponse(a.getQuestion().getId(), a.getSelectedOption())).toList();
        return mapperService.toAttemptResponse(attempt, questionDtos, answers);
    }

    private SubmitResultResponse buildSubmitResponse(ExamAttempt attempt) {
        int total = attempt.getTotalMarks() == null ? 0 : attempt.getTotalMarks();
        int score = attempt.getScore() == null ? 0 : attempt.getScore();
        double percentage = total == 0 ? 0 : ((double) score / total) * 100;
        return new SubmitResultResponse(
                attempt.getId(),
                attempt.getStatus(),
                score,
                total,
                percentage,
                Boolean.TRUE.equals(attempt.getPassed())
        );
    }

    private void validateStudentExamVisibility(Exam exam, List<String> enrolledCourseIds) {
        LocalDateTime now = LocalDateTime.now();
        boolean visible = exam.isPublished()
                && enrolledCourseIds.contains(exam.getCourse().getId())
                && !now.isBefore(exam.getScheduledStart())
                && !now.isAfter(exam.getScheduledEnd());
        if (!visible) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Exam not available");
        }
    }

    private ExamAttempt getOwnedAttempt(User student, String attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Attempt not found"));
        if (!attempt.getStudent().getId().equals(student.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Can only access own attempt");
        }
        return attempt;
    }

    private Set<Long> parseQuestionIds(String csv) {
        if (csv == null || csv.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }
}

