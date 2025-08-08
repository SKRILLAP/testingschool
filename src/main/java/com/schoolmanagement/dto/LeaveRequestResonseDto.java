package com.schoolmanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.schoolmanagement.entity.LeaveRequest;

import lombok.Data;

@Data
public class LeaveRequestResonseDto extends CommonApiResponse {

	private List<LeaveRequest> requests = new ArrayList<>();
	
}
