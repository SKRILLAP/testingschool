package com.schoolmanagement.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.schoolmanagement.dao.GradeDao;
import com.schoolmanagement.dao.NoticeBoardDao;
import com.schoolmanagement.dao.UserDao;
import com.schoolmanagement.dto.AddNoticeRequest;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.NoticeBoardResponse;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.NoticeBoard;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.utility.Constants.ActiveStatus;
import com.schoolmanagement.utility.StorageService;

@Service
public class NoticeBoardService {

	private final Logger LOG = LoggerFactory.getLogger(NoticeBoardService.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private GradeDao gradeDao;

	@Autowired
	private NoticeBoardDao noticeBoardDao;

	@Autowired
	private StorageService storageService;

	public ResponseEntity<CommonApiResponse> addNoticeBoard(AddNoticeRequest request) {

		LOG.info("Request received for add exam");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("missing input or bad request");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User teacher = this.userDao.findById(request.getTeacherId()).orElse(null);

		if (teacher == null) {
			response.setResponseMessage("Teacher not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Grade grade = this.gradeDao.findById(request.getGradeId()).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String noticeAttachementFileName = this.storageService.store(request.getAttachmentFile());

		NoticeBoard notice = new NoticeBoard();
		notice.setAddedTime(noticeAttachementFileName);
		notice.setAttachmentFileName(noticeAttachementFileName);
		notice.setForDate(request.getForDate());
		notice.setGrade(grade);
		notice.setNotice(request.getNotice());
		notice.setStatus(ActiveStatus.ACTIVE.value());
		notice.setTeacher(teacher);
		notice.setTitle(request.getTitle());

		NoticeBoard savedNotice = this.noticeBoardDao.save(notice);

		if (savedNotice == null) {
			response.setResponseMessage("Failed to add notice!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.setResponseMessage("Notice Added Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<NoticeBoardResponse> fetchAllNotice() {

		LOG.info("Request received for fetching all notices");

		NoticeBoardResponse response = new NoticeBoardResponse();

		List<NoticeBoard> notices = new ArrayList<>();

		notices = this.noticeBoardDao.findByStatusOrderByIdDesc(ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(notices)) {
			response.setResponseMessage("No Notice found");
			response.setSuccess(false);

			return new ResponseEntity<NoticeBoardResponse>(response, HttpStatus.OK);
		}

		response.setNotices(notices);
		response.setResponseMessage("Notices fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<NoticeBoardResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<NoticeBoardResponse> fetchAllNoticeByGrade(int gradeId) {

		LOG.info("Request received for fetching all notices based on the grade");

		NoticeBoardResponse response = new NoticeBoardResponse();

		if (gradeId == 0) {
			response.setResponseMessage("grade id missing!!!");
			response.setSuccess(false);

			return new ResponseEntity<NoticeBoardResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Grade grade = this.gradeDao.findById(gradeId).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<NoticeBoardResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<NoticeBoard> notices = new ArrayList<>();

		notices = this.noticeBoardDao.findByGradeAndStatusOrderByIdDesc(grade, ActiveStatus.ACTIVE.value());

		if (CollectionUtils.isEmpty(notices)) {
			response.setResponseMessage("No Notice found");
			response.setSuccess(false);

			return new ResponseEntity<NoticeBoardResponse>(response, HttpStatus.OK);
		}

		response.setNotices(notices);
		response.setResponseMessage("Notices fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<NoticeBoardResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deleteNotice(int noticeId) {

		LOG.info("Request received for deleting the notice");

		CommonApiResponse response = new CommonApiResponse();

		if (noticeId == 0) {
			response.setResponseMessage("notice id missing!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		NoticeBoard notice = this.noticeBoardDao.findById(noticeId).orElse(null);

		if (notice == null) {
			response.setResponseMessage("notice not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		notice.setStatus(ActiveStatus.DEACTIVATED.value());

		NoticeBoard updatedNotice = this.noticeBoardDao.save(notice);

		if (updatedNotice == null) {
			response.setResponseMessage("Failed to delete the notice!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.setResponseMessage("Notices deleted successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<NoticeBoardResponse> fetchTodayNoticeByGrade(int gradeId) {

		LOG.info("Request received for fetching all notices based on the grade");

		NoticeBoardResponse response = new NoticeBoardResponse();

		if (gradeId == 0) {
			response.setResponseMessage("grade id missing!!!");
			response.setSuccess(false);

			return new ResponseEntity<NoticeBoardResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Grade grade = this.gradeDao.findById(gradeId).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<NoticeBoardResponse>(response, HttpStatus.BAD_REQUEST);
		}
		
		String todaysDate = LocalDate.now().toString();

		List<NoticeBoard> notices = new ArrayList<>();

		notices = this.noticeBoardDao.findByGradeAndStatusAndForDate(grade, ActiveStatus.ACTIVE.value(), todaysDate);

		if (CollectionUtils.isEmpty(notices)) {
			response.setResponseMessage("No Notice found");
			response.setSuccess(false);

			return new ResponseEntity<NoticeBoardResponse>(response, HttpStatus.OK);
		}

		response.setNotices(notices);
		response.setResponseMessage("Notices fetched successful");
		response.setSuccess(true);

		return new ResponseEntity<NoticeBoardResponse>(response, HttpStatus.OK);
	}

}
