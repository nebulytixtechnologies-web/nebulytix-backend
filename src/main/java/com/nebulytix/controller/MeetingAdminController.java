package com.nebulytix.controller;

import com.nebulytix.entity.ScheduledMeeting;
import com.nebulytix.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/meetings")
// CrossOrigin may be handled globally
public class MeetingAdminController {

    @Autowired
    private MeetingService meetingService;

    @GetMapping
    public ResponseEntity<List<ScheduledMeeting>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }
}
