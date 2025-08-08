package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Batch;
import com.schoolmanagement.entity.Course;
import com.schoolmanagement.entity.TimeTable;
import com.schoolmanagement.entity.User;

@Repository
public interface TimeTableDao extends JpaRepository<TimeTable, Integer> {

	List<TimeTable> findByBatch(Batch batch);

	List<TimeTable> findByBatchAndCourse(Batch batch, Course course);

	List<TimeTable> findByBatchAndTeacher(Batch batch, User teachzer);

	List<TimeTable> findByBatchAndCourseAndTeacher(Batch batch, Course course, User teacher);

	List<TimeTable> findByTeacher(User teachzer);

}
