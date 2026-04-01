package com.oems.repository;

import com.oems.model.Exam;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExamRepository extends MongoRepository<Exam, String> {
    List<Exam> findByTeacherId(String teacherId);
    List<Exam> findByCourseIdInAndPublishedTrueAndScheduledStartBeforeAndScheduledEndAfter(
            List<String> courseIds,
            LocalDateTime nowStart,
            LocalDateTime nowEnd
    );
}
