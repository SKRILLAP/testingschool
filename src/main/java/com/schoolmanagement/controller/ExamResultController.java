package com.schoolmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.dto.ExamResultResponse;
import com.schoolmanagement.service.ExamResultService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/exam/result")
//@CrossOrigin(origins = "http://localhost:3000")
public class ExamResultController {
	
	@Autowired
	private ExamResultService examResultService;
	
	@GetMapping("/fetch/student-wise")
	@Operation(summary = "Api to fetch student exam student")
	public ResponseEntity<ExamResultResponse> fetchStudentExamResult(@RequestParam("studentId") int studentId) {
		return examResultService.fetchStudentExamResult(studentId);
	}
	
	@GetMapping("/fetch/grade-wise")
	@Operation(summary = "Api to fetch student exam results by grade id")
	public ResponseEntity<ExamResultResponse> fetchStudentExamResultGradeWise(@RequestParam("gradeId") int gradeId) {
		return examResultService.fetchStudentExamResultGradeWise(gradeId);
	}
	
	@GetMapping("/fetch/all")
	@Operation(summary = "Api to fetch student exam results")
	public ResponseEntity<ExamResultResponse> fetchAllStudentResults() {
		return examResultService.fetchAllStudentResults();
	}

}
