package org.example.service.message;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class StartMessageHandler implements MessageHandler {

    private static final String COMMAND = "/start";

    @Override
    public boolean canHandle(Message msg) {
        return COMMAND.equals(msg.getText());
    }

    @Override
    public SendMessage handle(Message message) {
        return new SendMessage(message.getChatId().toString(), """
                Я бот, который скидывает вам прогноз погоды.
                Вы можете отправлять мне как название вашего города, так и название города + название улицы.
                Пример: Москва улица Пушкина 41.
                Информация о погоде может быть не точной.

                Разработчик: @Teigt""");
    }
}
