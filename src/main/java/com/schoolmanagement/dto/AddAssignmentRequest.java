package com.schoolmanagement.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class AddAssignmentRequest {

	private int gradeId;
	
	private int courseId;

	private String name;

	private String description;

	private String note;

	private String deadLine; // localdate format

	private MultipartFile assignmentDoc;
	
	private int teacherId;
	

}
