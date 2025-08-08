package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Course;
import com.schoolmanagement.entity.Grade;

@Repository
public interface CourseDao extends JpaRepository<Course, Integer> {
	
	List<Course> findByGradeAndStatus(Grade grade, String status);
	
	List<Course> findByStatus(String status);											

}
