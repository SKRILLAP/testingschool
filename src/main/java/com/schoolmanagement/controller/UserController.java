package com.schoolmanagement.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lowagie.text.DocumentException;
import com.schoolmanagement.dto.AddUserDetailRequest;
import com.schoolmanagement.dto.ChangePasswordRequest;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.RegisterUserRequestDto;
import com.schoolmanagement.dto.UserLoginRequest;
import com.schoolmanagement.dto.UserLoginResponse;
import com.schoolmanagement.dto.UserResponseDto;
import com.schoolmanagement.dto.UserStatusUpdateRequestDto;
import com.schoolmanagement.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/user")
//@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

	@Autowired
	private UserService userService;

	// RegisterUserRequestDto, we will set only email, password & role from UI
	@PostMapping("/admin/register")
	@Operation(summary = "Api to register Admin")
	public ResponseEntity<CommonApiResponse> registerAdmin(@RequestBody RegisterUserRequestDto request) {
		return userService.registerAdmin(request);
	}

	// for student and teacher register
	@PostMapping("register")
	@Operation(summary = "Api to register user")
	public ResponseEntity<CommonApiResponse> registerUser(@RequestBody RegisterUserRequestDto request) {
		return this.userService.registerUser(request);
	}
	
	@PostMapping("detail/add")
	@Operation(summary = "Api to add the user detail")
	public ResponseEntity<UserResponseDto> addUserDetail(AddUserDetailRequest request) {
		return this.userService.addUserDetail(request);
	}

	@PostMapping("login")
	@Operation(summary = "Api to login any User")
	public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
		return userService.login(userLoginRequest);
	}

	@GetMapping("/fetch/role-wise")
	@Operation(summary = "Api to get Users By Role")
	public ResponseEntity<UserResponseDto> fetchAllUsersByRole(@RequestParam("role") String role)
			throws JsonProcessingException {
		return userService.getUsersByRole(role);
	}

	@PutMapping("update/status")
	@Operation(summary = "Api to update the user status")
	public ResponseEntity<CommonApiResponse> updateUserStatus(@RequestBody UserStatusUpdateRequestDto request) {
		return userService.updateUserStatus(request);
	}

	@GetMapping("/fetch/user-id")
	@Operation(summary = "Api to get User Detail By User Id")
	public ResponseEntity<UserResponseDto> fetchUserById(@RequestParam("userId") int userId) {
		return userService.getUserById(userId);
	}
	
	@DeleteMapping("/delete/user-id")
	@Operation(summary = "Api to delete the user by ID")
	public ResponseEntity<CommonApiResponse> deleteUserById(@RequestParam("userId") int userId) {
		return userService.deleteUserById(userId);
	}
	
	@PutMapping("student/batch/transfer")
	@Operation(summary = "Api to transfer student to another bacth")
	public ResponseEntity<CommonApiResponse> transferStudentToAnotherBatch(@RequestParam("fromBatchId") int fromBatchId, @RequestParam("toBatchId") int toBatchId) {
		return userService.transferStudentToAnotherBatch(fromBatchId, toBatchId);
	}
	
	@PutMapping("student/batch/deactivate")
	@Operation(summary = "Api to deactivate the student from batch")
	public ResponseEntity<CommonApiResponse> deactivateBatchStudent(@RequestParam("batchId") int batchId) {
		return userService.deactivateBatchStudent(batchId);
	}
	
	@PostMapping("changePassword")
	@Operation(summary = "Api to change the user password")
	public ResponseEntity<UserLoginResponse> changePassword(@RequestBody ChangePasswordRequest request) {
		return userService.changePassword(request);
	}
	
	@GetMapping("/fetch/student/grade-wise")
	@Operation(summary = "Api to get Students by grade wise")
	public ResponseEntity<UserResponseDto> fetchStudentsByGrade(@RequestParam("gradeId") int gradeId) {
		return userService.getStudentsByGrade(gradeId);
	}
	
	@GetMapping("/fetch/student/batch-wise")
	@Operation(summary = "Api to get Students by grade wise")
	public ResponseEntity<UserResponseDto> fetchStudentsByBatch(@RequestParam("batchId") int batchId) {
		return userService.getStudentsByBatch(batchId);
	}
	
	@GetMapping(value = "/{userProfilePic}", produces = "image/*")
	@Operation(summary = "Api to fetch employee profile photo")
	public void fetchEmployeeProfilePic(@PathVariable("userProfilePic") String userProfilePic, HttpServletResponse resp) {
		this.userService.fetchUserProfilePic(userProfilePic, resp);
	}
	
	@GetMapping("/document/{documentFileName}/download")
	@Operation(summary = "Api for downloading the Employee document")
	public ResponseEntity<Resource> downloadDocumemt(@PathVariable("documentFileName") String documentFileName,
			HttpServletResponse response) throws DocumentException, IOException {
		return this.userService.downloadDocumemt(documentFileName, response);
	}


}
