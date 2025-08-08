package com.schoolmanagement.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.schoolmanagement.dao.BatchDao;
import com.schoolmanagement.dao.CourseDao;
import com.schoolmanagement.dao.GradeDao;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.GradeBatchResponse;
import com.schoolmanagement.dto.GradeResponseDto;
import com.schoolmanagement.entity.Batch;
import com.schoolmanagement.entity.Course;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.exception.GradeSaveFailedException;
import com.schoolmanagement.utility.Constants.ActiveStatus;

import jakarta.transaction.Transactional;

@Component
@Transactional
public class GradeService {

	private final Logger LOG = LoggerFactory.getLogger(GradeService.class);

	@Autowired
	private GradeDao gradeDao;
	
	@Autowired
	private BatchDao batchDao;
	
	@Autowired
	private CourseDao courseDao;
	
	public ResponseEntity<CommonApiResponse> addGrade(Grade grade) {
		
		LOG.info("Request received for add grade");

		CommonApiResponse response = new CommonApiResponse();

		if (grade == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		grade.setStatus(ActiveStatus.ACTIVE.value());

		Grade savedGrade = this.gradeDao.save(grade);

		if (savedGrade == null) {
			throw new GradeSaveFailedException("Failed to add grade");
		}

		response.setResponseMessage("Grade Added Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}
	
    public ResponseEntity<CommonApiResponse> updateGrade(Grade grade) {
		
		LOG.info("Request received for add grade");

		CommonApiResponse response = new CommonApiResponse();

		if (grade == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (grade.getId() == 0) {
			response.setResponseMessage("missing grade Id");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		grade.setStatus(ActiveStatus.ACTIVE.value());
		Grade savedGrade = this.gradeDao.save(grade);

		if (savedGrade == null) {
			throw new GradeSaveFailedException("Failed to update grade");
		}

		response.setResponseMessage("Grade Updated Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<GradeResponseDto> fetchAllGrade() {

		LOG.info("Request received for fetching all grades");

		GradeResponseDto response = new GradeResponseDto();

		List<Grade> grades = new ArrayList<>();

		grades = this.gradeDao.findByStatus(ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(grades)) {
			response.setResponseMessage("No Grades found");
			response.setSuccess(false);

			return new ResponseEntity<GradeResponseDto>(response, HttpStatus.OK);
		}

		response.setGrades(grades);
		response.setResponseMessage("Grade fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<GradeResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deleteGrade(int gradeId) {

		LOG.info("Request received for deleting grade");

		CommonApiResponse response = new CommonApiResponse();

		if (gradeId == 0) {
			response.setResponseMessage("missing grade Id");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Grade grade = this.gradeDao.findById(gradeId).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}


		grade.setStatus(ActiveStatus.DEACTIVATED.value());
		Grade updatedGrade = this.gradeDao.save(grade);

		if (updatedGrade == null) {
			throw new GradeSaveFailedException("Failed to delete the Grade");
		}

		response.setResponseMessage("Grade Deleted Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<GradeBatchResponse> fetchAllGradesWithBatches() {

		LOG.info("Request received for fetching all grades data");

		GradeBatchResponse response = new GradeBatchResponse();

		List<Grade> grades = new ArrayList<>();

		grades = this.gradeDao.findByStatus(ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(grades)) {
			response.setResponseMessage("No Grades found");
			response.setSuccess(false);

			return new ResponseEntity<GradeBatchResponse>(response, HttpStatus.OK);
		}

		response.setGrades(grades);
		
		List<Batch> batches = new ArrayList<>();
		
		batches = this.batchDao.findByStatus(ActiveStatus.ACTIVE.value());

		if (!CollectionUtils.isEmpty(batches)) {
			response.setBatches(batches);
		}
		
		List<Course> courses = new ArrayList<>();
		
		courses = this.courseDao.findByStatus(ActiveStatus.ACTIVE.value());

		if (!CollectionUtils.isEmpty(courses)) {
			response.setCourses(courses);
		}
		
		response.setResponseMessage("Grade fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<GradeBatchResponse>(response, HttpStatus.OK);
	}

}
