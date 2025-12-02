package interfaceadapter.groupchat.adduser;

import usecase.groups.adduser.AddUserInputBoundary;
import usecase.groups.adduser.AddUserInputData;

public class AddUserController {
    private final AddUserInputBoundary addUserInteractor;

    public AddUserController(AddUserInputBoundary addUserInteractor) {
        this.addUserInteractor = addUserInteractor;
    }

    /**
     * Adds a user to a specified chat by delegating the operation to the interactor.
     * @param chatId the ID of the chat to which the user should be added
     * @param usernameToAdd the username of the user to add to the chat
     */
    public void execute(String chatId, String usernameToAdd) {
        final AddUserInputData inputData = new AddUserInputData(chatId, usernameToAdd);
        addUserInteractor.execute(inputData);
    }
}
