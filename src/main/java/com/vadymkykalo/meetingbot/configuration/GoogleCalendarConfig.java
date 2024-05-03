package com.vadymkykalo.meetingbot.configuration;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

@Configuration
public class GoogleCalendarConfig {

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    @Bean
    public Calendar calendarService() throws IOException, GeneralSecurityException {

        InputStream in = getClass().getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource 'credentials.json' is not found");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/calendar"));

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Meeting bot")
                .build();
    }
}
