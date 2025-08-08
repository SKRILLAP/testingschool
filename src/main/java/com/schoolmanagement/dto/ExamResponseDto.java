package com.schoolmanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.schoolmanagement.entity.Exam;


public class ExamResponseDto extends CommonApiResponse {
	
	private List<Exam> exams = new ArrayList<>();

	public List<Exam> getExams() {
		return exams;
	}

	public void setExams(List<Exam> exams) {
		this.exams = exams;
	}

}
