package com.schoolmanagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class StudentAssignmentSubmission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "assignment_id")
	private Assignment assignment;

	@ManyToOne
	@JoinColumn(name = "student_id")
	private User student;

	private String submittedAt; // in millis

	private String submissionFileName; // pdf file name which is uploaded by the Student

	private String remarks; // Teacher's remarks after checking

	private String status; // refer com.schoolmanagement.utility.Constants.AssignmentSubmissionStatus

	private String grade; // refer com.schoolmanagement.utility.Constants.AssignmentGrade

}
