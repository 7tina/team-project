package interface_adapter.groupchat.changegroupname;

import use_case.groups.changegroupname.ChangeGroupNameInputBoundary;
import use_case.groups.changegroupname.ChangeGroupNameInputData;

/**
 * Controller for changing a group chat's name.
 */
public class ChangeGroupNameController {

    private final ChangeGroupNameInputBoundary interactor;

    public ChangeGroupNameController(ChangeGroupNameInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the change group name use case.
     * @param chatId The ID of the chat to rename
     * @param newGroupName The new name for the group
     */
    public void execute(String chatId, String newGroupName) {
        ChangeGroupNameInputData inputData = new ChangeGroupNameInputData(chatId, newGroupName);
        interactor.execute(inputData);
    }
}