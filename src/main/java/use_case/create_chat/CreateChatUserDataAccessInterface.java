package use_case.create_chat;

import entity.Chat;

public interface CreateChatUserDataAccessInterface {
    boolean loadToEntity(String username);

    void updateChatRepository(String username);

    Chat saveChat(Chat chat);
}
