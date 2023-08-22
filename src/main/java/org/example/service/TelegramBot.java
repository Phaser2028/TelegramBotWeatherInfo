package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.config.TelegramBotProperties;
import org.example.service.message.MessageHandler;
import org.example.util.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramBotProperties.Bot telegramBotProperties;

    private final WeatherData weatherData;

    private final List<MessageHandler> messageHandlers;

    @Autowired
    public TelegramBot(TelegramBotProperties telegramBotProperties, WeatherData weatherData, List<MessageHandler> messageHandlers){
        this.telegramBotProperties = telegramBotProperties.getBot();
        this.weatherData = weatherData;
        this.messageHandlers = messageHandlers;
    }

    @Override
    public void onUpdateReceived(Update update) {

        messageHandlers.stream()
                .filter(handler -> update.hasMessage())
                .filter(handler -> update.getMessage().hasText())
                .filter(handler -> handler.canHandle(update.getMessage()))
                .findFirst()
                .ifPresentOrElse(
                        command -> sendMessage(command.handle(update.getMessage())),
                        () -> {
                            try {
                                sendLocation(weatherData.getCoordinates(update.getMessage()));
                                sendMessage(weatherData.getWeatherInfo(update.getMessage()));
                            } catch (JsonProcessingException | NullPointerException e) {
                                sendMessageError(update.getMessage());
                            }
                        }
                );
    }

    private void sendMessage(SendMessage incomingMessage) {
        try {
            execute(incomingMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendLocation(SendLocation incomingMessage) {
        try {
            execute(incomingMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageError(Message incomingMessage) {
        SendMessage message = new SendMessage(incomingMessage.getChatId().toString(), "Произошла ошибка. Попробуйте изменить название города или подождать.");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getName();
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.getToken();
    }
}