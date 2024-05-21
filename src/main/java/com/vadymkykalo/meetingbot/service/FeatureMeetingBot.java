package com.vadymkykalo.meetingbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeatureMeetingBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    private final GoogleCalendarService googleCalendarService;

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
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            log.info("Received message: {} from chat ID: {}", messageText, chatId);

            if (messageText.equalsIgnoreCase("/link")) {
                try {
                    String meetLink = googleCalendarService.createGoogleMeetLink();
                    SendMessage message = new SendMessage();
                    message.setChatId(String.valueOf(chatId));
                    message.setText("Link Google Meet: " + meetLink);
                    execute(message);
                    log.info("Google Meet link created and sent to chat ID: {}", chatId);
                } catch (TelegramApiException | IOException | GeneralSecurityException e) {
                    log.error("Error creating Google Meet link", e);
                    SendMessage errorMessage = new SendMessage();
                    errorMessage.setChatId(String.valueOf(chatId));
                    errorMessage.setText("Sorry, there was an error creating the Google Meet link. Please try again later.");
                    try {
                        execute(errorMessage);
                    } catch (TelegramApiException ex) {
                        log.error("Error sending error message to chat ID: {}", chatId, ex);
                    }
                }
            }
        }
    }
}
