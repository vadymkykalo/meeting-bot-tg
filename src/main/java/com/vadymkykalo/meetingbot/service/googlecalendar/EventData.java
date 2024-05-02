package com.vadymkykalo.meetingbot.service.googlecalendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventData {
    private String summary;
    private String description;
    private String dateStart;
    private String timeStart;
    private List<String> attendeesEmails;
}
