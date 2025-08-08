package com.schoolmanagement.scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.schoolmanagement.dao.AttendanceDao;
import com.schoolmanagement.entity.Attendance;
import com.schoolmanagement.utility.Constants.AttendanceStatus;
import com.schoolmanagement.utility.Constants.WorkingStatus;

import jakarta.annotation.PreDestroy;

@Component
public class AttendanceAbsentScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(AttendanceAbsentScheduler.class);

	@Autowired
	private AttendanceDao attendanceDao;

	private final ExecutorService executorService = Executors.newFixedThreadPool(10);

	// Scheduled to run every day at 1:00 AM
	@Scheduled(cron = "0 0 1 * * ?")
	public void markAbsentForMissingAttendance() {
		LOG.info("Attendance Absent Scheduler Starting on thread: {}", Thread.currentThread().getName());

		LocalDate yesterday = LocalDate.now().minusDays(1);

		processCompanyAttendance(yesterday);

		LOG.info("Attendance Absent Scheduler Completed.");
	}

	private void processCompanyAttendance(LocalDate date) {
		LOG.info("Processing attendance on thread: {}", Thread.currentThread().getName());

		List<Attendance> attendances = attendanceDao.findByDateAndWorkingStatus(date.toString(),
				WorkingStatus.WORKING.value());

		if (CollectionUtils.isEmpty(attendances)) {
			LOG.info("No working day attendance records found");
			return;
		}

		attendances.forEach(attendance -> executorService.submit(() -> checkAndMarkAbsent(attendance)));

		LOG.info("Completed attendance processing");
	}

	private void checkAndMarkAbsent(Attendance attendance) {
		LOG.info("Checking attendance for employee: {} {}", attendance.getUser().getFirstName(),
				attendance.getUser().getLastName());

		if (isAttendanceMissing(attendance)) {
			markAsAbsent(attendance);
		}
	}

	private boolean isAttendanceMissing(Attendance attendance) {

		// status NA --> if clock out is not done but clock in can be done
		if (attendance.getStatus().equals("NA")) {
			return true;
		} else {
			return false;
		}

	}

	private void markAsAbsent(Attendance attendance) {
		LOG.info("Marking as absent for employee: {} {} on date: {}", attendance.getUser().getFirstName(),
				attendance.getUser().getLastName(), attendance.getDate());

		attendance.setStatus(AttendanceStatus.ABSENT.value());

		attendance.setLastUpdatedDate(String.valueOf(System.currentTimeMillis()));
		attendanceDao.save(attendance);

		LOG.info("Absent marked for employee: {} {} on date: {}", attendance.getUser().getFirstName(),
				attendance.getUser().getLastName(), attendance.getDate());
	}

	// Gracefully shuts down the ExecutorService when the application context is
	// closed
	@PreDestroy
	public void shutdownExecutorService() {
		LOG.info("Shutting down ExecutorService...");
		executorService.shutdown();
		LOG.info("ExecutorService shut down successfully.");
	}
}
