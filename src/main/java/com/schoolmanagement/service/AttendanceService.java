package com.schoolmanagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.schoolmanagement.dao.AttendanceDao;
import com.schoolmanagement.dao.UserDao;
import com.schoolmanagement.dto.AttendanceResponseDto;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.RegularizeRequestDto;
import com.schoolmanagement.entity.Attendance;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.utility.Constants.AttendanceStatus;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AttendanceService {

	private final Logger LOG = LoggerFactory.getLogger(AttendanceService.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private AttendanceDao attendanceDao;

	@Value("${com.schoolmanagement.workingHour}")
	private int workingHour;

	public ResponseEntity<CommonApiResponse> clockIn(int userId) {

		LOG.info("Received request for employee clock in!");

		CommonApiResponse response = new CommonApiResponse();

		String currentTime = String
				.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		String todaysDay = LocalDate.now().toString();

		if (userId == 0) {
			response.setResponseMessage("user id is missing!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = this.userDao.findById(userId).orElse(null);

		if (user == null) {
			response.setResponseMessage("user not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Attendance attendance = this.attendanceDao.findByUserAndDate(user, todaysDay);

		if (attendance == null) {
			response.setResponseMessage("User Attendance not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (!StringUtils.isEmpty(attendance.getClockIn())) {
			response.setResponseMessage("user already clock in!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		attendance.setClockIn(currentTime);
		attendance.setLastUpdatedBy(user);
		attendance.setLastUpdatedDate(currentTime);

		Attendance updatedAttendance = this.attendanceDao.save(attendance);

		if (updatedAttendance == null) {
			response.setResponseMessage("Failed to clock in.");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		response.setResponseMessage("Clock In Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> clockOut(int userId) {

		LOG.info("Received request for user clock out!");

		CommonApiResponse response = new CommonApiResponse();

		String currentTime = String
				.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		String todaysDay = LocalDate.now().toString();

		if (userId == 0) {
			response.setResponseMessage("User ID is missing!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = this.userDao.findById(userId).orElse(null);

		if (user == null) {
			response.setResponseMessage("User not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Attendance attendance = this.attendanceDao.findByUserAndDate(user, todaysDay);

		if (attendance == null || StringUtils.isEmpty(attendance.getClockIn())) {
			response.setResponseMessage("Clock In record not found for the user today!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (!StringUtils.isEmpty(attendance.getClockOut())) {
			response.setResponseMessage("User already clocked out!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		attendance.setClockOut(currentTime);

		// Calculate total hours worked
		long clockInTime = Long.parseLong(attendance.getClockIn());
		long clockOutTime = Long.parseLong(currentTime);
		double totalHoursWorked = (clockOutTime - clockInTime) / (1000.0 * 60 * 60); // Convert milliseconds to hours

		attendance.setTotalHoursWorked(totalHoursWorked);

		int companyMandatoryWorkingHour = workingHour;

		double mandatoryWorkingHour = Double.parseDouble(String.valueOf(companyMandatoryWorkingHour));

		if (totalHoursWorked < mandatoryWorkingHour) {
			attendance.setStatus(AttendanceStatus.HALF_DAY_PRESENT.value());
		} else {
			attendance.setStatus(AttendanceStatus.PRESENT.value());
		}

		attendance.setLastUpdatedBy(user);
		attendance.setLastUpdatedDate(currentTime);

		Attendance updatedAttendance = this.attendanceDao.save(attendance);

		if (updatedAttendance == null) {
			response.setResponseMessage("Failed to clock out.");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		response.setResponseMessage("Clock Out Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> regularizeEmployeeAttendance(RegularizeRequestDto request) {

		LOG.info("Received request to regularize attendance for employee!");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getUserId() == 0 || request.getUpdatedByUserId() == 0) {
			response.setResponseMessage("User ID or Updated User ID is missing!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (StringUtils.isEmpty(request.getDate()) || StringUtils.isEmpty(request.getClockInTime())
				|| StringUtils.isEmpty(request.getClockOutTime())) {
			response.setResponseMessage("Date, clock-in time, or clock-out time is missing!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User employee = this.userDao.findById(request.getUserId()).orElse(null);
		User updatedUser = this.userDao.findById(request.getUpdatedByUserId()).orElse(null);

		if (employee == null) {
			response.setResponseMessage("Employee not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (updatedUser == null) {
			response.setResponseMessage("User performing the update not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Attendance attendance = this.attendanceDao.findByUserAndDate(employee, request.getDate());

		if (attendance == null) {
			response.setResponseMessage("Attendance record not found for user and date");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		// Update the attendance details
		attendance.setClockIn(request.getClockInTime());
		attendance.setClockOut(request.getClockOutTime());

		// Calculate total hours worked
		long clockInMillis = Long.parseLong(request.getClockInTime());
		long clockOutMillis = Long.parseLong(request.getClockOutTime());
		double totalHoursWorked = (clockOutMillis - clockInMillis) / (1000.0 * 60 * 60); // Convert milliseconds to
																							// hours
		attendance.setTotalHoursWorked(totalHoursWorked);

		// Set additional metadata
		attendance.setLastUpdatedBy(updatedUser);
		attendance.setLastUpdatedDate(String.valueOf(System.currentTimeMillis()));
		attendance.setReason("Regularized: " + request.getReason());

		int companyMandatoryWorkingHour = workingHour;

		double mandatoryWorkingHour = Double.parseDouble(String.valueOf(companyMandatoryWorkingHour));

		if (totalHoursWorked < mandatoryWorkingHour) {
			attendance.setStatus(AttendanceStatus.HALF_DAY_PRESENT.value());
		} else {
			attendance.setStatus(AttendanceStatus.PRESENT.value());
		}

		Attendance updatedAttendance = this.attendanceDao.save(attendance);

		if (updatedAttendance == null) {
			response.setResponseMessage("Failed to regularize attendance.");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		response.setResponseMessage("Attendance regularized successfully!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<AttendanceResponseDto> fetchEmployeeCurrentMonthAttendances(int userId) {

		LOG.info("Request received for fetching employee current month attendances");

		AttendanceResponseDto response = new AttendanceResponseDto();

		if (userId == 0) {
			response.setResponseMessage("employee id missing");
			response.setSuccess(false);

			return new ResponseEntity<AttendanceResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		User employeeUser = this.userDao.findById(userId).orElse(null);

		if (employeeUser == null) {
			response.setResponseMessage("employee not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<AttendanceResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		LocalDate todaysDate = LocalDate.now(); // YYYY-MM-DD I want only YYYY-MM- in string
		String formattedDate = todaysDate.format(DateTimeFormatter.ofPattern("yyyy-MM-"));

		List<Attendance> attendances = this.attendanceDao.findByUserAndDateContainingIgnoreCase(employeeUser,
				formattedDate);

		if (CollectionUtils.isEmpty(attendances)) {
			response.setResponseMessage("Employee Attendances not found for current month!!!");
			response.setSuccess(false);

			return new ResponseEntity<AttendanceResponseDto>(response, HttpStatus.OK);
		}

		response.setAttendances(attendances);
		response.setResponseMessage("Employee Attendance fetched Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<AttendanceResponseDto>(response, HttpStatus.OK);

	}

}
