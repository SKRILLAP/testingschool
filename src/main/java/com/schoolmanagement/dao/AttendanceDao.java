package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Attendance;
import com.schoolmanagement.entity.User;

@Repository
public interface AttendanceDao extends JpaRepository<Attendance, Integer> {

	Attendance findByUserAndDate(User user, String string);

	List<Attendance> findByDateAndWorkingStatus(String string, String status);

	List<Attendance> findByUserAndDateContainingIgnoreCaseAndWorkingStatus(User employeeUser, String string,
			String status);

	List<Attendance> findByUserAndDateContainingIgnoreCase(User employeeUser, String date);

}
