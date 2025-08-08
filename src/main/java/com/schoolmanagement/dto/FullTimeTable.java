package com.schoolmanagement.dto;

import java.util.Map;

import com.schoolmanagement.entity.Batch;
import com.schoolmanagement.entity.Grade;

public class FullTimeTable {

	private Batch batch;

	private Grade grade;

	// Monday 9am-10am Java, Teacher1
	private Map<String, Map<String, TeacherCourse>> timetable;

	public Batch getBatch() {
		return batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	public Grade getGrade() {
		return grade;
	}

	public void setGrade(Grade grade) {
		this.grade = grade;
	}

	public Map<String, Map<String, TeacherCourse>> getTimetable() {
		return timetable;
	}

	public void setTimetable(Map<String, Map<String, TeacherCourse>> timetable) {
		this.timetable = timetable;
	}

}
