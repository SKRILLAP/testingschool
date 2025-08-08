package com.schoolmanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.schoolmanagement.entity.NoticeBoard;

import lombok.Data;

@Data
public class NoticeBoardResponse extends CommonApiResponse {

	private List<NoticeBoard> notices = new ArrayList<>();
	
}
