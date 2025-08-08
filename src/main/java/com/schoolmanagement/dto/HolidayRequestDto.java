package com.schoolmanagement.dto;

import lombok.Data;

@Data
public class HolidayRequestDto {

	private String date; // local date format
	private String name; // Name of the holiday (e.g., New Year, Independence Day)
	private String description; // Optional description of the holiday

}
