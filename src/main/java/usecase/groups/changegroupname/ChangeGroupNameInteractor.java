package usecase.groups.changegroupname;

import java.util.Optional;

import entity.Chat;
import entity.ports.ChatRepository;

/**
 * Interactor (use case) for renaming a group chat.
 */
public class ChangeGroupNameInteractor implements ChangeGroupNameInputBoundary {
    private final ChatRepository chatRepository;
    private final ChangeGroupNameOutputBoundary outputBoundary;
    private final ChangeGroupNameDataAccessInterface dataAccess;

    public ChangeGroupNameInteractor(
            ChatRepository chatRepository,
            ChangeGroupNameOutputBoundary outputBoundary,
            ChangeGroupNameDataAccessInterface dataAccess) {
        this.chatRepository = chatRepository;
        this.outputBoundary = outputBoundary;
        this.dataAccess = dataAccess;
    }

    @Override
    public void execute(ChangeGroupNameInputData inputData) {
        String errorMessage = null;
        ChangeGroupNameOutputData outputData = null;
        final int maxGroupName = 100;

        try {
            final String chatId = inputData.getChatId();
            final String newGroupName = inputData.getNewGroupName();

            if (newGroupName == null || newGroupName.trim().isEmpty()) {
                errorMessage = "Group name cannot be empty";
            }
            else if (newGroupName.trim().length() > maxGroupName) {
                errorMessage = "Group name cannot exceed 100 characters";
            }
            else {
                final Optional<Chat> chatOpt = chatRepository.findById(chatId);

                if (chatOpt.isEmpty()) {
                    errorMessage = "Chat not found";
                }
                else {
                    final Chat chat = chatOpt.get();
                    final String trimmedName = newGroupName.trim();

                    chat.setGroupName(trimmedName);
                    dataAccess.changeGroupName(chat.getId(), trimmedName);
                    dataAccess.saveChat(chat);

                    outputData = new ChangeGroupNameOutputData(
                            chat.getId(),
                            chat.getGroupName(),
                            true,
                            null
                    );
                }
            }
        }
        catch (IllegalArgumentException | IllegalStateException ex) {
            errorMessage = "Failed to rename group: " + ex.getMessage();
        }

        if (errorMessage != null) {
            final ChangeGroupNameOutputData failureData = new ChangeGroupNameOutputData(
                    inputData.getChatId(),
                    null,
                    false,
                    errorMessage
            );
            outputBoundary.prepareFailView(failureData);
        }
        else {
            outputBoundary.prepareSuccessView(outputData);
        }
    }
}
