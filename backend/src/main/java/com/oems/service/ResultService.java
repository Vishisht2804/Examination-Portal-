package com.oems.service;

import com.oems.dto.ExamResultItemResponse;
import com.oems.exception.ApiException;
import com.oems.model.Exam;
import com.oems.model.User;
import com.oems.repository.ExamAttemptRepository;
import com.oems.repository.ExamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ResultService {

    private final ExamAttemptRepository examAttemptRepository;
    private final ExamRepository examRepository;
    private final MapperService mapperService;

    public ResultService(ExamAttemptRepository examAttemptRepository, ExamRepository examRepository, MapperService mapperService) {
        this.examAttemptRepository = examAttemptRepository;
        this.examRepository = examRepository;
        this.mapperService = mapperService;
    }

    public List<ExamResultItemResponse> examResultsForTeacher(User teacher, String examId) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Exam not found"));
        if (!exam.getTeacher().getId().equals(teacher.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        return examAttemptRepository.findByExamIdOrderByStartedAtDesc(examId)
                .stream().map(mapperService::toExamResultItem).toList();
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

