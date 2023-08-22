package org.example.service.message;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class HelpMessageHandler implements MessageHandler {
    private static final String COMMAND = "/help";

    @Override
    public boolean canHandle(Message incomingMessage) {
        return COMMAND.equals(incomingMessage.getText());
    }

    @Override
    public SendMessage handle(Message message) {
        return new SendMessage(message.getChatId().toString(), "Просто отправьте мне сообщение с названием города или с названием города + названием улицы, и я отправлю вам информацию о погоде");
    }
}
