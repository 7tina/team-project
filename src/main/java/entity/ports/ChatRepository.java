package entity.ports;

import java.util.List;
import java.util.Optional;

import entity.Chat;

/**
 * Repository interface for managing chats.
 */
public interface ChatRepository {

    /**
     * Saves the given chat.
     *
     * @param chat the chat to save
     * @return the saved chat
     */
    Chat save(Chat chat);

    /**
     * Finds a chat by its id.
     *
     * @param chatId the id of the chat
     * @return an Optional containing the chat if found, otherwise empty
     */
    Optional<Chat> findById(String chatId);

    /**
     * Returns all chats.
     *
     * @return a list of all chats
     */
    List<Chat> findAll();

    /**
     * Removes all chats stored. Will be empty after this method is called.
     */
    void clear();
}
