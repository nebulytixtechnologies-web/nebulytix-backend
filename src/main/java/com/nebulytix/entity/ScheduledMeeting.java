package com.nebulytix.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_meetings")
@Data
public class ScheduledMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String calendlyEventUri;
    private String inviteeEmail;
    private String inviteeName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // active, canceled

}
