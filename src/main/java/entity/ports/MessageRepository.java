package entity.ports;

import java.util.List;
import java.util.Optional;

import entity.Message;

/**
 * Repository interface for managing messages.
 */
public interface MessageRepository {

    /**
     * Finds a message by its id.
     *
     * @param id the id of the message
     * @return an Optional containing the message if found, otherwise empty
     */
    Optional<Message> findById(String id);

    /**
     * Saves the given message.
     *
     * @param message the message to save
     * @return the saved message
     */
    Message save(Message message);

    /**
     * Finds all messages that belong to the given chat.
     *
     * @param chatId the id of the chat
     * @return a list of messages in the chat
     */
    List<Message> findByChatId(String chatId);

    /**
     * Deletes the message with the given id.
     *
     * @param id the id of the message to delete
     */
    void deleteById(String id);

    /**
     * Removes all messages.
     */
    void clear();
}
