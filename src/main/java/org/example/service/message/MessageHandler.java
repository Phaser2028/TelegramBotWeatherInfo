package org.example.service.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageHandler {
    boolean canHandle(Message incomingMessage);

    SendMessage handle(Message message);
}