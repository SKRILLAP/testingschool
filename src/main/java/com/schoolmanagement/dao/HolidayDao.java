package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Holiday;

@Repository
public interface HolidayDao extends JpaRepository<Holiday, Integer> {

	List<Holiday> findByDateContainingIgnoreCase(String yearMonth);

}
