package com.schoolmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.dto.AddNoticeRequest;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.NoticeBoardResponse;
import com.schoolmanagement.service.NoticeBoardService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/notice")
//@CrossOrigin(origins = "http://localhost:3000")
public class NoticeBoardController {

	@Autowired
	private NoticeBoardService noticeBoardService;

	@PostMapping("/add")
	@Operation(summary = "Api to add exam")
	public ResponseEntity<CommonApiResponse> addNoticeBoard(AddNoticeRequest request) {
		return noticeBoardService.addNoticeBoard(request);
	}

	@GetMapping("/fetch/all")
	@Operation(summary = "Api to fetch all notices")
	public ResponseEntity<NoticeBoardResponse> fetchAllNotice() {
		return noticeBoardService.fetchAllNotice();
	}

	@GetMapping("/fetch/all/grade-wise")
	@Operation(summary = "Api to fetch all exams by grades")
	public ResponseEntity<NoticeBoardResponse> fetchAllNoticeByGrade(@RequestParam("gradeId") int gradeId) {
		return noticeBoardService.fetchAllNoticeByGrade(gradeId);
	}

	// for students login landing page
	@GetMapping("/fetch/todays")
	@Operation(summary = "Api to fetch todays notice by grade")
	public ResponseEntity<NoticeBoardResponse> fetchTodayNoticeByGrade(@RequestParam("gradeId") int gradeId) {
		return noticeBoardService.fetchTodayNoticeByGrade(gradeId);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Api to delete notice")
	public ResponseEntity<CommonApiResponse> deleteNotice(@RequestParam("noticeId") int noticeId) {
		return noticeBoardService.deleteNotice(noticeId);
	}

}
