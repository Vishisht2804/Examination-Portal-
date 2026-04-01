package com.oems.service;

import com.oems.dto.*;
import com.oems.model.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MapperService {

    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getRole(), user.isActive(), user.getCreatedAt());
    }

    public CourseResponse toCourseResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getTeacher().getId(),
                course.getTeacher().getFullName(),
                course.isActive(),
                course.getCreatedAt(),
                course.getStudents().stream().map(User::getId).toList()
        );
    }

    public ExamResponse toExamResponse(Exam exam, long questionCount) {
        return new ExamResponse(
                exam.getId(),
                exam.getTitle(),
                exam.getDescription(),
                exam.getCourse().getId(),
                exam.getCourse().getName(),
                exam.getTeacher().getId(),
                exam.getTeacher().getFullName(),
                exam.getDurationMinutes(),
                exam.getTotalMarks(),
                exam.getScheduledStart(),
                exam.getScheduledEnd(),
                exam.isPublished(),
                exam.isRandomizeQuestions(),
                exam.getPassPercentage(),
                questionCount,
                exam.getCreatedAt()
        );
    }

    public QuestionResponse toQuestionResponse(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getQuestionText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                question.getCorrectOption(),
                question.getMarks(),
                question.getOrderIndex()
        );
    }

    public StudentQuestionResponse toStudentQuestionResponse(Question question) {
        return new StudentQuestionResponse(
                question.getId(),
                question.getQuestionText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                question.getMarks(),
                question.getOrderIndex()
        );
    }

    public AttemptHistoryItemResponse toAttemptHistoryItem(ExamAttempt attempt) {
        return new AttemptHistoryItemResponse(
                attempt.getId(),
                attempt.getExam().getId(),
                attempt.getExam().getTitle(),
                attempt.getScore(),
                attempt.getTotalMarks(),
                Boolean.TRUE.equals(attempt.getPassed()),
                attempt.getStatus(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt()
        );
    }

    public ExamResultItemResponse toExamResultItem(ExamAttempt attempt) {
        return new ExamResultItemResponse(
                attempt.getId(),
                attempt.getStudent().getId(),
                attempt.getStudent().getFullName(),
                attempt.getScore(),
                attempt.getTotalMarks(),
                Boolean.TRUE.equals(attempt.getPassed()),
                attempt.getStatus(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt()
        );
    }

    public AttemptResponse toAttemptResponse(ExamAttempt attempt, List<StudentQuestionResponse> questions, List<AnswerStateResponse> answers) {
        long remaining = Math.max(0, Duration.between(LocalDateTime.now(), attempt.getDeadline()).getSeconds());
        return new AttemptResponse(
                attempt.getId(),
                attempt.getExam().getId(),
                attempt.getExam().getTitle(),
                attempt.getStatus(),
                attempt.getStartedAt(),
                attempt.getDeadline(),
                remaining,
                questions,
                answers
        );
    }
}
