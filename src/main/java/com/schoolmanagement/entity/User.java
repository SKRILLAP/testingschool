package com.schoolmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String firstName;

	private String lastName;

	private String emailId;

	@JsonIgnore
	private String password;

	private String phoneNo;

	private String role;

	@ManyToOne
	@JoinColumn(name = "batch_id")
	private Batch batch;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private UserDetail userDetail;

	@ManyToOne
	@JoinColumn(name = "manager_id")
	private User manager; // here Manager can be Teacher for Student and Admin for Teacher
	// if role is Student then store Teacher here
	// if role is Teacher then store Admin here

	private String status;

}
