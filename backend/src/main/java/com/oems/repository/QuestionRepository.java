package com.oems.repository;

import com.oems.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findByExamIdOrderByOrderIndexAsc(String examId);
    long countByExamId(String examId);
}
