package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Batch;
import com.schoolmanagement.entity.Grade;

@Repository
public interface BatchDao extends JpaRepository<Batch, Integer> {

	List<Batch> findByGradeAndStatus(Grade grade, String status);

	List<Batch> findByStatus(String status);

}
