package usecase.groups.changegroupname;

import java.util.Optional;

import entity.Chat;
import entity.ports.ChatRepository;

/**
 * Interactor (use case) for renaming a group chat.
 */
public class ChangeGroupNameInteractor implements ChangeGroupNameInputBoundary {
    private static final int MAX_GROUP_NAME_LENGTH = 100;

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

        try {
            final String chatId = inputData.getChatId();
            final String newGroupName = inputData.getNewGroupName();

            // Validate group name is not empty
            if (newGroupName == null || newGroupName.trim().isEmpty()) {
                errorMessage = "Group name cannot be empty";
            }
            // Validate group name length
            else if (newGroupName.length() > MAX_GROUP_NAME_LENGTH) {
                errorMessage = "Group name is too long (max 100 characters)";
            }
            else {
                // Retrieve the chat from repository
                final Optional<Chat> chatOpt = chatRepository.findById(chatId);

                if (chatOpt.isEmpty()) {
                    errorMessage = "Chat not found";
                }
                else {
                    final Chat chat = chatOpt.get();
                    final String trimmedName = newGroupName.trim();

                    // Update the group name
                    chat.setGroupName(trimmedName);
                    dataAccess.changeGroupName(chat.getId(), trimmedName);
                    dataAccess.saveChat(chat);

                    // Prepare success output
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

        // Single exit point
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
