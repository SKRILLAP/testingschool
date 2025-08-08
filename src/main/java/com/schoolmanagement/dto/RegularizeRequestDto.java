package com.schoolmanagement.dto;

import lombok.Data;

@Data
public class RegularizeRequestDto {

	private int userId;

	private String date;

	private String clockInTime;

	private String clockOutTime;

	private String reason;

	private int updatedByUserId;

}
