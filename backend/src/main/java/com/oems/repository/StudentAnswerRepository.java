package com.oems.repository;

import com.oems.model.StudentAnswer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAnswerRepository extends MongoRepository<StudentAnswer, String> {
    List<StudentAnswer> findByAttemptId(String attemptId);
    Optional<StudentAnswer> findByAttemptIdAndQuestionId(String attemptId, String questionId);
}
