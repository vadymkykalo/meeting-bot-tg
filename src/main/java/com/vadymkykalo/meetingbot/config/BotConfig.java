package com.vadymkykalo.meetingbot.config;

import com.vadymkykalo.meetingbot.service.bot.BotCommandMapping;
import com.vadymkykalo.meetingbot.service.bot.BotService;
import com.vadymkykalo.meetingbot.service.bot.BotCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class BotConfig {

    private final BotService botService;

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(botService);
        return telegramBotsApi;
    }

    @Bean
    public Map<String, BotCommand> commandMap(ApplicationContext applicationContext) {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(BotCommandMapping.class);
        return beans.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getValue().getClass().getAnnotation(BotCommandMapping.class).value(),
                        entry -> (BotCommand) entry.getValue()
                ));
    }
}
