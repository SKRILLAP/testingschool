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

import com.schoolmanagement.dao.BatchDao;
import com.schoolmanagement.dao.GradeDao;
import com.schoolmanagement.dao.TimeTableDao;
import com.schoolmanagement.dto.AddBatchRequest;
import com.schoolmanagement.dto.BatchResponseDto;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.entity.Batch;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.TimeTable;
import com.schoolmanagement.exception.BatchSaveFailedException;
import com.schoolmanagement.exception.GradeSaveFailedException;
import com.schoolmanagement.utility.Constants.ActiveStatus;

import jakarta.transaction.Transactional;

@Component
@Transactional
public class BatchService {

	private final Logger LOG = LoggerFactory.getLogger(BatchService.class);

	@Autowired
	private BatchDao batchDao;

	@Autowired
	private GradeDao gradeDao;

	@Autowired
	private TimeTableDao timeTableService;

	public ResponseEntity<CommonApiResponse> addBatch(AddBatchRequest request) {

		LOG.info("Request received for add batch");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || !AddBatchRequest.validate(request)) {
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

		Batch batch = new Batch();
		batch.setName(request.getName());
		batch.setDescription(request.getDescription());
		batch.setGrade(grade);
		batch.setStatus(ActiveStatus.ACTIVE.value());

		Batch savedBatch = this.batchDao.save(batch);

		if (savedBatch == null) {
			throw new BatchSaveFailedException("Failed to add batch");
		}

		response.setResponseMessage("Batch Added Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<CommonApiResponse> updateBatch(AddBatchRequest batch) {

		LOG.info("Request received for add batch");

		CommonApiResponse response = new CommonApiResponse();

		if (batch == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (batch.getId() == 0 || batch.getName() == null || batch.getDescription() == null
				|| batch.getGradeId() == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Grade grade = this.gradeDao.findById(batch.getGradeId()).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Batch existingBatch = this.batchDao.findById(batch.getId()).orElse(null);
		existingBatch.setName(batch.getName());
		existingBatch.setDescription(batch.getDescription());
		existingBatch.setGrade(grade);
		existingBatch.setStatus(ActiveStatus.ACTIVE.value());
		Batch savedBatch = this.batchDao.save(existingBatch);

		if (savedBatch == null) {
			throw new BatchSaveFailedException("Failed to update batch");
		}

		response.setResponseMessage("Batch Updated Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<BatchResponseDto> fetchAllBatch() {

		LOG.info("Request received for fetching all batch");

		BatchResponseDto response = new BatchResponseDto();

		List<Batch> batchs = new ArrayList<>();

		batchs = this.batchDao.findByStatus(ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(batchs)) {
			response.setResponseMessage("No Batch found");
			response.setSuccess(false);

			return new ResponseEntity<BatchResponseDto>(response, HttpStatus.OK);
		}

		for (Batch b : batchs) {

			List<TimeTable> timeTables = this.timeTableService.findByBatch(b);

			if (CollectionUtils.isEmpty(timeTables)) {
				b.setTimeTableUploaded(false);
			} else {
				b.setTimeTableUploaded(true);
			}

		}

		response.setBatchs(batchs);
		response.setResponseMessage("Batchs fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<BatchResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<BatchResponseDto> fetchAllBatchByGrade(int gradeId) {

		LOG.info("Request received for fetching all batch by Grade");

		BatchResponseDto response = new BatchResponseDto();

		List<Batch> batchs = new ArrayList<>();

		Grade grade = this.gradeDao.findById(gradeId).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<BatchResponseDto>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		batchs = this.batchDao.findByGradeAndStatus(grade, ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(batchs)) {
			response.setResponseMessage("No Batch found");
			response.setSuccess(false);

			return new ResponseEntity<BatchResponseDto>(response, HttpStatus.OK);
		}

		for (Batch b : batchs) {

			List<TimeTable> timeTables = this.timeTableService.findByBatch(b);

			if (CollectionUtils.isEmpty(timeTables)) {
				b.setTimeTableUploaded(false);
			} else {
				b.setTimeTableUploaded(true);
			}

		}

		response.setBatchs(batchs);
		response.setResponseMessage("Batchs fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<BatchResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deleteBatch(int batchId) {

		LOG.info("Request received for deleting grade");

		CommonApiResponse response = new CommonApiResponse();

		if (batchId == 0) {
			response.setResponseMessage("missing batch Id");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Batch batch = this.batchDao.findById(batchId).orElse(null);

		if (batch == null) {
			response.setResponseMessage("batch not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		batch.setStatus(ActiveStatus.DEACTIVATED.value());
		Batch updatedBatch = this.batchDao.save(batch);

		if (updatedBatch == null) {
			throw new GradeSaveFailedException("Failed to delete the Batch");
		}

		response.setResponseMessage("Batch Deleted Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<BatchResponseDto> fetchBatchById(int batchId) {

		LOG.info("Request received for fetch batch by batch id");

		BatchResponseDto response = new BatchResponseDto();

		if (batchId == 0) {
			response.setResponseMessage("missing batch Id");
			response.setSuccess(false);

			return new ResponseEntity<BatchResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Batch batch = this.batchDao.findById(batchId).orElse(null);

		if (batch == null) {
			response.setResponseMessage("batch not found");
			response.setSuccess(false);

			return new ResponseEntity<BatchResponseDto>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<TimeTable> timeTables = this.timeTableService.findByBatch(batch);

		if (CollectionUtils.isEmpty(timeTables)) {
			batch.setTimeTableUploaded(false);
		} else {
			batch.setTimeTableUploaded(true);
		}

		response.setBatchs(Arrays.asList(batch));
		response.setResponseMessage("Batch fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<BatchResponseDto>(response, HttpStatus.OK);
	}

}
