package com.nebulytix.controller;

import com.nebulytix.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/calendly/webhooks")
// @CrossOrigin(origins = "*") // If needed
public class CalendlyWebhookController {

    @Autowired
    private MeetingService meetingService;

    @PostMapping
    public ResponseEntity<String> handleCalendlyWebhook(@RequestBody Map<String, Object> payload) {
        String event = (String) payload.get("event");

        if ("invitee.created".equals(event) || "invitee.canceled".equals(event)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> payloadData = (Map<String, Object>) payload.get("payload");
            if (payloadData != null) {
                meetingService.saveOrUpdateMeeting(payloadData, event);
            }
        }

        return ResponseEntity.ok("Webhook received");
    }
}
