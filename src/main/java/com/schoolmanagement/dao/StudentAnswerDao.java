package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Exam;
import com.schoolmanagement.entity.Question;
import com.schoolmanagement.entity.StudentAnswer;
import com.schoolmanagement.entity.User;

@Repository
public interface StudentAnswerDao extends JpaRepository<StudentAnswer, Integer> {
	
	List<StudentAnswer> findByExam(Exam exam);
	
	StudentAnswer findByQuestion(Question question);
	
	List<StudentAnswer> findByExamAndStudent(Exam exam, User student);

}
