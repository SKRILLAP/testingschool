package com.schoolmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.dto.CommonApiResponse;
import com.schoolmanagement.dto.HolidayRequestDto;
import com.schoolmanagement.dto.HolidayResponseDto;
import com.schoolmanagement.service.HolidayService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/holiday")
//@CrossOrigin(origins = "http://localhost:3000")
@Validated
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    // Add a new holiday
    @PostMapping("/add")
    public ResponseEntity<CommonApiResponse> addHoliday(@Valid @RequestBody HolidayRequestDto holidayRequestDto) {
        return holidayService.addHoliday(holidayRequestDto);
    }

    // Get all holidays for a company
    @GetMapping("/fetch")
    public ResponseEntity<HolidayResponseDto> getAllHolidays() {
        return holidayService.getHolidaysByCompany();
    }

    // Update an existing holiday
    @PutMapping("/update/{holidayId}")
    public ResponseEntity<CommonApiResponse> updateHoliday(@PathVariable int holidayId,
            @Valid @RequestBody HolidayRequestDto holidayRequestDto) {
        return holidayService.updateHoliday(holidayId, holidayRequestDto);
    }

    // Delete a holiday by ID
    @DeleteMapping("/delete/{holidayId}")
    public ResponseEntity<CommonApiResponse> deleteHoliday(@PathVariable int holidayId) {
        return holidayService.deleteHoliday(holidayId);
    }
}
