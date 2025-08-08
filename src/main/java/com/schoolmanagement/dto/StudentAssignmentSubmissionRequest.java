package com.schoolmanagement.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class StudentAssignmentSubmissionRequest {

	private int id;

	private MultipartFile submissionFile; // pdf file

}
