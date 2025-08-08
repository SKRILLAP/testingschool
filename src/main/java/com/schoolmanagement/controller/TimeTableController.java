package com.schoolmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.dto.AddTimeTableRequest;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.TimeTableResponse;
import com.schoolmanagement.service.TimeTableService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/timetable")
//@CrossOrigin(origins = "http://localhost:3000")
public class TimeTableController {

	@Autowired
	private TimeTableService timeTableService;

	@PostMapping("/add")
	@Operation(summary = "Api to add timetable")
	public ResponseEntity<CommonApiResponse> addTimeTable(@RequestBody AddTimeTableRequest request) {
		return timeTableService.addTimeTable(request);
	}

	@GetMapping("/fetch/batch-wise")
	@Operation(summary = "Api to add timetable")
	public ResponseEntity<TimeTableResponse> fetchTimeTable(@RequestParam("batchId") Integer batchId) {
		return timeTableService.fetchTimeTable(batchId);
	}

	@GetMapping("/fetch/batch-wise/course-wise")
	@Operation(summary = "Api to fetch timetable by batch wise and course wise")
	public ResponseEntity<TimeTableResponse> fetchTimeTableByBatchCourse(@RequestParam("batchId") Integer batchId,
			@RequestParam("courseId") Integer courseId) {
		return timeTableService.fetchTimeTableByBatchAndCourse(batchId, courseId);
	}
	
	@GetMapping("/fetch/batch-wise/teacher-wise")
	@Operation(summary = "Api to fetch timetable by batch wise and course wise")
	public ResponseEntity<TimeTableResponse> fetchTimeTableByBatchTeacher(@RequestParam("batchId") Integer batchId,
			@RequestParam("teacherId") Integer teacherId) {
		return timeTableService.fetchTimeTableByBatchTeacher(batchId, teacherId);
	}
	
	@GetMapping("/fetch/batch/course/teacher-wise")
	@Operation(summary = "Api to fetch timetable by batch, course and teacher wise")
	public ResponseEntity<TimeTableResponse> fetchTimeTableByBatchTeacherAndCourse(@RequestParam("batchId") Integer batchId, @RequestParam("courseId") Integer courseId,
			@RequestParam("teacherId") Integer teacherId) {
		return timeTableService.fetchTimeTableByBatchTeacherAndCourse(batchId, courseId, teacherId);
	}
	
	@GetMapping("/fetch/teacher-wise")
	@Operation(summary = "Api to fetch timetable by teacher")
	public ResponseEntity<TimeTableResponse> fetchTimeTableByTeacher(@RequestParam("teacherId") Integer teacherId) {
		return timeTableService.fetchTimeTableByTeacher(teacherId);
	}

}
