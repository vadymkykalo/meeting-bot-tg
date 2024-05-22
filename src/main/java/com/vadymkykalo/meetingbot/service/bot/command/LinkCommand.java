package com.vadymkykalo.meetingbot.service.bot.command;

import com.vadymkykalo.meetingbot.service.bot.BotCommand;
import com.vadymkykalo.meetingbot.service.bot.BotCommandMapping;
import com.vadymkykalo.meetingbot.service.googlecalendar.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@BotCommandMapping("/link")
@Component
@RequiredArgsConstructor
public class LinkCommand implements BotCommand {

    private final GoogleCalendarService googleCalendarService;

    @Value("${meeting.messages.success}")
    private String successMessage;
    @Value("${meeting.messages.error}")
    private String errorMessage;

    @Override
    public SendMessage execute(Update update) {
        long chatId = update.getMessage().getChatId();

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        try {
            String meetLink = googleCalendarService.createGoogleMeetLink();
            message.setText(successMessage + meetLink);
        } catch (Exception e) {
            log.error("Error creating Google Meet link", e);
            message.setText(errorMessage);
        }

        return message;
    }
}