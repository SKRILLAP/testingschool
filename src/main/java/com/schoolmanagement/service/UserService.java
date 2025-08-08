package com.schoolmanagement.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;

import com.schoolmanagement.dao.BatchDao;
import com.schoolmanagement.dao.GradeDao;
import com.schoolmanagement.dao.UserDao;
import com.schoolmanagement.dao.UserDetailDao;
import com.schoolmanagement.dto.AddUserDetailRequest;
import com.schoolmanagement.dto.ChangePasswordRequest;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.RegisterUserRequestDto;
import com.schoolmanagement.dto.UserDto;
import com.schoolmanagement.dto.UserLoginRequest;
import com.schoolmanagement.dto.UserLoginResponse;
import com.schoolmanagement.dto.UserResponseDto;
import com.schoolmanagement.dto.UserStatusUpdateRequestDto;
import com.schoolmanagement.entity.Batch;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.entity.UserDetail;
import com.schoolmanagement.exception.UserSaveFailedException;
import com.schoolmanagement.utility.Constants.ActiveStatus;
import com.schoolmanagement.utility.Constants.IsShowChangePasswordPage;
import com.schoolmanagement.utility.Constants.UserRole;
import com.schoolmanagement.utility.JwtUtils;
import com.schoolmanagement.utility.StorageService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Component
@Transactional
public class UserService {

	private final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private BatchDao batchDao;

	@Autowired
	private GradeDao gradeDao;

	@Autowired
	private StorageService storageService;

	@Autowired
	private UserDetailDao userDetailDao;

