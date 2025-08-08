package com.schoolmanagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Attendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private String date; // YYYY-MM-DD

	private String clockIn; // Clock-in time, time in millis
	private String clockOut; // Clock-out time, time in millis
	private double totalHoursWorked; // Calculated total hours worked in the day

//	com.hrms.utility.Constants.AttendanceStatus  refer this
	private String status;
	// Present, Half Day based on employee clock in
	// Absent, Paid Leave, Loss of Pay -- by manager
	// Leave Applied -> if no action taken by manager

	private String workingStatus; // Working, Holiday

	// here we can add LEave Request Reason
	// and also Attendance Regularize reason
	private String reason;

	// Metadata
	@ManyToOne
	@JoinColumn(name = "user_added_by", nullable = false)
	private User addedBy; // "Employee" for self-clock-in or manager's ID if modified

	@ManyToOne
	@JoinColumn(name = "user_last_updated_by", nullable = false)
	private User lastUpdatedBy; // Tracks the person updating the record

	private String lastUpdatedDate; // time in millis
	
	@OneToOne
	@JoinColumn(name = "leave_request_id", unique = true)
	private LeaveRequest leaveRequest;


}
