package com.schoolmanagement.dto;

public class AddCourseRequest {

	private int id;

	private int gradeId;

	private String name;

	private String description;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGradeId() {
		return gradeId;
	}

	public void setGradeId(int gradeId) {
		this.gradeId = gradeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static boolean validate(AddCourseRequest request) {

		if (request.getName() == null || request.getGradeId() == 0 || request.getDescription() == null) {
			return false;
		}

		return true;

	}

}
