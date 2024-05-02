package com.vadymkykalo.meetingbot.configuration;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleCalendarConfig {

    @Bean
    public Calendar calendarService() throws IOException, GeneralSecurityException {
        InputStream in = GoogleCalendarConfig.class.getResourceAsStream("/credentials.json");
        assert in != null;
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(in).createScoped(Collections.singleton(CalendarScopes.CALENDAR_EVENTS));

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Telegram Bot Google Meet Integration")
                .build();
    }
}
