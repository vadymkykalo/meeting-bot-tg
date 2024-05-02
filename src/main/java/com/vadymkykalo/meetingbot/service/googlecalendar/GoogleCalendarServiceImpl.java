package com.vadymkykalo.meetingbot.service.googlecalendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    @Autowired
    private Calendar calendarService;

    @NotNull
    @Override
    public String createEventWithParticipants(@NotNull EventData eventData) throws  IOException {

        Event event = new Event()
                .setSummary(eventData.getSummary())
                .setDescription(eventData.getDescription());

        DateTime startDateTime = new DateTime(eventData.getDateStart() + "T" + eventData.getTimeStart() + ":00+02:00");
        event.setStart(new EventDateTime().setDateTime(startDateTime));
        event.setEnd(new EventDateTime().setDateTime(startDateTime));

        List<EventAttendee> attendees = eventData.getAttendeesEmails().stream()
                .map(email -> new EventAttendee().setEmail(email))
                .collect(Collectors.toList());
        event.setAttendees(attendees);

        event = calendarService.events().insert("primary", event).setConferenceDataVersion(1).execute();

        return event.getHangoutLink();
    }
}
