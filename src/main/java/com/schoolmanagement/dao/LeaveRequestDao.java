package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.LeaveRequest;
import com.schoolmanagement.entity.User;

@Repository
public interface LeaveRequestDao extends JpaRepository<LeaveRequest, Integer> {

	List<LeaveRequest> findByUser(User user);

	List<LeaveRequest> findByManager(User manager);

}
