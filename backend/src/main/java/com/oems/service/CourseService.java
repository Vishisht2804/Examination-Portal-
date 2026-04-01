package com.oems.service;

import com.oems.dto.*;
import com.oems.exception.ApiException;
import com.oems.model.Course;
import com.oems.model.Role;
import com.oems.model.User;
import com.oems.repository.CourseRepository;
import com.oems.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final MapperService mapperService;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository, MapperService mapperService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.mapperService = mapperService;
    }

    public CourseResponse create(CourseCreateRequest request) {
        User teacher = getUserByRole(request.teacherId(), Role.TEACHER, "Teacher not found");
        Course course = Course.builder()
                .name(request.name())
                .description(request.description())
                .teacher(teacher)
                .active(true)
                .build();
        return mapperService.toCourseResponse(courseRepository.save(course));
    }

    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream().map(mapperService::toCourseResponse).toList();
    }

    public CourseResponse enroll(String id, EnrollStudentsRequest request) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        Set<User> students = new HashSet<>();
        for (String studentId : request.studentIds()) {
            students.add(getUserByRole(studentId, Role.STUDENT, "Student not found: " + studentId));
        }
        course.getStudents().addAll(students);
        return mapperService.toCourseResponse(courseRepository.save(course));
    }

    public CourseResponse reassignTeacher(String id, ReassignTeacherRequest request) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        User teacher = getUserByRole(request.teacherId(), Role.TEACHER, "Teacher not found");
        course.setTeacher(teacher);
        return mapperService.toCourseResponse(courseRepository.save(course));
    }

    public List<CourseResponse> teacherCourses(String teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream().map(mapperService::toCourseResponse).toList();
    }

    public List<Course> studentCourses(String studentId) {
        return courseRepository.findByStudentsId(studentId);
    }

    private User getUserByRole(String id, Role role, String message) {
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, message));
        if (user.getRole() != role) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User role mismatch");
        }
        return user;
    }
}

