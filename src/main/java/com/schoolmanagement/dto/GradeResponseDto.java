package com.schoolmanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.schoolmanagement.entity.Grade;

public class GradeResponseDto extends CommonApiResponse {

	private List<Grade> grades = new ArrayList<>();

	public List<Grade> getGrades() {
		return grades;
	}

	public void setGrades(List<Grade> grades) {
		this.grades = grades;
	}

}
