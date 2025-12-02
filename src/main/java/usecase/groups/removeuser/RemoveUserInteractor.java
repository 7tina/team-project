package usecase.groups.removeuser;

import java.util.Optional;

import entity.Chat;
import entity.ports.ChatRepository;

/**
 * Interactor (use case) for removing a user from a group chat.
 */
public class RemoveUserInteractor implements RemoveUserInputBoundary {
    private static final int MIN_PARTICIPANTS = 3;

    private final ChatRepository chatRepository;
    private final RemoveUserOutputBoundary outputBoundary;
    private final RemoveUserDataAccessInterface dataAccess;

    public RemoveUserInteractor(
            ChatRepository chatRepository,
            RemoveUserOutputBoundary outputBoundary,
            RemoveUserDataAccessInterface dataAccess) {
        this.chatRepository = chatRepository;
        this.outputBoundary = outputBoundary;
        this.dataAccess = dataAccess;
    }

    @Override
    public void execute(RemoveUserInputData inputData) {
        String errorMessage = null;
        RemoveUserOutputData outputData = null;

        try {
            final String chatId = inputData.getChatId();
            final String usernameToRemove = inputData.getUsernameToRemove();

            // Validate username is not empty
            if (usernameToRemove == null || usernameToRemove.trim().isEmpty()) {
                errorMessage = "Username cannot be empty";
            }
            else {
                // Retrieve the chat from repository
                final Optional<Chat> chatOpt = chatRepository.findById(chatId);

                if (chatOpt.isEmpty()) {
                    errorMessage = "Chat not found";
                }
                else {
                    final Chat chat = chatOpt.get();
                    final String userIdToRemove = dataAccess.getUserIdByUsername(usernameToRemove.trim());

                    if (userIdToRemove == null) {
                        errorMessage = "User not found: " + usernameToRemove;
                    }
                    else if (!chat.getParticipantUserIds().contains(userIdToRemove)) {
                        errorMessage = "User is not a member of this chat";
                    }
                    else if (chat.getParticipantUserIds().size() <= MIN_PARTICIPANTS) {
                        errorMessage = "Minimum number of participants is 3";
                    }
                    else {
                        // Remove the user from the chat
                        chat.removeParticipant(userIdToRemove);
                        dataAccess.removeUser(chatId, userIdToRemove);
                        dataAccess.saveChat(chat);

                        // Prepare success output
                        outputData = new RemoveUserOutputData(chatId, usernameToRemove.trim());
                    }
                }
            }
        }
        catch (IllegalArgumentException | IllegalStateException ex) {
            errorMessage = "Failed to remove user: " + ex.getMessage();
        }

        // Single exit point
        if (errorMessage != null) {
            outputBoundary.prepareFailView(errorMessage);
        }
        else {
            outputBoundary.prepareSuccessView(outputData);
        }
    }
}
