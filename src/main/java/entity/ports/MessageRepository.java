package entity.ports;

import entity.Message;
import java.util.Optional;
import java.util.List;

public interface MessageRepository {
    Optional<Message> findById(String id);
    Message save(Message message);
    List<Message> findByChatId(String chatId);
    void deleteById(String id);
}