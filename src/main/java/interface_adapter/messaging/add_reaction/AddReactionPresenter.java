package interface_adapter.messaging.add_reaction;

import interface_adapter.ViewManagerModel;
import interface_adapter.messaging.ChatViewModel;
import use_case.messaging.add_reaction.AddReactionOutputBoundary;
import use_case.messaging.add_reaction.AddReactionOutputData;

/**
 * Presenter for the Add Reaction use case.
 */
public class AddReactionPresenter implements AddReactionOutputBoundary {

    private final ChatViewModel chatViewModel;
    private final ViewManagerModel viewManagerModel;

    public AddReactionPresenter(ChatViewModel chatViewModel,
                                ViewManagerModel viewManagerModel) {
        this.chatViewModel = chatViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(AddReactionOutputData outputData) {
        // Update ONLY the reactions, don't trigger full reload
        chatViewModel.getState().updateMessageReaction(
                outputData.getMessageId(),
                outputData.getReactions()
        );

        // Fire property change to update UI
        chatViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        chatViewModel.getState().setError(errorMessage);
        chatViewModel.firePropertyChange();
    }
}
