package com.oems.controller;

import com.oems.dto.*;
import com.oems.model.User;
import com.oems.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final CurrentUserService currentUserService;
    private final CourseService courseService;
    private final ExamService examService;
    private final ResultService resultService;

    public TeacherController(CurrentUserService currentUserService,
                             CourseService courseService,
                             ExamService examService,
                             ResultService resultService) {
        this.currentUserService = currentUserService;
        this.courseService = courseService;
        this.examService = examService;
        this.resultService = resultService;
    }

    @GetMapping("/courses")
    public List<CourseResponse> myCourses() {
        User teacher = currentUserService.requireCurrentUser();
        return courseService.teacherCourses(teacher.getId());
    }

    @GetMapping("/courses/{id}/students")
    public List<UserResponse> courseStudents(@PathVariable String id) {
        User teacher = currentUserService.requireCurrentUser();
        return courseService.studentsForTeacherCourse(teacher.getId(), id);
    }

    @GetMapping("/courses/{courseId}/students/{studentId}/results")
    public List<TeacherStudentCourseResultResponse> studentResults(@PathVariable String courseId, @PathVariable String studentId) {
        User teacher = currentUserService.requireCurrentUser();
        return resultService.studentResultsForTeacherCourse(teacher, courseId, studentId);
    }

    @PostMapping("/exams")
    public ExamResponse createExam(@Valid @RequestBody ExamCreateUpdateRequest request) {
        User teacher = currentUserService.requireCurrentUser();
        return examService.createExam(teacher, request);
    }

    @GetMapping("/exams")
    public List<ExamResponse> exams() {
        User teacher = currentUserService.requireCurrentUser();
        return examService.teacherExams(teacher.getId());
    }

    @GetMapping("/exams/{id}")
    public ExamResponse examDetail(@PathVariable String id) {
        User teacher = currentUserService.requireCurrentUser();
        return examService.examDetailForTeacher(teacher, id);
    }

    @PutMapping("/exams/{id}")
    public ExamResponse updateExam(@PathVariable String id, @Valid @RequestBody ExamCreateUpdateRequest request) {
        User teacher = currentUserService.requireCurrentUser();
        return examService.updateExam(teacher, id, request);
    }

    @PostMapping("/exams/{id}/questions")
    public QuestionResponse addQuestion(@PathVariable String id, @Valid @RequestBody QuestionCreateRequest request) {
        User teacher = currentUserService.requireCurrentUser();
        return examService.addQuestion(teacher, id, request);
    }

    @PutMapping("/questions/{id}")
    public QuestionResponse editQuestion(@PathVariable String id, @Valid @RequestBody QuestionUpdateRequest request) {
        User teacher = currentUserService.requireCurrentUser();
        return examService.updateQuestion(teacher, id, request);
    }

    @DeleteMapping("/questions/{id}")
    public void deleteQuestion(@PathVariable String id) {
        User teacher = currentUserService.requireCurrentUser();
        examService.deleteQuestion(teacher, id);
    }

    @PutMapping("/exams/{id}/publish")
    public ExamResponse publish(@PathVariable String id) {
        User teacher = currentUserService.requireCurrentUser();
        return examService.publishExam(teacher, id);
    }

    @GetMapping("/exams/{id}/results")
    public List<ExamResultItemResponse> results(@PathVariable String id) {
        User teacher = currentUserService.requireCurrentUser();
        return resultService.examResultsForTeacher(teacher, id);
    }

    @GetMapping("/exams/{id}/questions")
    public List<QuestionResponse> questions(@PathVariable String id) {
        User teacher = currentUserService.requireCurrentUser();
        return examService.teacherQuestionList(teacher, id);
    }
}

