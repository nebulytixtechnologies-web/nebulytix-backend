package com.nebulytix.service;

import com.nebulytix.entity.ScheduledMeeting;
import com.nebulytix.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Value("${calendly.api.token}")
    private String calendlyApiToken;

    public ScheduledMeeting saveOrUpdateMeeting(Map<String, Object> payloadData, String eventType) {
        String eventUri = (String) payloadData.get("event");

        Optional<ScheduledMeeting> existingMeetingOpt = meetingRepository.findByCalendlyEventUri(eventUri);
        ScheduledMeeting meeting = existingMeetingOpt.orElse(new ScheduledMeeting());

        if (eventUri != null) {
            meeting.setCalendlyEventUri(eventUri);

            // Fetch event details to get start and end times
            try {
                RestTemplate restTemplate = new RestTemplate();
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.set("Authorization", "Bearer " + calendlyApiToken);
                org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

                ResponseEntity<Map> response = restTemplate.exchange(
                        eventUri,
                        org.springframework.http.HttpMethod.GET,
                        entity,
                        Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Map<String, Object> resource = (Map<String, Object>) response.getBody().get("resource");
                    if (resource != null) {
                        if (resource.containsKey("start_time")) {
                            meeting.setStartTime(LocalDateTime.parse((String) resource.get("start_time"),
                                    DateTimeFormatter.ISO_DATE_TIME));
                        }
                        if (resource.containsKey("end_time")) {
                            meeting.setEndTime(LocalDateTime.parse((String) resource.get("end_time"),
                                    DateTimeFormatter.ISO_DATE_TIME));
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch event details from Calendly API: " + e.getMessage());
            }
        }

        String email = (String) payloadData.get("email");
        if (email != null) {
            meeting.setInviteeEmail(email);
        }

        String name = (String) payloadData.get("name");
        if (name != null) {
            meeting.setInviteeName(name);
        }

        if ("invitee.canceled".equals(eventType)) {
            meeting.setStatus("canceled");
        } else {
            meeting.setStatus("active");
        }

        return meetingRepository.save(meeting);
    }

    public List<ScheduledMeeting> getAllMeetings() {
        return meetingRepository.findAll();
    }
}
