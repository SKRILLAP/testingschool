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

import com.schoolmanagement.dto.AddBatchRequest;
import com.schoolmanagement.dto.BatchResponseDto;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.service.BatchService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/batch")
//@CrossOrigin(origins = "http://localhost:3000")
public class BatchController {

	@Autowired
	private BatchService batchService;

	@PostMapping("/add")
	@Operation(summary = "Api to add batch")
	public ResponseEntity<CommonApiResponse> addBatch(@RequestBody AddBatchRequest request) {
		return batchService.addBatch(request);
	}

	@PutMapping("/update")
	@Operation(summary = "Api to update batch")
	public ResponseEntity<CommonApiResponse> updateBatch(@RequestBody AddBatchRequest batch) {
		return batchService.updateBatch(batch);
	}

	@GetMapping("/fetch/all")
	@Operation(summary = "Api to fetch all batch")
	public ResponseEntity<BatchResponseDto> fetchAllBatch() {
		return batchService.fetchAllBatch();
	}

	@GetMapping("/fetch/all/grade-wise")
	@Operation(summary = "Api to fetch all batch by grades")
	public ResponseEntity<BatchResponseDto> fetchAllBatchbyGrade(@RequestParam("gradeId") int gradeId) {
		return batchService.fetchAllBatchByGrade(gradeId);
	}

	@GetMapping("/fetch/id")
	@Operation(summary = "Api to fetch all batch by grades")
	public ResponseEntity<BatchResponseDto> fetchBatchById(@RequestParam("batchId") int batchId) {
		return batchService.fetchBatchById(batchId);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Api to delete batch")
	public ResponseEntity<CommonApiResponse> deleteBatch(@RequestParam("batchId") int batchId) {
		return batchService.deleteBatch(batchId);
	}

}
