package com.schoolmanagement.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolmanagement.dao.BatchDao;
import com.schoolmanagement.dao.CourseDao;
import com.schoolmanagement.dao.GradeDao;
import com.schoolmanagement.dao.TimeTableDao;
import com.schoolmanagement.dao.UserDao;
import com.schoolmanagement.dto.AddTimeTableRequest;
import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.FullTimeTable;
import com.schoolmanagement.dto.TeacherCourse;
import com.schoolmanagement.dto.TimeTableEntry;
import com.schoolmanagement.dto.TimeTableResponse;
import com.schoolmanagement.entity.Batch;
import com.schoolmanagement.entity.Course;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.TimeTable;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.exception.TimeTableSaveFailedException;
import com.schoolmanagement.utility.Constants.ActiveStatus;
import com.schoolmanagement.utility.Constants.UserRole;
import com.schoolmanagement.utility.EmailService;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;

@Component
@Transactional
public class TimeTableService {

	private final Logger LOG = LoggerFactory.getLogger(TimeTableService.class);

	@Autowired
	private TimeTableDao timeTableService;

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private GradeDao gradeDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private BatchDao batchDao;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EmailService emailService;

	public ResponseEntity<CommonApiResponse> addTimeTable(AddTimeTableRequest request) {

		LOG.info("Request received for add course");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || !AddTimeTableRequest.validate(request)) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Grade grade = this.gradeDao.findById(request.getGradeId()).orElse(null);

