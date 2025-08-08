package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Exam;
import com.schoolmanagement.entity.ExamResult;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.User;

@Repository
public interface ExamResultDao extends JpaRepository<ExamResult, Integer> {

	List<ExamResult> findByExam(Exam exam);

	List<ExamResult> findByStudent(User student);

	List<ExamResult> findByStudentAndExam(User student, Exam exam);

	List<ExamResult> findByExam_Grade(Grade grade);

}
