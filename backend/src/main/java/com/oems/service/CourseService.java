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
        course.setStudents(students);
        return mapperService.toCourseResponse(courseRepository.save(course));
    }

    public CourseResponse update(String id, CourseUpdateRequest request) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        User teacher = getUserByRole(request.teacherId(), Role.TEACHER, "Teacher not found");
        course.setName(request.name());
        course.setDescription(request.description());
        course.setTeacher(teacher);
        course.setActive(request.active());
        return mapperService.toCourseResponse(courseRepository.save(course));
    }

    public void delete(String id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        courseRepository.delete(course);
    }

    public CourseResponse reassignTeacher(String id, ReassignTeacherRequest request) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        User teacher = getUserByRole(request.teacherId(), Role.TEACHER, "Teacher not found");
        course.setTeacher(teacher);
        return mapperService.toCourseResponse(courseRepository.save(course));
    }

    public CourseResponse removeTeacher(String id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        course.setTeacher(null);
        return mapperService.toCourseResponse(courseRepository.save(course));
    }

    public CourseResponse addStudent(String courseId, String studentId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        User student = getUserByRole(studentId, Role.STUDENT, "Student not found");
        course.getStudents().add(student);
        return mapperService.toCourseResponse(courseRepository.save(course));
    }

    public CourseResponse removeStudent(String courseId, String studentId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        course.getStudents().removeIf(student -> student.getId().equals(studentId));
        return mapperService.toCourseResponse(courseRepository.save(course));
    }

    public List<CourseResponse> teacherCourses(String teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream().map(mapperService::toCourseResponse).toList();
    }

    public List<Course> studentCourses(String studentId) {
        return courseRepository.findByStudentsId(studentId);
    }

    public List<CourseResponse> studentCourseResponses(String studentId) {
        return courseRepository.findByStudentsId(studentId).stream().map(mapperService::toCourseResponse).toList();
    }

    public List<UserResponse> studentsForTeacherCourse(String teacherId, String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Course not found"));
        if (course.getTeacher() == null || !course.getTeacher().getId().equals(teacherId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        return course.getStudents().stream().map(mapperService::toUserResponse).toList();
    }

    private User getUserByRole(String id, Role role, String message) {
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, message));
        if (user.getRole() != role) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User role mismatch");
        }
        return user;
    }
}

