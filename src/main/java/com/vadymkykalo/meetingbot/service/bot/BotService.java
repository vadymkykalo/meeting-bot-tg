package com.vadymkykalo.meetingbot.service.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotService extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    private final Map<String, BotCommand> commandMap;

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

            String messageText = update.getMessage().getText().trim().toLowerCase();

            BotCommand command = commandMap.get(messageText);
            if (null != command) {
                SendMessage message = command.execute(update);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    log.error("Error sending message to chat ID: {}", message.getChatId(), e);
                }
            } else {
                log.info("Unsupported command: {} from chat ID: {}", messageText, update.getMessage().getChatId());
            }
        }
    }
}
