package com.schoolmanagement.dto;

import lombok.Data;

@Data
public class AddLeaveRequestDto {

	private int userId; // user id primary key

	private String date;

	private String reason;

}
