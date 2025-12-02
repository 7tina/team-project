package usecase.groups.changegroupname;

import entity.Chat;

public interface ChangeGroupNameDataAccessInterface {
    Chat saveChat(Chat chat);

    void changeGroupName(String chatId, String groupName);
}
