package com.schoolmanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.schoolmanagement.entity.Batch;

public class BatchResponseDto extends CommonApiResponse {

	private List<Batch> batches = new ArrayList<>();

	public List<Batch> getBatchs() {
		return batches;
	}

	public void setBatchs(List<Batch> batches) {
		this.batches = batches;
	}

}
