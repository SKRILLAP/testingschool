package com.schoolmanagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Holiday {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String date; // Format: YYYY-MM-DD
	private String name; // Name of the holiday (e.g., New Year, Independence Day)
	private String description; // Optional description of the holiday
	private String createdDate; // Timestamp for when the holiday was added

}
