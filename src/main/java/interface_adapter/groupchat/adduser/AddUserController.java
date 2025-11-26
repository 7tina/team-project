package interface_adapter.groupchat.adduser;

import use_case.groups.adduser.AddUserInputBoundary;
import use_case.groups.adduser.AddUserInputData;

public class AddUserController {
    private final AddUserInputBoundary addUserInteractor;

    public AddUserController(AddUserInputBoundary addUserInteractor) {
        this.addUserInteractor = addUserInteractor;
    }

    public void execute(String chatId, String usernameToAdd) {
        AddUserInputData inputData = new AddUserInputData(chatId, usernameToAdd);
        addUserInteractor.execute(inputData);
    }
}