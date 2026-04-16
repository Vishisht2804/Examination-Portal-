package com.oems.service;

import com.oems.dto.ExamResultItemResponse;
import com.oems.dto.TeacherStudentCourseResultResponse;
import com.oems.exception.ApiException;
import com.oems.model.Course;
import com.oems.model.Exam;
import com.oems.model.User;
import com.oems.repository.ExamAttemptRepository;
import com.oems.repository.CourseRepository;
import com.oems.repository.ExamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ResultService {

    private final ExamAttemptRepository examAttemptRepository;
    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final MapperService mapperService;

    public ResultService(ExamAttemptRepository examAttemptRepository,
                         ExamRepository examRepository,
                         CourseRepository courseRepository,
                         MapperService mapperService) {
        this.examAttemptRepository = examAttemptRepository;
        this.examRepository = examRepository;
        this.courseRepository = courseRepository;
        this.mapperService = mapperService;
    }

    public List<ExamResultItemResponse> examResultsForTeacher(User teacher, String examId) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Exam not found"));
        if (!exam.getTeacher().getId().equals(teacher.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        if (!exam.isPublished()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Results are available only for published exams");
        }
        return examAttemptRepository.findByExamIdOrderByStartedAtDesc(examId)
                .stream().map(mapperService::toExamResultItem).toList();
    }

    public List<TeacherStudentCourseResultResponse> studentResultsForTeacherCourse(User teacher, String courseId, String studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        if (course.getTeacher() == null || !course.getTeacher().getId().equals(teacher.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        return examAttemptRepository.findByStudentIdOrderByStartedAtDesc(studentId)
                .stream()
                .filter(attempt -> attempt.getExam().getCourse().getId().equals(courseId))
                .map(attempt -> new TeacherStudentCourseResultResponse(
                        attempt.getId(),
                        attempt.getExam().getId(),
                        attempt.getExam().getTitle(),
                        attempt.getScore(),
                        attempt.getTotalMarks(),
                        Boolean.TRUE.equals(attempt.getPassed()),
                        attempt.getStatus(),
                        attempt.getStartedAt(),
                        attempt.getSubmittedAt()
                ))
                .toList();
    }

    public List<ExamResultItemResponse> examResultsForAdmin(String examId) {
        examRepository.findById(examId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Exam not found"));
        return examAttemptRepository.findByExamIdOrderByStartedAtDesc(examId)
                .stream().map(mapperService::toExamResultItem).toList();
    }

    public Map<String, Object> publishResults(String examId) {
        examRepository.findById(examId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Exam not found"));
        return Map.of("examId", examId, "published", true);
    }
}

