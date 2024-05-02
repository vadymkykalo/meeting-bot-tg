package com.vadymkykalo.meetingbot.service.googlecalendar;

import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface GoogleCalendarService {

    @NotNull
    String createEventWithParticipants(
            String summary,
            String description,
            String dateStart,
            String timeStart,
            List<String> attendeesEmails
    ) throws GeneralSecurityException, IOException;
}
