package com.schoolmanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.schoolmanagement.dao.AttendanceDao;
import com.schoolmanagement.dao.LeaveRequestDao;
import com.schoolmanagement.dao.UserDao;
import com.schoolmanagement.dto.AddLeaveRequestDto;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.LeaveRequestResonseDto;
import com.schoolmanagement.entity.Attendance;
import com.schoolmanagement.entity.LeaveRequest;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.utility.Constants.AttendanceStatus;
import com.schoolmanagement.utility.Constants.LeaveRequestStatus;

@Service
public class LeaveRequestService {

	@Autowired
	private LeaveRequestDao leaveRequestDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AttendanceDao attendanceDao;

	public ResponseEntity<CommonApiResponse> addLeaveRequest(AddLeaveRequestDto addLeaveRequestDto) {
		CommonApiResponse response = new CommonApiResponse();

		User user = userDao.findById(addLeaveRequestDto.getUserId()).orElse(null);
		if (user == null) {
			response.setSuccess(false);
			response.setResponseMessage("Employee not found!");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		Attendance attendance = this.attendanceDao.findByUserAndDate(user, addLeaveRequestDto.getDate());

		if (attendance.getLeaveRequest() != null) {
			response.setSuccess(false);
			response.setResponseMessage("Leave Request is already applied for this Date!!!");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		LeaveRequest leaveRequest = new LeaveRequest();
		leaveRequest.setUser(user);
		leaveRequest.setDate(addLeaveRequestDto.getDate());
		leaveRequest.setReason(addLeaveRequestDto.getReason());
		leaveRequest.setStatus(LeaveRequestStatus.PENDING.value());
		leaveRequest.setCreatedDate(String.valueOf(System.currentTimeMillis()));
		leaveRequest.setManager(user.getManager());

		LeaveRequest appliedLeaveRequest = leaveRequestDao.save(leaveRequest);

		attendance.setReason(addLeaveRequestDto.getReason());
		attendance.setLeaveRequest(appliedLeaveRequest);

		this.attendanceDao.save(attendance);

		response.setSuccess(true);
		response.setResponseMessage("Leave request submitted successfully!!!");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<CommonApiResponse> updateLeaveStatus(int id, String status, String managerComments) {
		CommonApiResponse response = new CommonApiResponse();

		LeaveRequest leaveRequest = leaveRequestDao.findById(id).orElse(null);
		if (leaveRequest == null) {
			response.setSuccess(false);
			response.setResponseMessage("Leave request not found!");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		leaveRequest.setStatus(status);
		leaveRequest.setComment(managerComments);
		leaveRequest.setLastUpdatedDate(String.valueOf(System.currentTimeMillis()));

		leaveRequestDao.save(leaveRequest);

		// If approved, update attendance
		if (status.equals(LeaveRequestStatus.APPROVED.value())) {
			updateAttendanceForLeave(leaveRequest);
		}

		response.setSuccess(true);
		response.setResponseMessage("Leave request " + status.toLowerCase() + " successfully!");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<LeaveRequestResonseDto> getLeaveRequestsForEmployee(int userId) {
		LeaveRequestResonseDto response = new LeaveRequestResonseDto();

		User user = userDao.findById(userId).orElse(null);
		if (user == null) {
			response.setSuccess(false);
			response.setResponseMessage("Employee not found!");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		List<LeaveRequest> leaveRequests = leaveRequestDao.findByUser(user);

		if (CollectionUtils.isEmpty(leaveRequests)) {
			response.setSuccess(false);
			response.setResponseMessage("Leave Request not found!!");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}

		response.setRequests(leaveRequests);
		response.setSuccess(true);
		response.setResponseMessage("Leave requests fetched successfully!");
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<LeaveRequestResonseDto> getLeaveRequestsForManager(int managerId) {
		LeaveRequestResonseDto response = new LeaveRequestResonseDto();

		User manager = userDao.findById(managerId).orElse(null);
		if (manager == null) {
			response.setSuccess(false);
			response.setResponseMessage("Manager not found!");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		List<LeaveRequest> leaveRequests = leaveRequestDao.findByManager(manager);

		if (CollectionUtils.isEmpty(leaveRequests)) {
			response.setSuccess(false);
			response.setResponseMessage("Leave Request not found!!");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}

		response.setRequests(leaveRequests);
		response.setSuccess(true);
		response.setResponseMessage("Leave requests fetched successfully!");
		return ResponseEntity.ok(response);
	}

	private void updateAttendanceForLeave(LeaveRequest leaveRequest) {
		User employee = leaveRequest.getUser();
		String leaveDate = leaveRequest.getDate();

		Attendance attendance = attendanceDao.findByUserAndDate(employee, leaveDate);
		attendance.setStatus(AttendanceStatus.ABSENT.value());
		attendance.setReason(leaveRequest.getReason());

		attendanceDao.save(attendance);
		
	}

}
