package com.schoolmanagement.dto;

import java.util.Map;

public class AddTimeTableRequest {

	private int batchId;

	private int gradeId;

	private Map<String, Map<String, TimeTableEntry>> timetable;

	public int getBatchId() {
		return batchId;
	}

	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}

	public int getGradeId() {
		return gradeId;
	}

	public void setGradeId(int gradeId) {
		this.gradeId = gradeId;
	}

	public Map<String, Map<String, TimeTableEntry>> getTimetable() {
		return timetable;
	}

	public void setTimetable(Map<String, Map<String, TimeTableEntry>> timetable) {
		this.timetable = timetable;
	}
	
	public static boolean validate(AddTimeTableRequest request) {
		if(request.getBatchId() == 0 || request.getGradeId() == 0 || request.getTimetable().size() <= 0) {
			return false;
		}
		
		return true;
	}

}

// sample add time table request

//  {
//	  "batchId": 1,
//	  "gradeId": 101,
//	  "timetable": {
//	    "Monday": {
//	      "10am - 11am": {
//	        "courseId": 201,
//	        "teacherId": 301
//	      },
//	      "11am - 12pm": {
//	        "courseId": 202,
//	        "teacherId": 302
//	      }
//	      // ... other time slots
//	    },
//	    // ... other days
//	  }
//	}
