package com.schoolmanagement.service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.schoolmanagement.dao.ExamDao;
import com.schoolmanagement.dao.ExamResultDao;
import com.schoolmanagement.dao.QuestionDao;
import com.schoolmanagement.dao.StudentAnswerDao;
import com.schoolmanagement.dao.UserDao;
import com.schoolmanagement.dto.QuestionsResponseDto;
import com.schoolmanagement.dto.StudentAnswerRequest;
import com.schoolmanagement.entity.Course;
import com.schoolmanagement.entity.Exam;
import com.schoolmanagement.entity.ExamResult;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.Question;
import com.schoolmanagement.entity.StudentAnswer;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.exception.ExamResultSaveFailedException;
import com.schoolmanagement.utility.Constants.ExamResultStatus;
import com.schoolmanagement.utility.Constants.ExamSubmissionMessage;
import com.schoolmanagement.utility.Constants.UserRole;
import com.schoolmanagement.utility.DateTimeUtils;
import com.schoolmanagement.utility.EmailService;

import io.micrometer.common.util.StringUtils;

@Component
public class StudentAnswerService {

	private final Logger LOG = LoggerFactory.getLogger(StudentAnswerService.class);

	@Autowired
	private StudentAnswerDao studentAnswerDao;

	@Autowired
	private ExamDao examDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private QuestionDao questionDao;

	@Autowired
	private ExamResultDao examResultDao;

	@Autowired
	private EmailService emailService;

	@Value("${com.schoolmanagement.universityName}")
	private String universityName; // check application. properties file for this

