package com.schoolmanagement.dto;

import org.springframework.beans.BeanUtils;

import com.schoolmanagement.entity.User;

import lombok.Data;

@Data
public class RegisterUserRequestDto {

	private String firstName;

	private String lastName;

	private String emailId;

	private String password;

	private String phoneNo;

	private String role;

	private int batchId;

	private int userId;
	// admin user id if Teacher registration
	// teacher user id if Student registration

	public static User toUserEntity(RegisterUserRequestDto registerUserRequestDto) {
		User user = new User();
		BeanUtils.copyProperties(registerUserRequestDto, user);
		return user;
	}

}
