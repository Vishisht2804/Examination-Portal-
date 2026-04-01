package com.oems.repository;

import com.oems.model.AttemptStatus;
import com.oems.model.ExamAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends MongoRepository<ExamAttempt, String> {
    Optional<ExamAttempt> findFirstByStudentIdAndExamIdAndStatusOrderByStartedAtDesc(String studentId, String examId, AttemptStatus status);
    List<ExamAttempt> findByStudentIdOrderByStartedAtDesc(String studentId);
    List<ExamAttempt> findByExamIdOrderByStartedAtDesc(String examId);
}
