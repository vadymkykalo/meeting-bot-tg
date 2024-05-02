package com.vadymkykalo.meetingbot.service.googlecalendar;

import jakarta.validation.constraints.NotNull;

import java.io.IOException;

public interface GoogleCalendarService {

    @NotNull
    String createEventWithParticipants(@NotNull EventData eventData) throws IOException;
}
