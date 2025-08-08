package com.schoolmanagement.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class AddNoticeRequest {

	private String title;

	private String notice;

	private String forDate;

	private MultipartFile attachmentFile; // pdf file from UI or client side

	private int teacherId;

	private int gradeId;

}
