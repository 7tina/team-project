package entity.ports;

import entity.Chat;
import entity.User;
import java.util.Optional;

public interface ChatRepository {
    Chat save(Chat chat);
    Optional<Chat> findById(String chatId);
    java.util.List<Chat> findAll();
}
