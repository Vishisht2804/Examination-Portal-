package com.oems.controller;

import com.oems.dto.*;
import com.oems.model.Course;
import com.oems.model.User;
import com.oems.service.AttemptService;
import com.oems.service.CourseService;
import com.oems.service.CurrentUserService;
import com.oems.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final CurrentUserService currentUserService;
    private final CourseService courseService;
    private final ExamService examService;
    private final AttemptService attemptService;

    public StudentController(CurrentUserService currentUserService,
                             CourseService courseService,
                             ExamService examService,
                             AttemptService attemptService) {
        this.currentUserService = currentUserService;
        this.courseService = courseService;
        this.examService = examService;
        this.attemptService = attemptService;
    }

    @GetMapping("/exams")
    public List<ExamResponse> availableExams() {
        User student = currentUserService.requireCurrentUser();
        List<String> courseIds = courseService.studentCourses(student.getId()).stream().map(Course::getId).toList();
        return examService.availableExamsForStudent(courseIds);
    }

    @GetMapping("/courses")
    public List<CourseResponse> myCourses() {
        User student = currentUserService.requireCurrentUser();
        return courseService.studentCourseResponses(student.getId());
    }

    @GetMapping("/exams/{id}")
    public ExamResponse examInfo(@PathVariable String id) {
        User student = currentUserService.requireCurrentUser();
        List<String> courseIds = courseService.studentCourses(student.getId()).stream().map(Course::getId).toList();
        return examService.studentExamInfo(id, courseIds);
    }

    @PostMapping("/exams/{id}/start")
    public AttemptResponse start(@PathVariable String id) {
        User student = currentUserService.requireCurrentUser();
        List<String> courseIds = courseService.studentCourses(student.getId()).stream().map(Course::getId).toList();
        return attemptService.startAttempt(student, id, courseIds);
    }

    @GetMapping("/attempts/{id}")
    public AttemptResponse attempt(@PathVariable String id) {
        User student = currentUserService.requireCurrentUser();
        return attemptService.getAttempt(student, id);
    }

    @PutMapping("/attempts/{id}/answer")
    public void saveAnswer(@PathVariable String id, @Valid @RequestBody SaveAnswerRequest request) {
        User student = currentUserService.requireCurrentUser();
        attemptService.saveAnswer(student, id, request);
    }

    @PostMapping("/attempts/{id}/submit")
    public SubmitResultResponse submit(@PathVariable String id) {
        User student = currentUserService.requireCurrentUser();
        return attemptService.submit(student, id);
    }

    @GetMapping("/attempts")
    public List<AttemptHistoryItemResponse> history() {
        User student = currentUserService.requireCurrentUser();
        return attemptService.history(student);
    }

    @GetMapping("/attempts/{id}/result")
    public ResultDetailResponse result(@PathVariable String id) {
        User student = currentUserService.requireCurrentUser();
        return attemptService.resultDetail(student, id);
    }
}

