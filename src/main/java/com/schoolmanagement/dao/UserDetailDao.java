package com.schoolmanagement.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schoolmanagement.entity.UserDetail;

@Repository
public interface UserDetailDao extends JpaRepository<UserDetail, Integer> {

}
