package com.schoolmanagement.dto;

import org.springframework.beans.BeanUtils;

import com.schoolmanagement.entity.Batch;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.entity.UserDetail;

import lombok.Data;

@Data
public class UserDto {

	private int id;

	private String firstName;

	private String lastName;

	private String emailId;

	private String phoneNo;

	private String role;

	private Batch batch;

	private String status;

	private UserDetail userDetail;

	private User manager;

	public static UserDto toUserDtoEntity(User user) {
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(user, userDto);
		return userDto;
	}

}
