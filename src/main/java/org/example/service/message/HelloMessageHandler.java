package org.example.service.message;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class HelloMessageHandler implements MessageHandler {
    private static final String COMMAND = "/hello";

    @Override
    public boolean canHandle(Message incomingMessage) {
        return COMMAND.equals(incomingMessage.getText());
    }

    @Override
    public SendMessage handle(Message message) {
        return new SendMessage(message.getChatId().toString(), "Привет ^_^");
    }
}
