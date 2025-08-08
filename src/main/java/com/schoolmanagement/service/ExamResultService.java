package com.schoolmanagement.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.schoolmanagement.dao.ExamResultDao;
import com.schoolmanagement.dao.GradeDao;
import com.schoolmanagement.dao.StudentAnswerDao;
import com.schoolmanagement.dao.UserDao;
import com.schoolmanagement.dto.ExamResultResponse;
import com.schoolmanagement.entity.ExamResult;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.Question;
import com.schoolmanagement.entity.StudentAnswer;
import com.schoolmanagement.entity.User;

@Component
public class ExamResultService {

	private final Logger LOG = LoggerFactory.getLogger(ExamResultService.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private GradeDao gradeDao;

	@Autowired
	private ExamResultDao examResultDao;

	@Autowired
	private StudentAnswerDao studentAnswerService;

	public ResponseEntity<ExamResultResponse> fetchStudentExamResult(int studentId) {

		LOG.info("Request received for fetching result using student id");

		ExamResultResponse response = new ExamResultResponse();

		if (studentId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<ExamResultResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User student = this.userDao.findById(studentId).orElse(null);

		List<ExamResult> results = this.examResultDao.findByStudent(student);

		if (CollectionUtils.isEmpty(results)) {
			response.setResponseMessage("No Exam Results found!!!");
			response.setSuccess(true);

			return new ResponseEntity<ExamResultResponse>(response, HttpStatus.OK);
		}

		for (ExamResult result : results) {

			if (!result.getExam().getQuestions().isEmpty()) {

				List<StudentAnswer> answers = this.studentAnswerService.findByExamAndStudent(result.getExam(), student);

				for (Question question : result.getExam().getQuestions()) {
					question.setCorrectAns(question.getCorrectAnswer());   // correctAnswer is json ignored, so it will go in response

					for (StudentAnswer answer : answers) {
						if (question.getId() == answer.getQuestion().getId()) {
							question.setAnswer(answer.getCorrectAnswer());
							break;
						}
					}

				}
			}

		}

		response.setResults(results);
		response.setResponseMessage("Exams Results Fetched successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<ExamResultResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<ExamResultResponse> fetchStudentExamResultGradeWise(int gradeId) {

		LOG.info("Request received for fetching result using grade id");

		ExamResultResponse response = new ExamResultResponse();

		if (gradeId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<ExamResultResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Grade grade = this.gradeDao.findById(gradeId).orElse(null);

		List<ExamResult> results = this.examResultDao.findByExam_Grade(grade);

		if (CollectionUtils.isEmpty(results)) {
			response.setResponseMessage("No Exam Results found!!!");
			response.setSuccess(true);

			return new ResponseEntity<ExamResultResponse>(response, HttpStatus.OK);
		}

		for (ExamResult result : results) {

			if (!result.getExam().getQuestions().isEmpty()) {

				List<StudentAnswer> answers = this.studentAnswerService.findByExamAndStudent(result.getExam(), result.getStudent());

				for (Question question : result.getExam().getQuestions()) {
					question.setCorrectAns(question.getCorrectAnswer());   // correctAnswer is json ignored, so it will go in response

					for (StudentAnswer answer : answers) {
						if (question.getId() == answer.getQuestion().getId()) {
							question.setAnswer(answer.getCorrectAnswer());
							break;
						}
					}

				}
			}

		}

		response.setResults(results);
		response.setResponseMessage("Exams Results Fetched successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<ExamResultResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<ExamResultResponse> fetchAllStudentResults() {

		LOG.info("Request received for fetching results");

		ExamResultResponse response = new ExamResultResponse();
		
		List<ExamResult> results = this.examResultDao.findAll();

		if (CollectionUtils.isEmpty(results)) {
			response.setResponseMessage("No Exam Results found!!!");
			response.setSuccess(true);

			return new ResponseEntity<ExamResultResponse>(response, HttpStatus.OK);
		}

		for (ExamResult result : results) {

			if (!result.getExam().getQuestions().isEmpty()) {

				List<StudentAnswer> answers = this.studentAnswerService.findByExamAndStudent(result.getExam(), result.getStudent());

				for (Question question : result.getExam().getQuestions()) {
					question.setCorrectAns(question.getCorrectAnswer());   // correctAnswer is json ignored, so it will go in response

					for (StudentAnswer answer : answers) {
						if (question.getId() == answer.getQuestion().getId()) {
							question.setAnswer(answer.getCorrectAnswer());
							break;
						}
					}

				}
			}

		}
		
		response.setResults(results);
		response.setResponseMessage("Exams Results Fetched successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<ExamResultResponse>(response, HttpStatus.OK);
	}

}
