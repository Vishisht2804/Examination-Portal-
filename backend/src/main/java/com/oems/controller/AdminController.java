package com.oems.controller;

import com.oems.dto.*;
import com.oems.service.CourseService;
import com.oems.service.ExamService;
import com.oems.service.ResultService;
import com.oems.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final CourseService courseService;
    private final ExamService examService;
    private final ResultService resultService;

    public AdminController(UserService userService, CourseService courseService, ExamService examService, ResultService resultService) {
        this.userService = userService;
        this.courseService = courseService;
        this.examService = examService;
        this.resultService = resultService;
    }

    @PostMapping("/users")
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return userService.findAll();
    }

    @PutMapping("/users/{id}")
    public UserResponse updateUser(@PathVariable String id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/users/{id}")
    public void deactivateUser(@PathVariable String id) {
        userService.deactivate(id);
    }

    @PostMapping("/courses")
    public CourseResponse createCourse(@Valid @RequestBody CourseCreateRequest request) {
        return courseService.create(request);
    }

    @GetMapping("/courses")
    public List<CourseResponse> courses() {
        return courseService.findAll();
    }

    @PutMapping("/courses/{id}")
    public CourseResponse updateCourse(@PathVariable String id, @Valid @RequestBody CourseUpdateRequest request) {
        return courseService.update(id, request);
    }

    @DeleteMapping("/courses/{id}")
    public void deleteCourse(@PathVariable String id) {
        courseService.delete(id);
    }

    @GetMapping("/exams")
    public List<ExamResponse> exams() {
        return examService.allExams();
    }

    @PutMapping("/courses/{id}/enroll")
    public CourseResponse enroll(@PathVariable String id, @Valid @RequestBody EnrollStudentsRequest request) {
        return courseService.enroll(id, request);
    }

    @PutMapping("/courses/{id}/teacher")
    public CourseResponse reassignTeacher(@PathVariable String id, @Valid @RequestBody ReassignTeacherRequest request) {
        return courseService.reassignTeacher(id, request);
    }

    @DeleteMapping("/courses/{id}/teacher")
    public CourseResponse removeTeacher(@PathVariable String id) {
        return courseService.removeTeacher(id);
    }

    @PostMapping("/courses/{id}/students/{studentId}")
    public CourseResponse addStudent(@PathVariable String id, @PathVariable String studentId) {
        return courseService.addStudent(id, studentId);
    }

    @DeleteMapping("/courses/{id}/students/{studentId}")
    public CourseResponse removeStudent(@PathVariable String id, @PathVariable String studentId) {
        return courseService.removeStudent(id, studentId);
    }

    @GetMapping("/results/exam/{examId}")
    public List<ExamResultItemResponse> results(@PathVariable String examId) {
        return resultService.examResultsForAdmin(examId);
    }

    @PostMapping("/results/publish/{examId}")
    public Map<String, Object> publish(@PathVariable String examId) {
        return resultService.publishResults(examId);
    }
}

