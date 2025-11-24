package interface_adapter.groupchat;

import interface_adapter.ViewManagerModel;
import use_case.groups.CreateGroupChatOutputBoundary;
import use_case.groups.CreateGroupChatOutputData;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupChatPresenter implements CreateGroupChatOutputBoundary {

    private final GroupChatViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public CreateGroupChatPresenter(GroupChatViewModel viewModel, ViewManagerModel viewManagerModel) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(CreateGroupChatOutputData outputData) {
        GroupChatState state = new GroupChatState();
        state.setChatId(outputData.getChatId());
        state.setGroupName(outputData.getGroupName());

        // Use getParticipantUserIds from outputData
        if (outputData.getParticipantUserIds() != null) {
            state.setParticipants(outputData.getParticipantUserIds());
        } else {
            state.setParticipants(new ArrayList<>());
        }

        // MessageIds will be empty for a new group chat
        state.setMessageIds(new ArrayList<>());

        state.setSuccess(true);
        state.setError(null);

        viewModel.setState(state);

        // Fire property change (already fires with "state" property name)
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(CreateGroupChatOutputData outputData) {
        GroupChatState state = new GroupChatState();
        state.setSuccess(false);
        state.setError(outputData.getErrorMessage());

        viewModel.setState(state);

        // Fire property change (already fires with "state" property name)
        viewModel.firePropertyChange();
    }
}