		if (grade == null) {
			response.setResponseMessage("grade not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Batch batch = this.batchDao.findById(request.getBatchId()).orElse(null);

		if (batch == null) {
			response.setResponseMessage("batch not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Map<String, Map<String, TimeTableEntry>> timeTable = request.getTimetable();

		List<TimeTable> tables = new ArrayList<>();

		for (Map.Entry<String, Map<String, TimeTableEntry>> dayEntry : timeTable.entrySet()) {
			String dayOfWeek = dayEntry.getKey(); // Monday

			if (StringUtils.isEmpty(dayOfWeek)) {
				throw new TimeTableSaveFailedException("Exception Caught While Saving the TimeTable");
			}

			Map<String, TimeTableEntry> timeSlots = dayEntry.getValue(); // complete slot timetable

			for (Map.Entry<String, TimeTableEntry> timeSlotEntry : timeSlots.entrySet()) {
				String timeSlot = timeSlotEntry.getKey(); // 9am -10am
				TimeTableEntry timeTableEntry = timeSlotEntry.getValue();

				int courseId = timeTableEntry.getCourseId();
				int teacherId = timeTableEntry.getTeacherId();

				if (courseId == 0 || teacherId == 0) {
					throw new TimeTableSaveFailedException("Exception Caught While Saving the TimeTable");
				}

				Course course = this.courseDao.findById(courseId).orElse(null);

				if (course == null) {
					throw new TimeTableSaveFailedException("Exception Caught While Saving the TimeTable");
				}

				User teacher = this.userDao.findById(teacherId).orElse(null);

				if (teacher == null) {
					throw new TimeTableSaveFailedException("Exception Caught While Saving the TimeTable");
				}

				System.out.println("  Time Slot: " + timeSlot);
				System.out.println("    Course ID: " + timeTableEntry.getCourseId());
				System.out.println("    Teacher ID: " + timeTableEntry.getTeacherId());

				TimeTable table = new TimeTable();
				table.setBatch(batch);
				table.setDayOfWeek(dayOfWeek);
				table.setGrade(grade);
				table.setSlot(timeSlot);

				table.setTeacher(teacher);
				table.setCourse(course);

				tables.add(table);
			}
		}

		List<TimeTable> savedTimeTable = this.timeTableService.saveAll(tables);

		if (CollectionUtils.isEmpty(savedTimeTable)) {
			System.out.println("Issue here");
			throw new TimeTableSaveFailedException("Exception Caught While Saving the TimeTable");
		}

		List<User> batchStudents = this.userDao.findByRoleAndBatchAndStatus(UserRole.ROLE_STUDENT.value(), batch,
				ActiveStatus.ACTIVE.value());

		String subject = "New Timetable Added – Stay Updated with Your Schedule!!!";
		String message = "<html>" + "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>"
				+ "<p>Dear Student,</p>"
				+ "<p>We hope you're doing well. A new timetable has been added for your batch by the teacher.</p>"
				+ "<p>Regards,</p>" + "<p><strong>Management Team</strong></p>" + "</body></html>";

		batchStudents.stream().forEach(u -> {
			try {
				this.emailService.sendEmail(u.getEmailId(), subject, message);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		});

		response.setResponseMessage("Time Table Saved Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<TimeTableResponse> fetchTimeTable(Integer batchId) {

		TimeTableResponse response = new TimeTableResponse();

		if (batchId == null) {
			response.setResponseMessage("batch id missing");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Batch batch = this.batchDao.findById(batchId).orElse(null);

		if (batch == null) {
			response.setResponseMessage("batch not found");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<TimeTable> timeTables = this.timeTableService.findByBatch(batch);

		if (CollectionUtils.isEmpty(timeTables)) {
			response.setResponseMessage("Time Table not uploded yet!!!");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.OK);
		}

		FullTimeTable fullTimeTable = new FullTimeTable();

		// Create a map to store the timetable data.
		Map<String, Map<String, TeacherCourse>> timetableMap = new HashMap<>();

		timetableMap = convertListToMap(timeTables);
		fullTimeTable.setBatch(batch);
		fullTimeTable.setGrade(batch.getGrade());
		fullTimeTable.setTimetable(timetableMap);

		// printing response

		try {
			System.out.println(objectMapper.writeValueAsString(timetableMap));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response.setTimeTables(Arrays.asList(fullTimeTable));
		response.setResponseMessage("Time Table Fetched Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<TimeTableResponse>(response, HttpStatus.OK);
	}

	public static Map<String, Map<String, TeacherCourse>> convertListToMap(List<TimeTable> timeTables) {
		// Create a map to store the timetable data.
		Map<String, Map<String, TeacherCourse>> timetableMap = new HashMap<>();

		// Iterate through each TimeTable entry.
		for (TimeTable timeTable : timeTables) {
			// Extract the day of the week and time slot from the current TimeTable entry.
			String dayOfWeek = timeTable.getDayOfWeek();
			String slot = timeTable.getSlot();

			// Create or get the map for the specific day of the week.
			Map<String, TeacherCourse> dayMap = timetableMap.computeIfAbsent(dayOfWeek, k -> new HashMap<>());

			// Create a TeacherCourse object and add it to the map for the specific time
			// slot.
			TeacherCourse teacherCourse = new TeacherCourse();
			teacherCourse.setCourse(timeTable.getCourse());
			teacherCourse.setTeacher(timeTable.getTeacher());
			teacherCourse.setBatch(timeTable.getBatch()); // only in case of time table by teacher set this because
															// teacher can take class in different grade and batches

			dayMap.put(slot, teacherCourse);
		}

		// Return the final timetable map.
		return timetableMap;
	}

	public ResponseEntity<TimeTableResponse> fetchTimeTableByBatchAndCourse(Integer batchId, Integer courseId) {

		TimeTableResponse response = new TimeTableResponse();

		if (batchId == null) {
			response.setResponseMessage("batch id missing");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Batch batch = this.batchDao.findById(batchId).orElse(null);

		if (batch == null) {
			response.setResponseMessage("batch not found");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Course course = this.courseDao.findById(courseId).orElse(null);

		if (course == null) {
			response.setResponseMessage("course not found");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<TimeTable> timeTables = this.timeTableService.findByBatchAndCourse(batch, course);

		if (CollectionUtils.isEmpty(timeTables)) {
			response.setResponseMessage("Time Table not uploded yet!!!");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.OK);
		}

		FullTimeTable fullTimeTable = new FullTimeTable();

		// Create a map to store the timetable data.
		Map<String, Map<String, TeacherCourse>> timetableMap = new HashMap<>();

		timetableMap = convertListToMap(timeTables);
		fullTimeTable.setBatch(batch);
		fullTimeTable.setGrade(batch.getGrade());
		fullTimeTable.setTimetable(timetableMap);

		// printing response

		try {
			System.out.println(objectMapper.writeValueAsString(timetableMap));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response.setTimeTables(Arrays.asList(fullTimeTable));
		response.setResponseMessage("Time Table Fetched Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<TimeTableResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<TimeTableResponse> fetchTimeTableByBatchTeacher(Integer batchId, Integer teacherId) {

		TimeTableResponse response = new TimeTableResponse();

		if (batchId == null) {
			response.setResponseMessage("batch id missing");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Batch batch = this.batchDao.findById(batchId).orElse(null);

		if (batch == null) {
			response.setResponseMessage("batch not found");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User teacher = this.userDao.findById(teacherId).orElse(null);

		if (teacher == null) {
			response.setResponseMessage("teacher not found");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<TimeTable> timeTables = this.timeTableService.findByBatchAndTeacher(batch, teacher);

		if (CollectionUtils.isEmpty(timeTables)) {
			response.setResponseMessage("Time Table not uploded yet!!!");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.OK);
		}

		FullTimeTable fullTimeTable = new FullTimeTable();

		// Create a map to store the timetable data.
		Map<String, Map<String, TeacherCourse>> timetableMap = new HashMap<>();

		timetableMap = convertListToMap(timeTables);
		fullTimeTable.setBatch(batch);
		fullTimeTable.setGrade(batch.getGrade());
		fullTimeTable.setTimetable(timetableMap);

		// printing response

		try {
			System.out.println(objectMapper.writeValueAsString(timetableMap));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response.setTimeTables(Arrays.asList(fullTimeTable));
		response.setResponseMessage("Time Table Fetched Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<TimeTableResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<TimeTableResponse> fetchTimeTableByBatchTeacherAndCourse(Integer batchId, Integer courseId,
			Integer teacherId) {

		TimeTableResponse response = new TimeTableResponse();

		if (batchId == null) {
			response.setResponseMessage("batch id missing");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Batch batch = this.batchDao.findById(batchId).orElse(null);

		if (batch == null) {
			response.setResponseMessage("batch not found");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Course course = this.courseDao.findById(courseId).orElse(null);

		if (course == null) {
			response.setResponseMessage("course not found");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User teacher = this.userDao.findById(teacherId).orElse(null);

		if (teacher == null) {
			response.setResponseMessage("teacher not found");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<TimeTable> timeTables = this.timeTableService.findByBatchAndCourseAndTeacher(batch, course, teacher);

		if (CollectionUtils.isEmpty(timeTables)) {
			response.setResponseMessage("Time Table not uploded yet!!!");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.OK);
		}

		FullTimeTable fullTimeTable = new FullTimeTable();

		// Create a map to store the timetable data.
		Map<String, Map<String, TeacherCourse>> timetableMap = new HashMap<>();

		timetableMap = convertListToMap(timeTables);
		fullTimeTable.setBatch(batch);
		fullTimeTable.setGrade(batch.getGrade());
		fullTimeTable.setTimetable(timetableMap);

		// printing response

		try {
			System.out.println(objectMapper.writeValueAsString(timetableMap));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response.setTimeTables(Arrays.asList(fullTimeTable));
		response.setResponseMessage("Time Table Fetched Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<TimeTableResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<TimeTableResponse> fetchTimeTableByTeacher(Integer teacherId) {

		TimeTableResponse response = new TimeTableResponse();

		if (teacherId == null) {
			response.setResponseMessage("teacher id missing");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User teacher = this.userDao.findById(teacherId).orElse(null);

		if (teacher == null) {
			response.setResponseMessage("teacher not found");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<TimeTable> timeTables = this.timeTableService.findByTeacher(teacher);

		if (CollectionUtils.isEmpty(timeTables)) {
			response.setResponseMessage("Time Table not uploded yet!!!");
			response.setSuccess(false);

			return new ResponseEntity<TimeTableResponse>(response, HttpStatus.OK);
		}

		FullTimeTable fullTimeTable = new FullTimeTable();

		// Create a map to store the timetable data.
		Map<String, Map<String, TeacherCourse>> timetableMap = new HashMap<>();

		timetableMap = convertListToMap(timeTables);
//		fullTimeTable.setBatch(batch);
//		fullTimeTable.setGrade(batch.getGrade());
		fullTimeTable.setTimetable(timetableMap);

		// printing response

		try {
			System.out.println(objectMapper.writeValueAsString(timetableMap));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response.setTimeTables(Arrays.asList(fullTimeTable));
		response.setResponseMessage("Time Table Fetched Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<TimeTableResponse>(response, HttpStatus.OK);
	}

}
