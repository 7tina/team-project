package usecase.create_chat;

import entity.Chat;

public interface CreateChatUserDataAccessInterface {
    boolean loadToEntity(String username);

    void updateChatRepository(String username);

    void saveChat(Chat chat);
}
