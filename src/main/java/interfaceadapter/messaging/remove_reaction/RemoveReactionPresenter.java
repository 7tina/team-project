package interfaceadapter.messaging.remove_reaction;

import interfaceadapter.ViewManagerModel;
import interfaceadapter.messaging.ChatViewModel;
import usecase.messaging.remove_reaction.RemoveReactionOutputBoundary;
import usecase.messaging.remove_reaction.RemoveReactionOutputData;

/**
 * Presenter for the Remove Reaction use case.
 */
public class RemoveReactionPresenter implements RemoveReactionOutputBoundary {

    private final ChatViewModel chatViewModel;
    private final ViewManagerModel viewManagerModel;

    public RemoveReactionPresenter(ChatViewModel chatViewModel,
                                   ViewManagerModel viewManagerModel) {
        this.chatViewModel = chatViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(RemoveReactionOutputData outputData) {
        // Update the chat view model with updated reaction data
        chatViewModel.getState().updateMessageReaction(
                outputData.getMessageId(),
                outputData.getReactions()
        );
        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        chatViewModel.getState().setError(errorMessage);
        chatViewModel.firePropertyChange();
    }
}
