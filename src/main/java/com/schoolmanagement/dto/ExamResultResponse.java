package com.schoolmanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.schoolmanagement.entity.ExamResult;

public class ExamResultResponse extends CommonApiResponse {

	private List<ExamResult> results = new ArrayList<>();

	public List<ExamResult> getResults() {
		return results;
	}

	public void setResults(List<ExamResult> results) {
		this.results = results;
	}

}
