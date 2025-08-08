package com.schoolmanagement.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.DocumentException;
import com.schoolmanagement.dto.AddAssignmentRequest;
import com.schoolmanagement.dto.AssignmentResponse;
import com.schoolmanagement.dto.AssignmentSubmissionResponse;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.StudentAssignmentSubmissionRequest;
import com.schoolmanagement.service.AssignmentService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/assignment")
//@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {

	@Autowired
	private AssignmentService assignmentService;

	@PostMapping("/add")
	@Operation(summary = "Api to add grade assignment by teacher")
	public ResponseEntity<CommonApiResponse> addAssignment(AddAssignmentRequest request) {
		return assignmentService.addAssignment(request);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Api to delete the grade assignment by teacher")
	public ResponseEntity<CommonApiResponse> deleteAssignment(@RequestParam("assignmentId") int assignmentId) {
		return assignmentService.deleteAssignment(assignmentId);
	}

	// teacher will update the status as Deadline meet, and student assignment
	// submission date over
	@PutMapping("/deadline")
	@Operation(summary = "Api to update the grade assignment by teacher")
	public ResponseEntity<CommonApiResponse> updateAssignment(@RequestParam("assignmentId") int assignmentId) {
		return assignmentService.updateAssignment(assignmentId);
	}

	// for teacher
	@GetMapping("/fetch/teacher-wise")
	@Operation(summary = "Api to fetch all assingments by teacher id")
	public ResponseEntity<AssignmentResponse> fetchAllAssignmentsByTeacher(@RequestParam("teacherId") int teacherId) {
		return assignmentService.fetchAllAssignmentsByTeacher(teacherId);
	}

	// for admin
	@GetMapping("/fetch/all")
	@Operation(summary = "Api to fetch all assingments")
	public ResponseEntity<AssignmentResponse> fetchAllAssignments() {
		return assignmentService.fetchAllAssignments();
	}

	// below apis are for Student Assignment submission
	@GetMapping("submission/fetch/student-wise")
	@Operation(summary = "Api to fetch assignment submission status by student")
	public ResponseEntity<AssignmentSubmissionResponse> fetchAssignmenSubmissionStatusByStudent(
			@RequestParam("studentId") int studentId) {
		return assignmentService.fetchAssignmenSubmissionStatusByStudent(studentId);
	}

	@PostMapping("/student/submission")
	@Operation(summary = "Api to add grade assignment by teacher")
	public ResponseEntity<CommonApiResponse> addSubmissionRequest(StudentAssignmentSubmissionRequest request) {
		return assignmentService.addSubmissionRequest(request);
	}

	// by teacher
	@GetMapping("/submission/review")
	@Operation(summary = "Api to give review on Student assignment submission")
	public ResponseEntity<CommonApiResponse> reviewStudentSubmittedAssginment(
			@RequestParam("submissionId") int submissionId, @RequestParam("grade") String grade,
			@RequestParam("remark") String remark) {
		return assignmentService.reviewStudentSubmittedAssginment(submissionId, grade, remark);
	}

	@GetMapping("submission/fetch")
	@Operation(summary = "Api to fetch submissions by assignment")
	public ResponseEntity<AssignmentSubmissionResponse> fetchSubmissionByAssignment(
			@RequestParam("assignmentId") int assignmentId) {
		return assignmentService.fetchSubmissionByAssignment(assignmentId);
	}

	@GetMapping("/document/{documentFileName}/download")
	@Operation(summary = "Api for downloading the Employee document")
	public ResponseEntity<Resource> downloadDocumemt(@PathVariable("documentFileName") String documentFileName,
			HttpServletResponse response) throws DocumentException, IOException {
		return this.assignmentService.downloadDocumemt(documentFileName, response);
	}
	
}
