package com.schoolmanagement.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.schoolmanagement.dao.CourseDao;
import com.schoolmanagement.dao.ExamDao;
import com.schoolmanagement.dao.ExamResultDao;
import com.schoolmanagement.dao.GradeDao;
import com.schoolmanagement.dao.UserDao;
import com.schoolmanagement.dto.AddExamRequest;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.ExamResponseDto;
import com.schoolmanagement.entity.Course;
import com.schoolmanagement.entity.Exam;
import com.schoolmanagement.entity.ExamResult;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.Question;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.exception.GradeSaveFailedException;
import com.schoolmanagement.utility.Constants.ActiveStatus;
import com.schoolmanagement.utility.Constants.ExamSubmissionMessage;
import com.schoolmanagement.utility.Constants.UserRole;

import jakarta.transaction.Transactional;

@Component
@Transactional
public class ExamService {

	private final Logger LOG = LoggerFactory.getLogger(ExamService.class);

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private GradeDao gradeDao;

	@Autowired
	private ExamDao examDao;

	@Autowired
	private ExamResultDao examResultDao;

	public ResponseEntity<ExamResponseDto> addExam(AddExamRequest request) {

		LOG.info("Request received for add exam");

		ExamResponseDto response = new ExamResponseDto();

		if (request == null || !AddExamRequest.validate(request)) {
			response.setResponseMessage("missing input or bad request");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		User teacher = this.userDao.findById(request.getTeacherId()).orElse(null);

		if (teacher == null) {
			response.setResponseMessage("Teacher not found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Grade grade = this.gradeDao.findById(request.getGradeId()).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Course course = this.courseDao.findById(request.getCourseId()).orElse(null);

		if (course == null) {
			response.setResponseMessage("course not found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Exam exam = new Exam();
		exam.setAddedDateTime(
				String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
		exam.setCourse(course);
		exam.setEndTime(request.getEndTime());
		exam.setGrade(grade);
		exam.setName(request.getName());
		exam.setStartTime(request.getStartTime());
		exam.setStatus(ActiveStatus.ACTIVE.value());
		exam.setTeacher(teacher);

		Exam addedExam = this.examDao.save(exam);

		if (addedExam == null) {
			response.setResponseMessage("Failed to add exam");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		response.setExams(Arrays.asList(addedExam));
		response.setResponseMessage("Exam Added Successful");
		response.setSuccess(true);

		return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);

	}

	public ResponseEntity<ExamResponseDto> fetchAllExams() {

		LOG.info("Request received for fetching all exams");

		ExamResponseDto response = new ExamResponseDto();

		List<Exam> exams = new ArrayList<>();

		exams = this.examDao.findByStatusOrderByIdDesc(ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(exams)) {
			response.setResponseMessage("No Exams found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
		}

		for (Exam exam : exams) {
			if (!CollectionUtils.isEmpty(exam.getQuestions())) {
				for (Question question : exam.getQuestions()) {
					question.setCorrectAns(question.getCorrectAnswer());
					question.setAnswer(5);
				}
			}
		}

		response.setExams(exams);
		response.setResponseMessage("Exams fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<ExamResponseDto> fetchAllExamByGrade(int gradeId) {

		LOG.info("Request received for fetching all exams by Grade");

		ExamResponseDto response = new ExamResponseDto();

		Grade grade = this.gradeDao.findById(gradeId).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Exam> exams = new ArrayList<>();

		exams = this.examDao.findByGradeAndStatus(grade, ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(exams)) {
			response.setResponseMessage("No Exams found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
		}

		response.setExams(exams);
		response.setResponseMessage("Exams fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<ExamResponseDto> fetchAllExambyCourse(int courseId) {

		LOG.info("Request received for fetching all exams by Grade");

		ExamResponseDto response = new ExamResponseDto();

		Course course = this.courseDao.findById(courseId).orElse(null);

		if (course == null) {
			response.setResponseMessage("course not found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Exam> exams = new ArrayList<>();

		exams = this.examDao.findByCourseAndStatus(course, ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(exams)) {
			response.setResponseMessage("No Exams found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
		}

		response.setExams(exams);
		response.setResponseMessage("Exams fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deleteExam(int examId) {

		LOG.info("Request received for deleting exam");

		CommonApiResponse response = new CommonApiResponse();

		if (examId == 0) {
			response.setResponseMessage("missing exam Id");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Exam exam = this.examDao.findById(examId).orElse(null);

		if (exam == null) {
			response.setResponseMessage("exam not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		exam.setStatus(ActiveStatus.DEACTIVATED.value());
		Exam updatedExam = this.examDao.save(exam);

		if (updatedExam == null) {
			throw new GradeSaveFailedException("Failed to delete the Exam");
		}

		response.setResponseMessage("Exam Deleted Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<ExamResponseDto> fetchExamById(int examId) {

		LOG.info("Request received for fetching the exam using exam id");

		ExamResponseDto response = new ExamResponseDto();

		if (examId == 0) {
			response.setResponseMessage("exam not found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Exam exam = this.examDao.findById(examId).orElse(null);

		if (exam == null) {
			response.setResponseMessage("No Exam found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
		}

		response.setExams(Arrays.asList(exam));
		response.setResponseMessage("Exams fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);

	}

	public ResponseEntity<ExamResponseDto> fetchUpcomingExamsByGrade(int gradeId, String role) {

		LOG.info("Request received for fetching upcoming exams by Grade");

		ExamResponseDto response = new ExamResponseDto();

		Grade grade = this.gradeDao.findById(gradeId).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Exam> exams = new ArrayList<>();

		String now = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		exams = this.examDao.findByGradeAndStartTimeGreaterThanAndStatus(grade, now, ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(exams)) {
			response.setResponseMessage("No Exams found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
		}

		if (!role.equals(UserRole.ROLE_STUDENT.value())) {
			for (Exam exam : exams) {
				if (!CollectionUtils.isEmpty(exam.getQuestions())) {
					for (Question question : exam.getQuestions()) {
						question.setCorrectAns(question.getCorrectAnswer());
					}
				}
			}
		}

		for (Exam exam : exams) {
			if (!CollectionUtils.isEmpty(exam.getQuestions())) {
				for (Question question : exam.getQuestions()) {
					question.setAnswer(5);
				}
			}
		}

		response.setExams(exams);
		response.setResponseMessage("Exams fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<ExamResponseDto> fetchPreviousExamsByGrade(int gradeId, String role) {

		LOG.info("Request received for fetching previous exams by Grade");

		ExamResponseDto response = new ExamResponseDto();

		Grade grade = this.gradeDao.findById(gradeId).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Exam> exams = new ArrayList<>();

		String now = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		exams = this.examDao.findByGradeAndStartTimeLessThanAndStatus(grade, now, ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(exams)) {
			response.setResponseMessage("No Exams found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
		}

		if (!role.equals(UserRole.ROLE_STUDENT.value())) {
			for (Exam exam : exams) {
				if (!CollectionUtils.isEmpty(exam.getQuestions())) {
					for (Question question : exam.getQuestions()) {
						question.setCorrectAns(question.getCorrectAnswer());
					}
				}
			}
		}

		for (Exam exam : exams) {
			if (!CollectionUtils.isEmpty(exam.getQuestions())) {
				for (Question question : exam.getQuestions()) {
					question.setAnswer(5);
				}
			}
		}

		response.setExams(exams);
		response.setResponseMessage("Exams fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<ExamResponseDto> fetchOngoingExamsByGrade(int gradeId, int studentId, String role) {

		LOG.info("Request received for fetching ongoing exams by Grade");

		ExamResponseDto response = new ExamResponseDto();

		Grade grade = this.gradeDao.findById(gradeId).orElse(null);

		if (grade == null || studentId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Exam> exams = new ArrayList<>();

		String now = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		exams = this.examDao.findByGradeAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndStatus(grade, now, now,
				ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(exams)) {
			response.setResponseMessage("No Exams found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
		}

		for (Exam exam : exams) {

			List<ExamResult> results = this.examResultDao.findByStudentAndExam(this.userDao.findById(studentId).get(),
					exam);

			if (!CollectionUtils.isEmpty(results)) {
				exam.setMessage(ExamSubmissionMessage.SUBMITTED.value()); // that means Student has already submitted
																			// the exam and we will not allow him to
																			// attempt again
			}

		}

		if (!role.equals(UserRole.ROLE_STUDENT.value())) {
			for (Exam exam : exams) {
				if (!CollectionUtils.isEmpty(exam.getQuestions())) {
					for (Question question : exam.getQuestions()) {
						question.setCorrectAns(question.getCorrectAnswer());
					}
				}
			}
		}

		response.setExams(exams);
		response.setResponseMessage("Exams fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<ExamResponseDto> fetchUpcomingExams() {

		LOG.info("Request received for fetching upcoming exams");

		ExamResponseDto response = new ExamResponseDto();

		List<Exam> exams = new ArrayList<>();

		String now = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		exams = this.examDao.findByStartTimeGreaterThanAndStatusOrderByIdDesc(now, ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(exams)) {
			response.setResponseMessage("No Exams found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
		}

		for (Exam exam : exams) {
			if (!CollectionUtils.isEmpty(exam.getQuestions())) {
				for (Question question : exam.getQuestions()) {
					question.setCorrectAns(question.getCorrectAnswer());
				}
			}
		}

		for (Exam exam : exams) {
			if (!CollectionUtils.isEmpty(exam.getQuestions())) {
				for (Question question : exam.getQuestions()) {
					question.setAnswer(5);
				}
			}
		}

		response.setExams(exams);
		response.setResponseMessage("Exams fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<ExamResponseDto> fetchPreviousExams() {

		LOG.info("Request received for fetching previous exams by Grade");

		ExamResponseDto response = new ExamResponseDto();

		List<Exam> exams = new ArrayList<>();

		String now = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		exams = this.examDao.findByStartTimeLessThanAndStatusOrderByIdDesc(now, ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(exams)) {
			response.setResponseMessage("No Exams found");
			response.setSuccess(false);

			return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
		}

		for (Exam exam : exams) {
			if (!CollectionUtils.isEmpty(exam.getQuestions())) {
				for (Question question : exam.getQuestions()) {
					question.setCorrectAns(question.getCorrectAnswer());
				}
			}
		}

		for (Exam exam : exams) {
			if (!CollectionUtils.isEmpty(exam.getQuestions())) {
				for (Question question : exam.getQuestions()) {
					question.setAnswer(5);
				}
			}
		}

		response.setExams(exams);
		response.setResponseMessage("Exams fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<ExamResponseDto>(response, HttpStatus.OK);
	}

}
