package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Assignment;
import com.schoolmanagement.entity.StudentAssignmentSubmission;
import com.schoolmanagement.entity.User;

@Repository
public interface StudentAssignmentSubmissionDao extends JpaRepository<StudentAssignmentSubmission, Integer> {

	List<StudentAssignmentSubmission> findByAssignment(Assignment assignment);

	List<StudentAssignmentSubmission> findByStudent(User student);

	List<StudentAssignmentSubmission> findByAssignmentAndStatus(Assignment assignment, String status);

	List<StudentAssignmentSubmission> findByStudentOrderByIdDesc(User student);

}
