package com.schoolmanagement.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Question;

@Repository
public interface QuestionDao extends JpaRepository<Question, Integer> {
	
}
