package com.schoolmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.dto.AddExamRequest;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.ExamResponseDto;
import com.schoolmanagement.service.ExamService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/exam")
//@CrossOrigin(origins = "http://localhost:3000")
public class ExamController {

	@Autowired
	private ExamService exmaService;

	@PostMapping("/add")
	@Operation(summary = "Api to add exam")
	public ResponseEntity<ExamResponseDto> addCourse(@RequestBody AddExamRequest request) {
		return exmaService.addExam(request);
	}

	@GetMapping("/fetch/all")
	@Operation(summary = "Api to fetch all exams")
	public ResponseEntity<ExamResponseDto> fetchAllExams() {
		return exmaService.fetchAllExams();
	}

	@GetMapping("/fetch/all/grade-wise")
	@Operation(summary = "Api to fetch all exams by grades")
	public ResponseEntity<ExamResponseDto> fetchAllExambyGrade(@RequestParam("gradeId") int gradeId) {
		return exmaService.fetchAllExamByGrade(gradeId);
	}

	@GetMapping("/fetch/all/course-wise")
	@Operation(summary = "Api to fetch all exams by course")
	public ResponseEntity<ExamResponseDto> fetchAllExambyCourse(@RequestParam("courseId") int courseId) {
		return exmaService.fetchAllExambyCourse(courseId);
	}

	@GetMapping("/fetch/exam-id")
	@Operation(summary = "Api to fetch Exam using ID")
	public ResponseEntity<ExamResponseDto> fetchExamById(@RequestParam("examId") int examId) {
		return exmaService.fetchExamById(examId);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Api to delete exam")
	public ResponseEntity<CommonApiResponse> deleteExam(@RequestParam("examId") int examId) {
		return exmaService.deleteExam(examId);
	}

	@GetMapping("/fetch/upcoming/grade-wise")
	@Operation(summary = "Api to fetch all upcoming exams by grade")
	public ResponseEntity<ExamResponseDto> fetchUpcomingExamsByGrade(@RequestParam("gradeId") int gradeId,
			@RequestParam("role") String role) {
		return exmaService.fetchUpcomingExamsByGrade(gradeId, role);
	}

	@GetMapping("/fetch/previous/grade-wise")
	@Operation(summary = "Api to fetch all previous exams by grade")
	public ResponseEntity<ExamResponseDto> fetchPreviousExamsByGrade(@RequestParam("gradeId") int gradeId,
			@RequestParam("role") String role) {
		return exmaService.fetchPreviousExamsByGrade(gradeId, role);
	}
	
	// for teacher and admin
	@GetMapping("/fetch/upcoming/all")
	@Operation(summary = "Api to fetch all upcoming exams from all grades")
	public ResponseEntity<ExamResponseDto> fetchUpcomingExams() {
		return exmaService.fetchUpcomingExams();
	}

	@GetMapping("/fetch/previous/all")
	@Operation(summary = "Api to fetch all previous exams from all grades")
	public ResponseEntity<ExamResponseDto> fetchPreviousExams() {
		return exmaService.fetchPreviousExams();
	}

	// for students
	@GetMapping("/fetch/grade-wise/ongoing")
	@Operation(summary = "Api to fetch all ongoing exams by grade")
	public ResponseEntity<ExamResponseDto> fetchOngoingExamsByGrade(@RequestParam("gradeId") int gradeId,
			@RequestParam("studentId") int studentId, @RequestParam("role") String role) {
		return exmaService.fetchOngoingExamsByGrade(gradeId, studentId, role);
	}

}
