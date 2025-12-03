package entity.repo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import entity.Chat;
import entity.ports.ChatRepository;

public class InMemoryChatRepository implements ChatRepository {

    private final Map<String, Chat> chats = new HashMap<>();

    @Override
    public Chat save(Chat chat) {
        chats.put(chat.getId(), chat);
        return chat;
    }

    @Override
    public Optional<Chat> findById(String chatId) {
        return Optional.ofNullable(chats.get(chatId));
    }

    @Override
    public java.util.List<Chat> findAll() {
        return new java.util.ArrayList<>(chats.values());
    }

    @Override
    public void clear() {
        chats.clear();
    }
}
