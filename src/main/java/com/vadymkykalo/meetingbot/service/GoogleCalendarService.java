package com.vadymkykalo.meetingbot.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    private final Calendar calendar;

    @Value("#{'${bot.attendees}'.split(',')}")
    private List<String> attendeesEmails;

    public String createGoogleMeetLink() throws IOException, GeneralSecurityException {

        long startTimeMillis = System.currentTimeMillis() + 5 * 60 * 1000;
        DateTime startDateTime = new DateTime(startTimeMillis);

        long endTimeMillis = startTimeMillis + 60 * 60 * 1000;
        DateTime endDateTime = new DateTime(endTimeMillis);

        List<EventAttendee> attendees = attendeesEmails.stream()
                .map(email -> new EventAttendee().setEmail(email.trim()))
                .collect(Collectors.toList());

        Event event = new Event()
                .setSummary("Google Meet M4Y IT")
                .setDescription("A chance to POBALAKATY with friends")
                .setStart(new EventDateTime().setDateTime(startDateTime).setTimeZone("Europe/Kiev"))
                .setEnd(new EventDateTime().setDateTime(endDateTime).setTimeZone("Europe/Kiev"))
                .setVisibility("public")
                .setAttendees(attendees)
                .setGuestsCanInviteOthers(true)
                .setGuestsCanModify(true)
                .setGuestsCanSeeOtherGuests(true);

        CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest()
                .setRequestId(UUID.randomUUID().toString())
                .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"));

        ConferenceData conferenceData = new ConferenceData()
                .setCreateRequest(createConferenceRequest);

        event.setConferenceData(conferenceData);

        event = calendar.events().insert("primary", event)
                .setConferenceDataVersion(1)
                .execute();

        ConferenceData createdConferenceData = event.getConferenceData();
        if (createdConferenceData != null && createdConferenceData.getEntryPoints() != null) {
            for (EntryPoint entryPoint : createdConferenceData.getEntryPoints()) {
                if ("video".equals(entryPoint.getEntryPointType())) {
                    return entryPoint.getUri();
                }
            }
        }
        return "No Google Meet link available";
    }
}
