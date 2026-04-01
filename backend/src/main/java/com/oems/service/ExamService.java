package com.oems.service;

import com.oems.dto.*;
import com.oems.exception.ApiException;
import com.oems.model.*;
import com.oems.repository.CourseRepository;
import com.oems.repository.ExamRepository;
import com.oems.repository.QuestionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;
    private final MapperService mapperService;

    public ExamService(ExamRepository examRepository, CourseRepository courseRepository, QuestionRepository questionRepository, MapperService mapperService) {
        this.examRepository = examRepository;
        this.courseRepository = courseRepository;
        this.questionRepository = questionRepository;
        this.mapperService = mapperService;
    }

    public ExamResponse createExam(User teacher, ExamCreateUpdateRequest request) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        if (!course.getTeacher().getId().equals(teacher.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Teacher can only create exam in own courses");
        }
        Exam exam = Exam.builder()
                .title(request.title())
                .description(request.description())
                .course(course)
                .teacher(teacher)
                .durationMinutes(request.durationMinutes())
                .totalMarks(0)
                .scheduledStart(request.scheduledStart())
                .scheduledEnd(request.scheduledEnd())
                .published(false)
                .randomizeQuestions(request.randomizeQuestions())
                .passPercentage(request.passPercentage())
                .build();
        exam = examRepository.save(exam);
        return mapperService.toExamResponse(exam, 0);
    }

    public List<ExamResponse> teacherExams(String teacherId) {
        return examRepository.findByTeacherId(teacherId).stream()
                .map(exam -> mapperService.toExamResponse(exam, questionRepository.countByExamId(exam.getId())))
                .toList();
    }

    public List<ExamResponse> allExams() {
        return examRepository.findAll().stream()
                .map(exam -> mapperService.toExamResponse(exam, questionRepository.countByExamId(exam.getId())))
                .toList();
    }

    public ExamResponse examDetailForTeacher(User teacher, String examId) {
        Exam exam = getExam(examId);
        if (!exam.getTeacher().getId().equals(teacher.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        return mapperService.toExamResponse(exam, questionRepository.countByExamId(exam.getId()));
    }

    public ExamResponse updateExam(User teacher, String examId, ExamCreateUpdateRequest request) {
        Exam exam = getExam(examId);
        if (!exam.getTeacher().getId().equals(teacher.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        if (!course.getTeacher().getId().equals(teacher.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Teacher can only assign own course");
        }
        exam.setTitle(request.title());
        exam.setDescription(request.description());
        exam.setCourse(course);
        exam.setDurationMinutes(request.durationMinutes());
        exam.setScheduledStart(request.scheduledStart());
        exam.setScheduledEnd(request.scheduledEnd());
        exam.setRandomizeQuestions(request.randomizeQuestions());
        exam.setPassPercentage(request.passPercentage());
        exam = examRepository.save(exam);
        return mapperService.toExamResponse(exam, questionRepository.countByExamId(exam.getId()));
    }

    public QuestionResponse addQuestion(User teacher, String examId, QuestionCreateRequest request) {
        Exam exam = getExam(examId);
        ensureTeacherOwnsExam(teacher, exam);
        int orderIndex = questionRepository.findByExamIdOrderByOrderIndexAsc(examId).size() + 1;
        Question q = Question.builder()
                .exam(exam)
                .questionText(request.questionText())
                .optionA(request.optionA())
                .optionB(request.optionB())
                .optionC(request.optionC())
                .optionD(request.optionD())
                .correctOption(request.correctOption())
                .marks(request.marks())
                .orderIndex(orderIndex)
                .build();
        q = questionRepository.save(q);
        recalculateTotalMarks(exam);
        return mapperService.toQuestionResponse(q);
    }

    public QuestionResponse updateQuestion(User teacher, String questionId, QuestionUpdateRequest request) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Question not found"));
        ensureTeacherOwnsExam(teacher, q.getExam());
        q.setQuestionText(request.questionText());
        q.setOptionA(request.optionA());
        q.setOptionB(request.optionB());
        q.setOptionC(request.optionC());
        q.setOptionD(request.optionD());
        q.setCorrectOption(request.correctOption());
        q.setMarks(request.marks());
        q = questionRepository.save(q);
        recalculateTotalMarks(q.getExam());
        return mapperService.toQuestionResponse(q);
    }

    public void deleteQuestion(User teacher, String questionId) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Question not found"));
        ensureTeacherOwnsExam(teacher, q.getExam());
        Exam exam = q.getExam();
        questionRepository.delete(q);
        List<Question> remaining = questionRepository.findByExamIdOrderByOrderIndexAsc(exam.getId());
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setOrderIndex(i + 1);
        }
        questionRepository.saveAll(remaining);
        recalculateTotalMarks(exam);
    }

    public ExamResponse publishExam(User teacher, String examId) {
        Exam exam = getExam(examId);
        ensureTeacherOwnsExam(teacher, exam);
        long count = questionRepository.countByExamId(examId);
        if (count < 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "At least one question is required");
        }
        exam.setPublished(true);
        exam = examRepository.save(exam);
        return mapperService.toExamResponse(exam, count);
    }

    public List<QuestionResponse> teacherQuestionList(User teacher, String examId) {
        Exam exam = getExam(examId);
        ensureTeacherOwnsExam(teacher, exam);
        return questionRepository.findByExamIdOrderByOrderIndexAsc(examId)
                .stream().map(mapperService::toQuestionResponse).toList();
    }

    public List<ExamResponse> availableExamsForStudent(List<String> enrolledCourseIds) {
        if (enrolledCourseIds.isEmpty()) {
            return List.of();
        }
        LocalDateTime now = LocalDateTime.now();
        return examRepository.findByCourseIdInAndPublishedTrueAndScheduledStartBeforeAndScheduledEndAfter(enrolledCourseIds, now, now)
                .stream()
                .map(exam -> mapperService.toExamResponse(exam, questionRepository.countByExamId(exam.getId())))
                .toList();
    }

    public ExamResponse studentExamInfo(String examId, List<String> enrolledCourseIds) {
        Exam exam = getExam(examId);
        LocalDateTime now = LocalDateTime.now();
        boolean visible = exam.isPublished()
                && enrolledCourseIds.contains(exam.getCourse().getId())
                && !now.isBefore(exam.getScheduledStart())
                && !now.isAfter(exam.getScheduledEnd());
        if (!visible) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Exam not available");
        }
        return mapperService.toExamResponse(exam, questionRepository.countByExamId(examId));
    }

    public Exam getExam(String id) {
        return examRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Exam not found"));
    }

    public List<Question> getQuestionsInStoredOrder(ExamAttempt attempt) {
        Map<String, Question> byId = questionRepository.findByExamIdOrderByOrderIndexAsc(attempt.getExam().getId())
                .stream().collect(Collectors.toMap(Question::getId, q -> q));
        List<Question> ordered = new ArrayList<>();
        if (attempt.getQuestionOrder() == null || attempt.getQuestionOrder().isBlank()) {
            return byId.values().stream().toList();
        }
        for (String token : attempt.getQuestionOrder().split(",")) {
            Question q = byId.get(token.trim());
            if (q != null) {
                ordered.add(q);
            }
        }
        return ordered;
    }

    public String buildQuestionOrderForAttempt(Exam exam) {
        List<String> ids = questionRepository.findByExamIdOrderByOrderIndexAsc(exam.getId())
                .stream().map(Question::getId).toList();
        List<String> mutable = new ArrayList<>(ids);
        if (exam.isRandomizeQuestions()) {
            Collections.shuffle(mutable);
        }
        return mutable.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private void ensureTeacherOwnsExam(User teacher, Exam exam) {
        if (!exam.getTeacher().getId().equals(teacher.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }

    private void recalculateTotalMarks(Exam exam) {
        int total = questionRepository.findByExamIdOrderByOrderIndexAsc(exam.getId())
                .stream().mapToInt(Question::getMarks).sum();
        exam.setTotalMarks(total);
        examRepository.save(exam);
    }
}

