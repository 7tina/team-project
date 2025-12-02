package usecase.groups.changegroupname;

import entity.Chat;

/**
 * Data access interface for changing group chat names.
 * Provides methods to persist chat modifications and update group names.
 */
public interface ChangeGroupNameDataAccessInterface {

    /**
     * Saves the chat entity to persistent storage.
     *
     * @param chat the chat entity to save
     */
    Chat saveChat(Chat chat);

    /**
     * Changes the group name of the specified chat.
     *
     * @param chatId the ID of the chat whose name should be changed
     * @param groupName the new group name
     */
    void changeGroupName(String chatId, String groupName);
}
