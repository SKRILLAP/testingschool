package com.schoolmanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.schoolmanagement.entity.Holiday;

import lombok.Data;

@Data
public class HolidayResponseDto extends CommonApiResponse {

	private List<Holiday> holidays = new ArrayList<>();
	
}