	public ResponseEntity<QuestionsResponseDto> addStudentAnswers(StudentAnswerRequest request) {

		LOG.info("Request received for add student answers");

		QuestionsResponseDto response = new QuestionsResponseDto();

		if (request == null || !StudentAnswerRequest.validate(request)) {
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

		User student = this.userDao.findById(request.getStudentId()).orElse(null);

		if (student == null || !student.getRole().equals(UserRole.ROLE_STUDENT.value())) {
			response.setResponseMessage("Student not found");
			response.setSuccess(false);

			return new ResponseEntity<QuestionsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<StudentAnswer> studentAnswers = new ArrayList<>();

		List<Question> questions = request.getQuestions();

		String answerSubmittedTime = String
				.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		for (int i = 0; i < questions.size(); i++) {
			Question question = questions.get(i);
			Question fetchedQuestion = this.questionDao.findById(question.getId()).get();

			StudentAnswer answer = new StudentAnswer();
			answer.setExam(exam);
			answer.setQuestion(fetchedQuestion);
			answer.setStudent(student);
			answer.setSubmitDateTime(answerSubmittedTime);
			answer.setCorrectAnswer(question.getAnswer());

			studentAnswers.add(answer);

			fetchedQuestion.setAnswer(question.getAnswer());
			questions.set(i, fetchedQuestion);
		}

		List<StudentAnswer> addedStudentAnswers = this.studentAnswerDao.saveAll(studentAnswers);

		if (addedStudentAnswers == null) {
			response.setResponseMessage("Failed to Post the Student answers");
			response.setSuccess(false);

			return new ResponseEntity<QuestionsResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		String message = populateStudentExamResultAndMailResultToStudent(questions, exam, student, answerSubmittedTime);

		response.setQuestions(questions);
		response.setResponseMessage(message);
		response.setSuccess(true);

		return new ResponseEntity<QuestionsResponseDto>(response, HttpStatus.OK);

	}

	private String populateStudentExamResultAndMailResultToStudent(List<Question> questions, Exam exam, User student,
			String answerSubmittedTime) {
		// Assuming you have the necessary services and repositories

		// Calculate total questions, correct answers, wrong answers, marks, and score
		int totalQuestions = questions.size();
		int totalCorrectAnswers = 0;
		int totalWrongAnswers = 0;
		double score = 0;
		double totalMarks = 0.0;

		for (Question question : questions) {
			int studentAnswer = question.getAnswer();
			totalMarks += question.getMarks();
			if (studentAnswer == question.getCorrectAnswer()) {
				totalCorrectAnswers++;
				score += question.getMarks();
			} else {
				totalWrongAnswers++;
			}
		}

		// Calculate percentage
		double percentage = (score / totalMarks) * 100.0;

		// Format the percentage with two digits after the decimal point
		DecimalFormat df = new DecimalFormat("#.##");
		String formattedPercentage = df.format(percentage);

		percentage = Double.parseDouble(formattedPercentage);

		// Create ExamResult object
		ExamResult examResult = new ExamResult();
		examResult.setTotalQuestions(totalQuestions);
		examResult.setTotalCorrectAnswers(totalCorrectAnswers);
		examResult.setTotalWrongAnswers(totalWrongAnswers);
		examResult.setTotalMarks(totalMarks);
		examResult.setScore(score); // Assuming score is the same as total marks
		examResult.setPercentage(percentage);

		// Set Exam, Student, and DateTime
		examResult.setExam(exam); // Replace yourExamObject with the actual Exam object
		examResult.setStudent(student); // Replace yourStudentObject with the actual User object representing the
										// student
		examResult.setDateTime(answerSubmittedTime); // Set the current date and time

		// Set result status based on a threshold (e.g., 35.0)
		String resultStatus = (percentage >= 35.0) ? ExamResultStatus.PASS.value() : ExamResultStatus.FAIL.value();
		examResult.setResultStatus(resultStatus);

		// Save the examResult to the database
		ExamResult addedResult = examResultDao.save(examResult); // Replace yourExamResultRepository with your
																	// JPA repository for ExamResult

		if (addedResult == null) {
			throw new ExamResultSaveFailedException("Failed to Store Student Exam Result!!!");
		}

		// Send mail to the student with the result
		String resultMessage = (resultStatus == ExamResultStatus.PASS.value())
				? ExamSubmissionMessage.CONGRATULATIONS_PASS.value()
				: ExamSubmissionMessage.FAILED.value();

		String mailBody = makeEmailBody(examResult, resultMessage);

		if (StringUtils.isEmpty(mailBody)) {
			LOG.error("Failed to send Exam Result to " + student.getEmailId());
		}

		boolean flag = this.sendOrderEmail(student.getEmailId(),
				exam.getGrade().getName() + "-" + exam.getName() + " Examination Result", mailBody);

		if (flag) {
			LOG.info("Exam Result Mail Sent to " + student.getEmailId());
		} else {
			LOG.error("Failed to send Exam Result Mail to " + student.getEmailId());
		}

		return resultMessage;

	}

	public boolean sendOrderEmail(String to, String subject, String message) {

		try {
			this.emailService.sendEmail(to, subject, message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public String makeEmailBody(ExamResult examResult, String resultMessage) {

		Exam exam = examResult.getExam();
		Course course = examResult.getExam().getCourse();
		Grade grade = course.getGrade();

		StringBuilder emailBody = new StringBuilder();
		emailBody.append("<html><body>");
		emailBody.append("<h3>Dear Student,</h3>");
		emailBody.append(
				"<p>We hope this Email finds you well. We want to inform you about the results of your recent exam.</p>");

		if (resultMessage.equals(ExamSubmissionMessage.CONGRATULATIONS_PASS.value())) {
			emailBody.append("<p><span style='color: green;'><b>").append(resultMessage).append("</b></span></p>");
		} else {
			emailBody.append("<p><span style='color: red;'><b>").append(resultMessage).append("</b></span></p>");
		}

		emailBody.append("<h3>Exam Detail:</h3>");
		emailBody.append("<p>Timing: " + DateTimeUtils.getProperDateTimeFormatFromEpochTime(exam.getStartTime())
				+ " to " + DateTimeUtils.getProperDateTimeFormatFromEpochTime(exam.getEndTime()) + "</p>");
		// Create a dynamic table for the list of orders
		emailBody.append("<table border='1'>");
		emailBody.append("<tr><th>Grade</th><th>Course</th><th>Exam</th></tr>");

		emailBody.append("<tr>");
		emailBody.append("<td>").append(grade.getName()).append("</td>");
		emailBody.append("<td>").append(course.getName()).append("</td>");
		emailBody.append("<td>").append(exam.getName()).append("</td>");
		emailBody.append("</tr>");

		emailBody.append("</table>");

		emailBody.append("<h3>Exam Result:</h3>");

		emailBody.append("<table border='1'>");
		emailBody.append("<tr><th>Total Questions</th><th>Total Correct Answers</th><th>Total Wrong Answers</th></tr>");
		emailBody.append("<tr>");
		emailBody.append("<td>").append(examResult.getTotalQuestions()).append("</td>");
		emailBody.append("<td>").append(examResult.getTotalCorrectAnswers()).append("</td>");
		emailBody.append("<td>").append(examResult.getTotalWrongAnswers()).append("</td>");
		emailBody.append("</tr>");

		emailBody.append("</table>");

		emailBody.append("<p><b>").append("Total Score: " + examResult.getScore() + "/" + examResult.getTotalMarks())
				.append("</b></p>");
		emailBody.append("<p><b>").append("Percentage: " + examResult.getPercentage() + "%</b></p>");
		if (resultMessage.equals(ExamSubmissionMessage.CONGRATULATIONS_PASS.value())) {
			emailBody.append("<p><span style='color: green;'><b>").append("Result: " + examResult.getResultStatus())
					.append("</b></span></p>");
		} else {
			emailBody.append("<p><span style='color: red;'><b>").append("Result: " + examResult.getResultStatus())
					.append("</b></span></p>");
		}

		emailBody.append("<p>Best Regards,<br/>" + universityName + "</p>");

		emailBody.append("</body></html>");

		return emailBody.toString();
	}

}
