package use_case.groups.adduser;

import entity.Chat;

public interface AddUserDataAccessInterface {

    String getUserIdByUsername(String username);

    void addUser(String chatId, String userId);

    Chat saveChat(Chat chat);
}
