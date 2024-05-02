package com.vadymkykalo.meetingbot.service.googlecalendar;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private static final String APPLICATION_NAME = "Telegram Bot Google Meet Integration";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @NotNull
    @Override
    public String createEventWithParticipants(@NotNull EventData eventData) throws GeneralSecurityException, IOException {
        Calendar service = getCalendarService();

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

        event = service.events().insert("primary", event).setConferenceDataVersion(1).execute();

        return event.getHangoutLink();
    }

    private Calendar getCalendarService() throws GeneralSecurityException, IOException {
        GoogleCredential credential = GoogleCredential
                .fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                .createScoped(List.of("https://www.googleapis.com/auth/calendar"));

        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
