package com.vadymkykalo.meetingbot.service.googlecalendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.vadymkykalo.meetingbot.util.EmailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private final Calendar calendar;
    private final ScheduledExecutorService scheduler;

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

    @Override
    public String createGoogleMeetLink() throws IOException {
        long startTimeMillis = System.currentTimeMillis() + (long) startOffsetMinutes * 60 * 1000;
        long endTimeMillis = startTimeMillis + (long) durationMinutes * 60 * 1000;

        Event event = buildEvent(startTimeMillis, endTimeMillis);
        event = insertEventToCalendar(event);

        String eventId = event.getId();
        scheduleEventDeletion(eventId, endTimeMillis);

        return extractGoogleMeetLink(event);
    }

    private Event buildEvent(long startTimeMillis, long endTimeMillis) {
        DateTime startDateTime = new DateTime(startTimeMillis);
        DateTime endDateTime = new DateTime(endTimeMillis);

        List<EventAttendee> attendees = getEventAttendees();

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

        ConferenceData conferenceData = createConferenceData();
        event.setConferenceData(conferenceData);

        return event;
    }

    private List<EventAttendee> getEventAttendees() {
        return Optional.ofNullable(attendeesEmails)
                .filter(emails -> emails.stream().anyMatch(email -> !email.trim().isEmpty()))
                .orElse(Collections.emptyList())
                .stream()
                .map(String::trim)
                .filter(EmailValidator::isValidEmail)
                .map(email -> new EventAttendee().setEmail(email))
                .collect(Collectors.toList());
    }

    private ConferenceData createConferenceData() {
        CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest()
                .setRequestId(UUID.randomUUID().toString())
                .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"));

        return new ConferenceData().setCreateRequest(createConferenceRequest);
    }

    private Event insertEventToCalendar(Event event) throws IOException {
        return calendar.events().insert("primary", event)
                .setConferenceDataVersion(1)
                .execute();
    }

    private void scheduleEventDeletion(String eventId, long endTimeMillis) {
        long delay = endTimeMillis - System.currentTimeMillis();
        scheduler.schedule(() -> {
            try {
                calendar.events().delete("primary", eventId).execute();
                log.info("Event deleted: {}", eventId);
            } catch (IOException e) {
                log.error("Error deleting event: {}", eventId, e);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    private String extractGoogleMeetLink(Event event) {
        ConferenceData conferenceData = event.getConferenceData();
        if (conferenceData != null && conferenceData.getEntryPoints() != null) {
            for (EntryPoint entryPoint : conferenceData.getEntryPoints()) {
                if ("video".equals(entryPoint.getEntryPointType())) {
                    return entryPoint.getUri();
                }
            }
        }

        return "No Google Meet link available";
    }
}
