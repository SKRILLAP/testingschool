package com.schoolmanagement.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.schoolmanagement.dao.CourseDao;
import com.schoolmanagement.dao.GradeDao;
import com.schoolmanagement.dto.AddCourseRequest;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.CourseResponseDto;
import com.schoolmanagement.entity.Course;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.exception.CourseSaveFailedException;
import com.schoolmanagement.exception.GradeSaveFailedException;
import com.schoolmanagement.utility.Constants.ActiveStatus;

import jakarta.transaction.Transactional;

@Component
@Transactional
public class CourseService {

	private final Logger LOG = LoggerFactory.getLogger(CourseService.class);

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private GradeDao gradeDao;

	public ResponseEntity<CommonApiResponse> addCourse(AddCourseRequest request) {

		LOG.info("Request received for add course");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || !AddCourseRequest.validate(request)) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Grade grade = this.gradeDao.findById(request.getGradeId()).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Course course = new Course();
		course.setName(request.getName());
		course.setDescription(request.getDescription());
		course.setGrade(grade);
		course.setStatus(ActiveStatus.ACTIVE.value());

		Course savedCourse = this.courseDao.save(course);

		if (savedCourse == null) {
			throw new CourseSaveFailedException("Failed to add course");
		}

		response.setResponseMessage("Course Added Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<CommonApiResponse> updateCourse(AddCourseRequest course) {

		LOG.info("Request received for add course");

		CommonApiResponse response = new CommonApiResponse();

		if (course == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (course.getId() == 0 || course.getName() == null || course.getDescription() == null || course.getGradeId() == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		
		Grade grade = this.gradeDao.findById(course.getGradeId()).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Course existingCourse = this.courseDao.findById(course.getId()).orElse(null);
        existingCourse.setName(course.getName());
        existingCourse.setDescription(course.getDescription());
		existingCourse.setGrade(grade);
		existingCourse.setStatus(ActiveStatus.ACTIVE.value());
		Course savedCourse = this.courseDao.save(existingCourse);

		if (savedCourse == null) {
			throw new CourseSaveFailedException("Failed to update course");
		}

		response.setResponseMessage("Course Updated Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<CourseResponseDto> fetchAllCourse() {

		LOG.info("Request received for fetching all course");

		CourseResponseDto response = new CourseResponseDto();

		List<Course> courses = new ArrayList<>();

		courses = this.courseDao.findByStatus(ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(courses)) {
			response.setResponseMessage("No Course found");
			response.setSuccess(false);

			return new ResponseEntity<CourseResponseDto>(response, HttpStatus.OK);
		}

		response.setCourses(courses);
		response.setResponseMessage("Courses fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<CourseResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CourseResponseDto> fetchAllCourseByGrade(int gradeId) {

		LOG.info("Request received for fetching all course by Grade");

		CourseResponseDto response = new CourseResponseDto();

		List<Course> courses = new ArrayList<>();

		Grade grade = this.gradeDao.findById(gradeId).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<CourseResponseDto>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		courses = this.courseDao.findByGradeAndStatus(grade, ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(courses)) {
			response.setResponseMessage("No Course found");
			response.setSuccess(false);

			return new ResponseEntity<CourseResponseDto>(response, HttpStatus.OK);
		}

		response.setCourses(courses);
		response.setResponseMessage("Courses fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<CourseResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deleteCourse(int courseId) {

		LOG.info("Request received for deleting grade");

		CommonApiResponse response = new CommonApiResponse();

		if (courseId == 0) {
			response.setResponseMessage("missing course Id");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Course course = this.courseDao.findById(courseId).orElse(null);

		if (course == null) {
			response.setResponseMessage("course not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		course.setStatus(ActiveStatus.DEACTIVATED.value());
		Course updatedCourse = this.courseDao.save(course);

		if (updatedCourse == null) {
			throw new GradeSaveFailedException("Failed to delete the Course");
		}

		response.setResponseMessage("Course Deleted Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<CourseResponseDto> fetchCourseById(int courseId) {

		LOG.info("Request received for fetch course by course id");

		CourseResponseDto response = new CourseResponseDto();
		
		if (courseId == 0) {
			response.setResponseMessage("missing course Id");
			response.setSuccess(false);

			return new ResponseEntity<CourseResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Course course = this.courseDao.findById(courseId).orElse(null);

		if (course == null) {
			response.setResponseMessage("course not found");
			response.setSuccess(false);

			return new ResponseEntity<CourseResponseDto>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.setCourses(Arrays.asList(course));
		response.setResponseMessage("Course fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<CourseResponseDto>(response, HttpStatus.OK);
	}

}
