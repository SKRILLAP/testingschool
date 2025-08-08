package com.schoolmanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.schoolmanagement.entity.Assignment;

import lombok.Data;

@Data
public class AssignmentResponse extends CommonApiResponse {

	private List<Assignment> assignments = new ArrayList<>();
	
}
