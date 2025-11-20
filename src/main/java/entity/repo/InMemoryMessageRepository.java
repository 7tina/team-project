package entity.repo;

import entity.Message;
import entity.ports.MessageRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMessageRepository implements MessageRepository {

    private final Map<String, Message> messages = new HashMap<>();

    @Override
    public Optional<Message> findById(String id) {
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public Message save(Message msg) {
        messages.put(msg.getId(), msg);
        return msg;
    }

    @Override
    public List<Message> findByChatId(String chatId) {
        return messages.values().stream()
                .filter(m -> m.getChatId().equals(chatId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        messages.remove(id);
    }
}
