package com.vadymkykalo.meetingbot.service.telegrambot;

import com.vadymkykalo.meetingbot.service.googlecalendar.EventData;
import com.vadymkykalo.meetingbot.service.googlecalendar.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.TaskScheduler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeatureMeetingBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    private final GoogleCalendarService calendarService;
    private final TaskScheduler taskScheduler;

    private final Set<String> selectedUsers = new HashSet<>();

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            handleCommand(message);
        }
    }

    private void handleCommand(Message message) {
        String text = message.getText();
        long chatId = message.getChatId();

        if (text.startsWith("/setup_meeting")) {
            handleSetupMeeting(text, chatId);
        } else if (text.startsWith("/add_user")) {
            handleAddUser(message);
        }
    }

    private void handleSetupMeeting(String command, long chatId) {
        String[] parts = command.split(" ", 4);
        if (parts.length < 4) {
            sendMessage(chatId, "Invalid command format. Use /setup_meeting YYYY-MM-DD HH:MM \"Description\"");
            return;
        }
        try {
            EventData eventData = EventData.builder()
                    .summary("Meeting")
                    .description(parts[3])
                    .dateStart(parts[1])
                    .timeStart(parts[2])
                    .attendeesEmails(new ArrayList<>(selectedUsers))
                    .build();

            String link = calendarService.createEventWithParticipants(eventData);
            sendMessage(chatId, "The meeting is scheduled! Google Meet link: " + link);
            scheduleReminder(chatId, parts[1] + " " + parts[2], link, selectedUsers);
            selectedUsers.clear();
        } catch (Exception e) {
            log.error(e.getMessage());
            sendMessage(chatId, "Error creating a meeting: " + e.getMessage());
        }
    }

    private void handleAddUser(Message message) {
        User user = message.getFrom();
        selectedUsers.add(user.getUserName());
        sendMessage(message.getChatId(), "User @" + user.getUserName() + " added to the list of participants.");
    }

    private void scheduleReminder(long chatId, String dateTime, String link, Set<String> users) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime meetingTime = LocalDateTime.parse(dateTime, formatter);
        LocalDateTime reminderTime = meetingTime.minusMinutes(10);
        Instant instant = reminderTime.atZone(ZoneId.systemDefault()).toInstant();

        taskScheduler.schedule(() -> sendMessage(chatId, buildReminderMessage(link, users)), instant);
    }

    private String buildReminderMessage(String link, Set<String> users) {
        String taggedUsers = users.stream().map(u -> "@" + u).collect(Collectors.joining(" "));
        return "Reminder: The meeting starts in 10 minutes. Participants: " + taggedUsers + ". Join here: " + link;
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending a message", e);
        }
    }
}
