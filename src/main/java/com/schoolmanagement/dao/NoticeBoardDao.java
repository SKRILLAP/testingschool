package com.schoolmanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.Grade;
import com.schoolmanagement.entity.NoticeBoard;

@Repository
public interface NoticeBoardDao extends JpaRepository<NoticeBoard, Integer> {

	List<NoticeBoard> findByStatusOrderByIdDesc(String status);

	List<NoticeBoard> findByGradeAndStatusOrderByIdDesc(Grade garde, String status);
	
	List<NoticeBoard> findByGradeAndStatusAndForDate(Grade garde, String status, String date);

}
