package com.vadymkykalo.meetingbot.configuration;

import com.vadymkykalo.meetingbot.service.telegrambot.FeatureMeetingBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new FeatureMeetingBot());
            return api;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register bot", e);
        }
    }
}
