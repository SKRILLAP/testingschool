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
public class Assignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "grade_id")
	private Grade grade;
	
	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;

	private String status;

	private String name;

	private String description;

	private String note;

	private String addedDateTime;

	private String deadLine; // localdate format
	
	private String assignmentDocFileName;

	@ManyToOne
	@JoinColumn(name = "teacher_id")
	private User teacher; // teacher who will add this assignment

}
