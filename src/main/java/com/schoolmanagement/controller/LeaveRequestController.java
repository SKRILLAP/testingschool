package com.schoolmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.dto.AddLeaveRequestDto;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.LeaveRequestResonseDto;
import com.schoolmanagement.service.LeaveRequestService;
import com.schoolmanagement.utility.Constants.LeaveRequestStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("api/leave/request")
//@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping("/add")
    public ResponseEntity<CommonApiResponse> addLeaveRequest(@Valid @RequestBody AddLeaveRequestDto addLeaveRequestDto) {
        return leaveRequestService.addLeaveRequest(addLeaveRequestDto);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<CommonApiResponse> approveLeaveRequest(
            @PathVariable int id,
            @RequestParam("comment") @NotBlank(message = "Manager comments are required.") String managerComments) {
        return leaveRequestService.updateLeaveStatus(id, LeaveRequestStatus.APPROVED.value(), managerComments);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<CommonApiResponse> rejectLeaveRequest(
            @PathVariable int id,
            @RequestParam("comment") @NotBlank(message = "Manager comments are required.") String managerComments) {
        return leaveRequestService.updateLeaveStatus(id, LeaveRequestStatus.REJECTED.value(), managerComments);
    }

    @GetMapping("/user/{employeeId}")
    public ResponseEntity<LeaveRequestResonseDto> getLeaveRequestsForEmployee(@PathVariable("employeeId") int employeeId) {
        return leaveRequestService.getLeaveRequestsForEmployee(employeeId);
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<LeaveRequestResonseDto> getLeaveRequestsForManager(@PathVariable int managerId) {
        return leaveRequestService.getLeaveRequestsForManager(managerId);
    }
}


