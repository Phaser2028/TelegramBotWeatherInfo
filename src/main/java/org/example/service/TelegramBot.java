package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.util.WeatherData;
import org.example.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class TelegramBot extends TelegramLongPollingBot {


    private final BotConfig config;


    private final WeatherData weatherData;


    @Autowired
    public TelegramBot(BotConfig config, WeatherData weatherData) {
        this.config = config;
        this.weatherData = weatherData;
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.getMessage().getChatId();


        if (update.hasMessage() && update.getMessage().hasText()) {

            if (update.getMessage().getText().equals("/start"))
                sendMessage(chatId, """
                        Я бот, который скидывает вам прогноз погоды.
                        Вы можете отправлять мне как название вашего города, так и название города + название улицы.
                        Пример: Москва улица Пушкина 41.
                        Информация о погоде может быть не точной.

                        Разработчик: @Teigt""");
            else if (update.getMessage().getText().equals("/help"))
                sendMessage(chatId, "Просто отправьте мне сообщение с названием города или с названием города + названием улицы, и я отправлю вам информацию о погоде");

            else {
                try {
                    sendMessage(chatId, weatherData.getWeatherInfo(update.getMessage().getText()));
                } catch (JsonProcessingException e) {
                    sendMessage(chatId, "произошла ошибка");
                    throw new RuntimeException(e);
                }
            }

        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}