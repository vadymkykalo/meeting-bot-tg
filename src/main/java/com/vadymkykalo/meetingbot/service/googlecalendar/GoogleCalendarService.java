package com.vadymkykalo.meetingbot.service.googlecalendar;

import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleCalendarService {

    @NotNull
    String createEventWithParticipants(@NotNull EventData eventData) throws GeneralSecurityException, IOException;
}
