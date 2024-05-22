package com.vadymkykalo.meetingbot.service.googlecalendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private final Calendar calendar;

    @Value("#{'${bot.attendees:}'.split(',')}")
    private List<String> attendeesEmails;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    public String createGoogleMeetLink() throws IOException {

        attendeesEmails = Optional.ofNullable(attendeesEmails)
                .filter(emails -> emails.stream().anyMatch(email -> !email.trim().isEmpty()))
                .orElse(Collections.emptyList());

        long startTimeMillis = System.currentTimeMillis() + 5 * 60 * 1000;
        DateTime startDateTime = new DateTime(startTimeMillis);

        long endTimeMillis = startTimeMillis + 60 * 60 * 1000;
        DateTime endDateTime = new DateTime(endTimeMillis);

        List<EventAttendee> attendees = attendeesEmails.stream()
                .map(String::trim)
                .filter(this::isValidEmail)
                .map(email -> new EventAttendee().setEmail(email))
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

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
