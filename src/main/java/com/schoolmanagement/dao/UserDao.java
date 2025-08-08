package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Batch;
import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

	User findByEmailId(String email);

	User findByEmailIdAndStatus(String email, String status);

	User findByRoleAndStatusIn(String role, List<String> status);

	List<User> findByRole(String role);

	User findByEmailIdAndRoleAndStatus(String emailId, String role, String status);

	List<User> findByRoleAndStatus(String role, String status);

	List<User> findByRoleAndBatchAndStatus(String role, Batch batch, String status);
	
	@Query("SELECT u FROM User u WHERE u.role = :role AND u.status = :status AND u.batch.grade = :grade ")
	List<User> findByRoleAndGradeAndStatus(@Param("role") String role,@Param("grade") Grade grade,@Param("status") String status);

	List<User> findByRoleInAndStatus(List<String> roles, String status);

}
