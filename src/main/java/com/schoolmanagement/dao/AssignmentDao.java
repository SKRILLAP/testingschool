package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Assignment;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.User;

@Repository
public interface AssignmentDao extends JpaRepository<Assignment, Integer> {

	List<Assignment> findByTeacherOrderByIdDesc(User teacher);
	
	List<Assignment> findByGradeOrderByIdDesc(Grade grade);
	
	List<Assignment> findAllByOrderByIdDesc();
	
}
