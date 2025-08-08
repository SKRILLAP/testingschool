package com.schoolmanagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class NoticeBoard {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String title;

	private String notice;

	private String addedTime;

	private String forDate;

	private String attachmentFileName;

	@ManyToOne
	@JoinColumn(name = "teacher_id")
	private User teacher; // teacher who will add this notice

	@ManyToOne
	@JoinColumn(name = "grade_id")
	private Grade grade; // notice for particular grade
	
	private String status;

}
