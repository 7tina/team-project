package entity.repo;

import entity.Chat;
import entity.ports.ChatRepository;

import java.util.*;

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
}