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
public class LeaveRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user; // user can be student or teacher applying for leave

	private String date;  // each entry will be there for 1 day only it will be easy to handle the scenarios

	private String reason; // Reason for leave

	private String status;
	// Pending (default), Approved, Rejected

	private String comment; // Comments from teacher or admin

	@ManyToOne
	@JoinColumn(name = "user_manager_id", nullable = false)
	private User manager; // Manager (Admin for teacher or Teacher for student) reviewing the leave

	private String createdDate; // Timestamp for leave application
	private String lastUpdatedDate; // Timestamp for status updates

}
