package use_case.messaging.send_m;

import entity.Message;

public interface SendMessageDataAccessInterface {

    Message sendMessage(Message message);

    void updateChat(String chatId, String messageId);
}
