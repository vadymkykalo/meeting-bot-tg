package com.vadymkykalo.meetingbot.service.googlecalendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.vadymkykalo.meetingbot.util.EmailValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private final Calendar calendar;

    @Value("#{'${bot.attendees:}'.split(',')}")
    private List<String> attendeesEmails;
    @Value("${meeting.start-offset-minutes:5}")
    private int startOffsetMinutes;
    @Value("${meeting.duration-minutes:60}")
    private int durationMinutes;
    @Value("${meeting.timezone:Europe/Kiev}")
    private String timeZone;
    @Value("${meeting.summary:Google Meet}")
    private String summary;
    @Value("${meeting.description:Google Meet}")
    private String description;

    public String createGoogleMeetLink() throws IOException {

        attendeesEmails = Optional.ofNullable(attendeesEmails)
                .filter(emails -> emails.stream().anyMatch(email -> !email.trim().isEmpty()))
                .orElse(Collections.emptyList());

        long startTimeMillis = System.currentTimeMillis() + (long) startOffsetMinutes * 60 * 1000;
        DateTime startDateTime = new DateTime(startTimeMillis);

        long endTimeMillis = startTimeMillis + (long) durationMinutes * 60 * 1000;
        DateTime endDateTime = new DateTime(endTimeMillis);

        List<EventAttendee> attendees = attendeesEmails.stream()
                .map(String::trim)
                .filter(EmailValidator::isValidEmail)
                .map(email -> new EventAttendee().setEmail(email))
                .collect(Collectors.toList());

        Event event = new Event()
                .setSummary(summary)
                .setDescription(description)
                .setStart(new EventDateTime().setDateTime(startDateTime).setTimeZone(timeZone))
                .setEnd(new EventDateTime().setDateTime(endDateTime).setTimeZone(timeZone))
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
