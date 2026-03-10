package com.nebulytix.repository;

import com.nebulytix.entity.ScheduledMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<ScheduledMeeting, Long> {
    Optional<ScheduledMeeting> findByCalendlyEventUri(String calendlyEventUri);
}
