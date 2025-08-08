package com.schoolmanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.schoolmanagement.entity.StudentAnswer;

public class QuestionAnswerResponseDto extends CommonApiResponse {

	List<StudentAnswer> questionAnswers = new ArrayList<>();

	public List<StudentAnswer> getQuestionAnswers() {
		return questionAnswers;
	}

	public void setQuestionAnswers(List<StudentAnswer> questionAnswers) {
		this.questionAnswers = questionAnswers;
	}

}