	public ResponseEntity<CommonApiResponse> registerAdmin(RegisterUserRequestDto registerRequest) {

		LOG.info("Request received for Register Admin");

		CommonApiResponse response = new CommonApiResponse();

		if (registerRequest == null) {
			response.setResponseMessage("user is null");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (registerRequest.getEmailId() == null || registerRequest.getPassword() == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User existingUser = this.userDao.findByEmailIdAndStatus(registerRequest.getEmailId(),
				ActiveStatus.ACTIVE.value());

		if (existingUser != null) {
			response.setResponseMessage("User already register with this Email");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = RegisterUserRequestDto.toUserEntity(registerRequest);

		user.setRole(UserRole.ROLE_ADMIN.value());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setStatus(ActiveStatus.ACTIVE.value());

		existingUser = this.userDao.save(user);

		if (existingUser == null) {
			response.setResponseMessage("failed to register admin");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		response.setResponseMessage("Admin registered Successfully");
		response.setSuccess(true);

		LOG.info("Response Sent!!!");

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> registerUser(RegisterUserRequestDto request) {

		LOG.info("Received request for register user");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("user is null");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User existingUser = this.userDao.findByEmailIdAndStatus(request.getEmailId(), ActiveStatus.ACTIVE.value());

		if (existingUser != null) {
			response.setResponseMessage("User with this Email Id already resgistered!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getRole() == null) {
			response.setResponseMessage("bad request ,Role is missing");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getUserId() == 0) {
			response.setResponseMessage("bad request , Manager Id is missing");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		// manager is admin if Teacher registration
		// manager is teacher if Student registration
		// this we have added bcoz of below
		// if user initiates for Leave of regularization then request will go to the
		// manager
		User manager = this.userDao.findById(request.getUserId()).orElse(null);

		if (manager == null) {
			response.setResponseMessage("Manager not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Batch batch = null;

		User user = RegisterUserRequestDto.toUserEntity(request);

		if (request.getRole().equals(UserRole.ROLE_STUDENT.value())) {

			if (request.getBatchId() == 0) {
				response.setResponseMessage("bad request ,Batch Id missing");
				response.setSuccess(false);

				return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
			}

			batch = this.batchDao.findById(request.getBatchId()).orElse(null);

			if (batch == null) {
				response.setResponseMessage("bad request ,Batch Id missing");
				response.setSuccess(false);

				return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
			}

			user.setBatch(batch);
		}

		String encodedPassword = passwordEncoder.encode("123456");

		user.setManager(manager);
		user.setStatus(ActiveStatus.ACTIVE.value());
		user.setPassword(encodedPassword);
		existingUser = this.userDao.save(user);

		if (existingUser == null) {
			throw new UserSaveFailedException("Registration Failed because of Technical issue:(");
		}

		response.setResponseMessage("User registered Successfully");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserResponseDto> addUserDetail(AddUserDetailRequest request) {

		LOG.info("Request received for add the user detail!!");

		String currentTime = String
				.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		UserResponseDto response = new UserResponseDto();

		if (request == null) {
			response.setResponseMessage("user is null");
			response.setSuccess(false);

			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getUserId() == 0) {
			response.setResponseMessage("missing user id");
			response.setSuccess(false);

			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		User user = this.userDao.findById(request.getUserId()).orElse(null);

		if (user == null) {
			response.setResponseMessage("User not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		String profileImage = this.storageService.store(request.getProfileImage());
		String governmentProof = this.storageService.store(request.getGovernmentProofFileImage());

		UserDetail userDetail = new UserDetail();

		userDetail.setFirstName(request.getFirstName());
		userDetail.setLastName(request.getLastName());
		userDetail.setEmailId(request.getEmailId());
		userDetail.setPhoneNo(request.getPhoneNo());
		userDetail.setGender(request.getGender());
		userDetail.setDateOfBirth(request.getDateOfBirth());
		userDetail.setBloodGroup(request.getBloodGroup());

		// Address Information
		userDetail.setPermanentAddress(request.getPermanentAddress());
		userDetail.setCurrentAddress(request.getCurrentAddress());
		userDetail.setCity(request.getCity());
		userDetail.setState(request.getState());
		userDetail.setPostalCode(request.getPostalCode());

		// Emergency Contact
		userDetail.setEmergencyContactName(request.getEmergencyContactName());
		userDetail.setEmergencyContactPhone(request.getEmergencyContactPhone());
		userDetail.setEmergencyContactRelation(request.getEmergencyContactRelation());

		// Document paths (Assuming you have logic to upload MultipartFile and return
		// the file name or path)
		userDetail.setProfileImage(profileImage); // from your upload logic
		userDetail.setGovernmentProofFileImage(governmentProof); // from your upload logic
		userDetail.setCreatedDate(currentTime); // assuming this is a field in your entity
		userDetail.setUser(user);

		UserDetail savedUserDetail = this.userDetailDao.save(userDetail);

		if (savedUserDetail == null) {
			response.setResponseMessage("User not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<UserResponseDto>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		User updatedUser = this.userDao.findById(request.getUserId()).orElse(user);

		List<UserDto> userDtos = new ArrayList<>();

		UserDto dto = UserDto.toUserDtoEntity(updatedUser);

		userDtos.add(dto);

		response.setUsers(userDtos);
		response.setResponseMessage("User Detail Updated successful!!!");
		response.setSuccess(true);

		LOG.info("Response Sent!!!");

		return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserLoginResponse> login(UserLoginRequest loginRequest) {

		LOG.info("Received request for User Login");

		UserLoginResponse response = new UserLoginResponse();

		if (loginRequest == null) {
			response.setResponseMessage("Missing Input");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String jwtToken = null;
		User user = null;

		user = this.userDao.findByEmailId(loginRequest.getEmailId());

		if (user == null) {
			response.setResponseMessage("User with this Email Id not registered in System!!!");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(user.getRole()));

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmailId(),
					loginRequest.getPassword(), authorities));
		} catch (Exception ex) {
			response.setResponseMessage("Invalid email or password.");
			response.setSuccess(false);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		jwtToken = jwtUtils.generateToken(loginRequest.getEmailId());

		if (!user.getStatus().equals(ActiveStatus.ACTIVE.value())) {
			response.setResponseMessage("User is not active");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		UserDto userDto = UserDto.toUserDtoEntity(user);

		// user is authenticated
		if (jwtToken != null) {

			if (user.getRole().equals(UserRole.ROLE_ADMIN.value())) {
				response.setShowChangePassword(IsShowChangePasswordPage.NO.value());
			} else {

				String userEnteredPassword = loginRequest.getPassword();

				String encodedStaticPassword = passwordEncoder.encode("123456");

				boolean passwordMatches = passwordEncoder.matches(userEnteredPassword, encodedStaticPassword);

				if (passwordMatches) {
					response.setShowChangePassword(IsShowChangePasswordPage.YES.value());
				} else {
					response.setShowChangePassword(IsShowChangePasswordPage.NO.value());
				}
			}

			response.setUser(userDto);
			response.setResponseMessage("Logged in sucessful");
			response.setSuccess(true);
			response.setJwtToken(jwtToken);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.OK);
		}

		else {
			response.setResponseMessage("Failed to login");
			response.setSuccess(false);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

	}

	public ResponseEntity<UserResponseDto> getUsersByRole(String role) {

		UserResponseDto response = new UserResponseDto();

		if (role == null) {
			response.setResponseMessage("missing role");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> users = new ArrayList<>();

		users = this.userDao.findByRoleAndStatus(role, ActiveStatus.ACTIVE.value());

		if (users.isEmpty()) {
			response.setResponseMessage("No Users Found");
			response.setSuccess(false);
		}

		List<UserDto> userDtos = new ArrayList<>();

		for (User user : users) {

			UserDto dto = UserDto.toUserDtoEntity(user);

			userDtos.add(dto);

		}

		response.setUsers(userDtos);
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateUserStatus(UserStatusUpdateRequestDto request) {

		LOG.info("Received request for updating the user status");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("bad request, missing data");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getUserId() == 0) {
			response.setResponseMessage("bad request, user id is missing");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = null;
		user = this.userDao.findById(request.getUserId()).orElse(null);

		user.setStatus(request.getStatus());

		User updatedUser = this.userDao.save(user);

		if (updatedUser == null) {
			throw new UserSaveFailedException("Failed to update the User status");
		}

		response.setResponseMessage("User " + request.getStatus() + " Successfully!!!");
		response.setSuccess(true);
		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<UserResponseDto> getUserById(int userId) {

		UserResponseDto response = new UserResponseDto();

		if (userId == 0) {
			response.setResponseMessage("Invalid Input");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> users = new ArrayList<>();

		User user = this.userDao.findById(userId).orElse(null);
		users.add(user);

		if (users.isEmpty()) {
			response.setResponseMessage("No Users Found");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
		}

		List<UserDto> userDtos = new ArrayList<>();

		for (User u : users) {

			UserDto dto = UserDto.toUserDtoEntity(u);

			userDtos.add(dto);

		}

		response.setUsers(userDtos);
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deleteUserById(int userId) {

		CommonApiResponse response = new CommonApiResponse();

		if (userId == 0) {
			response.setResponseMessage("user id missing");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = this.userDao.findById(userId).orElse(null);

		if (user == null) {
			response.setResponseMessage("User not found");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		user.setStatus(ActiveStatus.DEACTIVATED.value());

		User updatedUser = this.userDao.save(user);

		if (updatedUser == null) {
			response.setResponseMessage("Failed to Delete the User");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.setResponseMessage("User Deleted Successful!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> transferStudentToAnotherBatch(Integer fromBatchId, Integer toBatchId) {

		CommonApiResponse response = new CommonApiResponse();

		if (fromBatchId == null || toBatchId == null) {
			response.setResponseMessage("batch id missing");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (fromBatchId == toBatchId) {
			response.setResponseMessage("From Batch Id and To Batch are same!!!");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Batch fromBatch = this.batchDao.findById(fromBatchId).orElse(null);

		if (fromBatch == null) {
			response.setResponseMessage("from batch not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Batch toBatch = this.batchDao.findById(toBatchId).orElse(fromBatch);

		if (toBatch == null) {
			response.setResponseMessage("To batch not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> students = this.userDao.findByRoleAndStatus(UserRole.ROLE_STUDENT.value(),
				ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(students)) {
			response.setResponseMessage("Students not found in select Batch!!!");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
		}

		for (User student : students) {
			student.setBatch(toBatch);

			this.userDao.save(student); // update student
		}

		response.setResponseMessage("Students transfered to Selected Batch!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deactivateBatchStudent(Integer batchId) {

		CommonApiResponse response = new CommonApiResponse();

		if (batchId == null) {
			response.setResponseMessage("batch id missing");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Batch batch = this.batchDao.findById(batchId).orElse(null);

		if (batch == null) {
			response.setResponseMessage("batch not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> students = this.userDao.findByRoleAndBatchAndStatus(UserRole.ROLE_STUDENT.value(), batch,
				ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(students)) {
			response.setResponseMessage("Students not found in Selected Batch!!!");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
		}

		for (User student : students) {
			student.setStatus(ActiveStatus.DEACTIVATED.value());

			this.userDao.save(student); // update student
		}

		response.setResponseMessage("Students Deactivated from Selected Batch!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserLoginResponse> changePassword(ChangePasswordRequest request) {

		LOG.info("Received request for changing the user passord");

		UserLoginResponse response = new UserLoginResponse();

		if (request == null) {
			response.setResponseMessage("Missing request body");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getUserId() == null || request.getUserId() == 0 || request.getNewPassword() == null
				|| request.getCurrentPassword() == null) {
			response.setResponseMessage("Missing input");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = this.userDao.findById(request.getUserId()).orElse(null);

		if (user == null) {
			response.setResponseMessage("User Not Found!!!");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String userRawPassword = request.getCurrentPassword();

		String encodedStoredPassord = user.getPassword();

		boolean isPasswordMatching = passwordEncoder.matches(userRawPassword, encodedStoredPassord);

		if (!isPasswordMatching) {
			response.setResponseMessage("Failed to verify current password!!!");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String newEncodedPassword = passwordEncoder.encode(request.getNewPassword());

		user.setPassword(newEncodedPassword);

		User updatedUser = this.userDao.save(user);

		if (updatedUser == null) {
			response.setResponseMessage("Failed to change password!!!");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			response.setResponseMessage("Password Changed Successful!!!");
			response.setSuccess(true);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.OK);
		}

	}

	public ResponseEntity<UserResponseDto> getStudentsByGrade(int gradeId) {

		UserResponseDto response = new UserResponseDto();

		if (gradeId == 0) {
			response.setResponseMessage("missing batch id!!!");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> users = new ArrayList<>();

		Grade grade = this.gradeDao.findById(gradeId).orElse(null);

		if (grade == null) {
			response.setResponseMessage("Grade not found!!!");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		users = this.userDao.findByRoleAndGradeAndStatus(UserRole.ROLE_STUDENT.value(), grade,
				ActiveStatus.ACTIVE.value());

		if (users.isEmpty()) {
			response.setResponseMessage("No Users Found");
			response.setSuccess(false);
		}

		List<UserDto> userDtos = new ArrayList<>();

		for (User user : users) {

			UserDto dto = UserDto.toUserDtoEntity(user);

			userDtos.add(dto);

		}

		response.setUsers(userDtos);
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserResponseDto> getStudentsByBatch(int batchId) {

		UserResponseDto response = new UserResponseDto();

		if (batchId == 0) {
			response.setResponseMessage("missing batch id!!!");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> users = new ArrayList<>();

		Batch batch = this.batchDao.findById(batchId).orElse(null);

		if (batch == null) {
			response.setResponseMessage("Batch not found!!!");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		users = this.userDao.findByRoleAndBatchAndStatus(UserRole.ROLE_STUDENT.value(), batch,
				ActiveStatus.ACTIVE.value());

		if (users.isEmpty()) {
			response.setResponseMessage("No Users Found");
			response.setSuccess(false);
		}

		List<UserDto> userDtos = new ArrayList<>();

		for (User user : users) {

			UserDto dto = UserDto.toUserDtoEntity(user);

			userDtos.add(dto);

		}

		response.setUsers(userDtos);
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
	}

	public void fetchUserProfilePic(String userProfilePic, HttpServletResponse resp) {
		LOG.info("Received request to fetch user profile pic!!!");
		Resource resource = storageService.load(userProfilePic);
		if (resource != null) {
			try (InputStream in = resource.getInputStream()) {
				ServletOutputStream out = resp.getOutputStream();
				FileCopyUtils.copy(in, out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		LOG.info("Response sent!!!");
	}

	public ResponseEntity<Resource> downloadDocumemt(String documentFileName, HttpServletResponse response) {

		Resource resource = storageService.load(documentFileName);
		if (resource == null) {
			// Handle file not found
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Document\"")
				.body(resource);

	}

}
