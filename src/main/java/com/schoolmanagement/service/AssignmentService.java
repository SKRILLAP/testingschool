package com.schoolmanagement.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.schoolmanagement.dao.AssignmentDao;
import com.schoolmanagement.dao.CourseDao;
import com.schoolmanagement.dao.GradeDao;
import com.schoolmanagement.dao.StudentAssignmentSubmissionDao;
import com.schoolmanagement.dao.UserDao;
import com.schoolmanagement.dto.AddAssignmentRequest;
import com.schoolmanagement.dto.AssignmentResponse;
import com.schoolmanagement.dto.AssignmentSubmissionResponse;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.StudentAssignmentSubmissionRequest;
import com.schoolmanagement.entity.Assignment;
import com.schoolmanagement.entity.Course;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.StudentAssignmentSubmission;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.utility.Constants.ActiveStatus;
import com.schoolmanagement.utility.Constants.AssignmentGrade;
import com.schoolmanagement.utility.Constants.AssignmentStatus;
import com.schoolmanagement.utility.Constants.AssignmentSubmissionStatus;
import com.schoolmanagement.utility.Constants.UserRole;
import com.schoolmanagement.utility.StorageService;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class AssignmentService {

	private final Logger LOG = LoggerFactory.getLogger(AssignmentService.class);

	@Autowired
	private AssignmentDao assignmentDao;

	@Autowired
	private StudentAssignmentSubmissionDao assignmentSubmissionDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private GradeDao gradeDao;

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private StorageService storageService;

	public ResponseEntity<CommonApiResponse> addAssignment(AddAssignmentRequest request) {

		LOG.info("Request received for add assignment");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Grade grade = this.gradeDao.findById(request.getGradeId()).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Course course = this.courseDao.findById(request.getCourseId()).orElse(null);

		if (course == null) {
			response.setResponseMessage("Course not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		User teacher = userDao.findById(request.getTeacherId()).orElse(null);

		if (teacher == null) {
			response.setResponseMessage("teacher not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		String addedTime = String
				.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		String assignmentDocFileName = this.storageService.store(request.getAssignmentDoc());

		Assignment assignment = new Assignment();
		assignment.setAddedDateTime(addedTime);
		assignment.setAssignmentDocFileName(assignmentDocFileName);
		assignment.setDeadLine(request.getDeadLine());
		assignment.setDescription(request.getDescription());
		assignment.setGrade(grade);
		assignment.setName(request.getName());
		assignment.setNote(request.getNote());
		assignment.setStatus(AssignmentStatus.ACTIVE.value());
		assignment.setTeacher(teacher);
		assignment.setCourse(course);

		Assignment savedAssignment = this.assignmentDao.save(assignment);

		if (savedAssignment == null) {
			response.setResponseMessage("Failed to add the assignment!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// for each student from same grade, we need to add the entry in db for entity
		// StudentAssignmentSubmission

		List<User> students = this.userDao.findByRoleAndGradeAndStatus(UserRole.ROLE_STUDENT.value(), grade,
				ActiveStatus.ACTIVE.value());

		for (User student : students) {
			StudentAssignmentSubmission submission = new StudentAssignmentSubmission();
			submission.setAssignment(savedAssignment);
			submission.setStudent(student);
			submission.setGrade("");
			submission.setRemarks("");
			submission.setStatus(AssignmentSubmissionStatus.PENDING.value());
			submission.setSubmissionFileName("");
			submission.setSubmittedAt("");

			assignmentSubmissionDao.save(submission);
		}

		response.setResponseMessage("Assignment Added Successful!!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<CommonApiResponse> deleteAssignment(int assignmentId) {

		LOG.info("Request received for deleting the assignment");

		CommonApiResponse response = new CommonApiResponse();

		if (assignmentId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Assignment assignment = this.assignmentDao.findById(assignmentId).orElse(null);

		if (assignment == null) {
			response.setResponseMessage("Assignment not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		assignment.setStatus(AssignmentStatus.DEACTIVATED.value());

		Assignment savedAssignment = this.assignmentDao.save(assignment);

		if (savedAssignment == null) {
			response.setResponseMessage("Failed to delete the assignment!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.setResponseMessage("Assignment Deleted Successful!!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateAssignment(int assignmentId) {

		LOG.info("Request received for marking the assignment as deadline meet");

		CommonApiResponse response = new CommonApiResponse();

		if (assignmentId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Assignment assignment = this.assignmentDao.findById(assignmentId).orElse(null);

		if (assignment == null) {
			response.setResponseMessage("Assignment not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		assignment.setStatus(AssignmentStatus.DEADLINE.value());

		Assignment savedAssignment = this.assignmentDao.save(assignment);

		if (savedAssignment == null) {
			response.setResponseMessage("Failed to update the assignment status!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<StudentAssignmentSubmission> studentSubmissions = this.assignmentSubmissionDao
				.findByAssignmentAndStatus(savedAssignment, AssignmentSubmissionStatus.PENDING.value());

		studentSubmissions.stream().peek(submission -> {
			submission.setStatus(AssignmentSubmissionStatus.NOT_SUBMITTED.value());
			submission.setRemarks(AssignmentSubmissionStatus.NOT_SUBMITTED.value());
			submission.setGrade(AssignmentGrade.F.getCode()); // marking as FAIL
		}).forEach(assignmentSubmissionDao::save);

		response.setResponseMessage("Assignment Status updated Successful!!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<AssignmentResponse> fetchAllAssignmentsByTeacher(int teacherId) {

		LOG.info("Request received for fetching assingment by teacher");

		AssignmentResponse response = new AssignmentResponse();

		if (teacherId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<AssignmentResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User teacher = this.userDao.findById(teacherId).orElse(null);

		if (teacher == null) {
			response.setResponseMessage("Teacher not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<AssignmentResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<Assignment> assignments = this.assignmentDao.findByTeacherOrderByIdDesc(teacher);

		if (CollectionUtils.isEmpty(assignments)) {
			response.setResponseMessage("No Assignments found");
			response.setSuccess(false);

			return new ResponseEntity<AssignmentResponse>(response, HttpStatus.OK);
		}

		response.setAssignments(assignments);
		response.setResponseMessage("Assignments fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<AssignmentResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<AssignmentResponse> fetchAllAssignments() {

		LOG.info("Request received for fetching assingment by admin");

		AssignmentResponse response = new AssignmentResponse();

		List<Assignment> assignments = this.assignmentDao.findAllByOrderByIdDesc();

		if (CollectionUtils.isEmpty(assignments)) {
			response.setResponseMessage("No Assignments found");
			response.setSuccess(false);

			return new ResponseEntity<AssignmentResponse>(response, HttpStatus.OK);
		}

		response.setAssignments(assignments);
		response.setResponseMessage("Assignments fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<AssignmentResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<AssignmentSubmissionResponse> fetchAssignmenSubmissionStatusByStudent(int studentId) {

		LOG.info("Request received for fetching assingment submission by studentId");

		AssignmentSubmissionResponse response = new AssignmentSubmissionResponse();

		if (studentId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<AssignmentSubmissionResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User student = this.userDao.findById(studentId).orElse(null);

		if (student == null) {
			response.setResponseMessage("Teacher not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<AssignmentSubmissionResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<StudentAssignmentSubmission> submissions = this.assignmentSubmissionDao
				.findByStudentOrderByIdDesc(student);

		if (CollectionUtils.isEmpty(submissions)) {
			response.setResponseMessage("No Assignments founds");
			response.setSuccess(false);

			return new ResponseEntity<AssignmentSubmissionResponse>(response, HttpStatus.OK);
		}

		response.setAssignmentSubmissions(submissions);
		response.setResponseMessage("Assignments fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<AssignmentSubmissionResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> addSubmissionRequest(StudentAssignmentSubmissionRequest request) {

		LOG.info("Request received for student submitting the assignment");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getId() == 0) {
			response.setResponseMessage("Submission Id missing");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		StudentAssignmentSubmission submission = this.assignmentSubmissionDao.findById(request.getId()).orElse(null);

		if (submission == null) {
			response.setResponseMessage("Submission not found!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String addedTime = String
				.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		String submissionFileName = this.storageService.store(request.getSubmissionFile());

		submission.setSubmissionFileName(submissionFileName);
		submission.setStatus(AssignmentSubmissionStatus.SUBMITTED.value());
		submission.setSubmittedAt(addedTime);

		StudentAssignmentSubmission updatedSubmission = this.assignmentSubmissionDao.save(submission);

		if (updatedSubmission == null) {
			response.setResponseMessage("Failed to submit the assignment!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		response.setResponseMessage("Assignment Submitted Successful!!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<CommonApiResponse> reviewStudentSubmittedAssginment(int submissionId, String grade,
			String remark) {

		LOG.info("Request received for student submitting the assignment");

		CommonApiResponse response = new CommonApiResponse();

		if (submissionId == 0 || grade == null || remark == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		StudentAssignmentSubmission submission = this.assignmentSubmissionDao.findById(submissionId).orElse(null);

		if (submission == null) {
			response.setResponseMessage("Submission not found!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		submission.setStatus(AssignmentSubmissionStatus.CHECKED.value());
		submission.setGrade(grade);
		submission.setRemarks(remark);

		StudentAssignmentSubmission updatedSubmission = this.assignmentSubmissionDao.save(submission);

		if (updatedSubmission == null) {
			response.setResponseMessage("Failed to review the assignment!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.setResponseMessage("Assignment Review Successful!!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<AssignmentSubmissionResponse> fetchSubmissionByAssignment(int assignmentId) {

		LOG.info("Request received for fetching submissions by assignment");

		AssignmentSubmissionResponse response = new AssignmentSubmissionResponse();

		if (assignmentId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<AssignmentSubmissionResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Assignment assignment = this.assignmentDao.findById(assignmentId).orElse(null);

		if (assignment == null) {
			response.setResponseMessage("Assignment not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<AssignmentSubmissionResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<StudentAssignmentSubmission> submissions = this.assignmentSubmissionDao.findByAssignment(assignment);

		if (CollectionUtils.isEmpty(submissions)) {
			response.setResponseMessage("No Assignments founds");
			response.setSuccess(false);

			return new ResponseEntity<AssignmentSubmissionResponse>(response, HttpStatus.OK);
		}

		response.setAssignmentSubmissions(submissions);
		response.setResponseMessage("Assignment Submissions fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<AssignmentSubmissionResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<Resource> downloadDocumemt(String documentFileName, HttpServletResponse response) {

		Resource resource = storageService.load(documentFileName);
		if (resource == null) {
			// Handle file not found
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Document\"")
				.body(resource);

	}

}
