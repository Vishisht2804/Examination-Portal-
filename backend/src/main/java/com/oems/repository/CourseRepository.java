package com.oems.repository;

import com.oems.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByTeacherId(String teacherId);
    List<Course> findByStudentsId(String studentId);
}
