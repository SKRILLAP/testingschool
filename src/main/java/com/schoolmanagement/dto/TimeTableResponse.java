package com.schoolmanagement.dto;

import java.util.List;

public class TimeTableResponse extends CommonApiResponse {

	private List<FullTimeTable> timeTables;

	public List<FullTimeTable> getTimeTables() {
		return timeTables;
	}

	public void setTimeTables(List<FullTimeTable> timeTables) {
		this.timeTables = timeTables;
	}

}
