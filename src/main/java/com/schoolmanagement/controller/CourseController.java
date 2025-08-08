package com.schoolmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.dto.AddCourseRequest;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.CourseResponseDto;
import com.schoolmanagement.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/course")
//@CrossOrigin(origins = "http://localhost:3000")
public class CourseController {

	@Autowired
	private CourseService courseService;

	@PostMapping("/add")
	@Operation(summary = "Api to add course")
	public ResponseEntity<CommonApiResponse> addCourse(@RequestBody AddCourseRequest request) {
		return courseService.addCourse(request);
	}

	@PutMapping("/update")
	@Operation(summary = "Api to update course")
	public ResponseEntity<CommonApiResponse> updateCourse(@RequestBody AddCourseRequest course) {
		return courseService.updateCourse(course);
	}

	@GetMapping("/fetch/all")
	@Operation(summary = "Api to fetch all course")
	public ResponseEntity<CourseResponseDto> fetchAllCourse() {
		return courseService.fetchAllCourse();
	}

	@GetMapping("/fetch/all/grade-wise")
	@Operation(summary = "Api to fetch all course by grades")
	public ResponseEntity<CourseResponseDto> fetchAllCoursebyGrade(@RequestParam("gradeId") int gradeId) {
		return courseService.fetchAllCourseByGrade(gradeId);
	}
	
	@GetMapping("/fetch/id")
	@Operation(summary = "Api to fetch all course by grades")
	public ResponseEntity<CourseResponseDto> fetchCourseById(@RequestParam("courseId") int courseId) {
		return courseService.fetchCourseById(courseId);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Api to delete course")
	public ResponseEntity<CommonApiResponse> deleteCourse(@RequestParam("courseId") int courseId) {
		return courseService.deleteCourse(courseId);
	}
	
}
