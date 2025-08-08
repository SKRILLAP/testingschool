package com.schoolmanagement.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.schoolmanagement.dao.ExamDao;
import com.schoolmanagement.dao.QuestionDao;
import com.schoolmanagement.dto.AddQuestionDto;
import com.schoolmanagement.dto.QuestionsResponseDto;
import com.schoolmanagement.entity.Exam;
import com.schoolmanagement.entity.Question;
import com.schoolmanagement.utility.Constants.ActiveStatus;

import jakarta.transaction.Transactional;

@Component
@Transactional
public class ExamQuestionService {

	private final Logger LOG = LoggerFactory.getLogger(ExamService.class);

	@Autowired
	private QuestionDao questionDao;;

	@Autowired
	private ExamDao examDao;

	public ResponseEntity<QuestionsResponseDto> addExamQuestion(AddQuestionDto request) {

		LOG.info("Request received for add exam question");

		QuestionsResponseDto response = new QuestionsResponseDto();

		if (request == null || request.getExamId() == 0) {
			response.setResponseMessage("missing input or bad request");
			response.setSuccess(false);

			return new ResponseEntity<QuestionsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Exam exam = this.examDao.findById(request.getExamId()).orElse(null);

		if (exam == null) {
			response.setResponseMessage("Exam not found");
			response.setSuccess(false);

			return new ResponseEntity<QuestionsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Question> questions = exam.getQuestions();

		if (!AddQuestionDto.validate(request)) {
			response.setQuestions(questions);
			response.setResponseMessage("missing input or bad request");
			response.setSuccess(false);

			return new ResponseEntity<QuestionsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Question question = AddQuestionDto.toQuestionEntity(request);

		if (question == null) {
			response.setQuestions(questions);
			response.setResponseMessage("Question not found");
			response.setSuccess(false);

			return new ResponseEntity<QuestionsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		question.setCorrectAnswer(request.getCorrectAnswer());
		question.setStatus(ActiveStatus.ACTIVE.value());
		question.setExams(Arrays.asList(exam));

		Question updateQuestion = this.questionDao.save(question);

		if (updateQuestion == null) {
			response.setQuestions(questions);
			response.setResponseMessage("Failed to add exam question");
			response.setSuccess(false);

			return new ResponseEntity<QuestionsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		questions.add(updateQuestion);
		
		for (Question q : questions) {
			q.setCorrectAns(q.getCorrectAnswer());
			q.setAnswer(5);
		}
		
		response.setQuestions(questions);
		response.setResponseMessage("Exam Question Added Successful");
		response.setSuccess(true);

		return new ResponseEntity<QuestionsResponseDto>(response, HttpStatus.OK);

	}

	public ResponseEntity<QuestionsResponseDto> deleteExamQuestion(int questionId) {

		LOG.info("Request received for deleting the Question using Id");

		QuestionsResponseDto response = new QuestionsResponseDto();

		if (questionId == 0) {
			response.setResponseMessage("missing input or bad request");
			response.setSuccess(false);

			return new ResponseEntity<QuestionsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Question question = this.questionDao.findById(questionId).orElse(null);

		if (question == null) {
			response.setResponseMessage("Question not found");
			response.setSuccess(false);

			return new ResponseEntity<QuestionsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		
		// Remove the question from associated exams
		for (Exam exam : question.getExams()) {
		    exam.getQuestions().remove(question);
		}

		this.questionDao.delete(question);
		
		response.setResponseMessage("Exam Questions Deleted Successful");
		response.setSuccess(true);

		return new ResponseEntity<QuestionsResponseDto>(response, HttpStatus.OK);

	}

}
