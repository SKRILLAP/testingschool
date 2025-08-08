package com.schoolmanagement.dto;

import java.util.List;

import com.schoolmanagement.entity.Batch;
import com.schoolmanagement.entity.Course;
import com.schoolmanagement.entity.Grade;

public class GradeBatchResponse extends CommonApiResponse {

	private List<Grade> grades;

	private List<Batch> batches;

	private List<Course> courses;

	public List<Grade> getGrades() {
		return grades;
	}

	public void setGrades(List<Grade> grades) {
		this.grades = grades;
	}

	public List<Batch> getBatches() {
		return batches;
	}

	public void setBatches(List<Batch> batches) {
		this.batches = batches;
	}

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

}
