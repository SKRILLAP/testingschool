package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Course;
import com.schoolmanagement.entity.Exam;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.User;

@Repository
public interface ExamDao extends JpaRepository<Exam, Integer> {

	List<Exam> findByCourseAndStatus(Course course, String status);

	List<Exam> findByGradeAndStatus(Grade grade, String status);

	List<Exam> findByTeacherAndStatus(User teacher, String status);

	List<Exam> findByStatus(String status);

	List<Exam> findByGradeAndStartTimeGreaterThanAndStatus(Grade grade, String startTime, String status);

	List<Exam> findByGradeAndStartTimeLessThanAndStatus(Grade grade, String startTime, String status);

	List<Exam> findByGradeAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndStatus(Grade grade, String currentTime,
			String currenTime, String status);

	List<Exam> findByStatusOrderByIdDesc(String value);

	List<Exam> findByStartTimeGreaterThanAndStatus(String startTime, String status);

	List<Exam> findByStartTimeLessThanAndStatus( String startTime, String status);

	List<Exam> findByStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndStatus(String currentTime,
			String currenTime, String status);

	List<Exam> findByStartTimeGreaterThanAndStatusOrderByIdDesc(String now, String value);

	List<Exam> findByStartTimeLessThanAndStatusOrderByIdDesc(String now, String value);
	
}
