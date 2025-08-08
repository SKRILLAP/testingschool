package com.schoolmanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.schoolmanagement.entity.Attendance;

import lombok.Data;

@Data
public class AttendanceResponseDto extends CommonApiResponse {

	private List<Attendance> attendances = new ArrayList<>();

}
