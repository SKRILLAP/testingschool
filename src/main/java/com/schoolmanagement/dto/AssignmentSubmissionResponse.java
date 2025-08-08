package com.schoolmanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.schoolmanagement.entity.StudentAssignmentSubmission;

import lombok.Data;

@Data
public class AssignmentSubmissionResponse extends CommonApiResponse {

	private List<StudentAssignmentSubmission> assignmentSubmissions = new ArrayList<>();

}
