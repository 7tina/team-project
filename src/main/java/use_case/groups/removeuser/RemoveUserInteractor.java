package use_case.groups.removeuser;

import data_access.FireBaseUserDataAccessObject;
import entity.Chat;
import entity.ports.ChatRepository;
import java.util.Optional;

/**
 * Interactor for removing a user from a group chat.
 * Implements the business logic for removing a participant from a chat.
 */
public class RemoveUserInteractor implements RemoveUserInputBoundary {

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
        try {
            String chatId = inputData.getChatId();
            String usernameToRemove = inputData.getUsernameToRemove();

            // Validate input
            if (usernameToRemove == null || usernameToRemove.trim().isEmpty()) {
                outputBoundary.prepareFailView("Username cannot be empty");
                return;
            }

            // Retrieve the chat from repository
            Optional<Chat> chatOpt = chatRepository.findById(chatId);

            if (chatOpt.isEmpty()) {
                outputBoundary.prepareFailView("Chat not found");
                return;
            }

            Chat chat = chatOpt.get();

            // Get the user ID for the username
            String userIdToRemove = dataAccess.getUserIdByUsername(usernameToRemove.trim());
            if (userIdToRemove == null) {
                outputBoundary.prepareFailView("User not found: " + usernameToRemove);
                return;
            }

            // Check if user is in the chat
            if (!chat.getParticipantUserIds().contains(userIdToRemove)) {
                outputBoundary.prepareFailView("User is not a member of this chat");
                return;
            }

            // Check if this is the last participant
            if (chat.getParticipantUserIds().size() <= 3) {
                outputBoundary.prepareFailView("Minimum number of participants is 3");
                return;
            }

            // Remove the user from the chat
            chat.removeParticipant(userIdToRemove);

            // Save the updated chat
            dataAccess.removeUser(chatId, userIdToRemove);
            dataAccess.saveChat(chat);

            // Prepare success output
            RemoveUserOutputData outputData = new RemoveUserOutputData(chatId, usernameToRemove.trim());
            outputBoundary.prepareSuccessView(outputData);

        } catch (Exception e) {
            // Handle any unexpected errors
            outputBoundary.prepareFailView("Failed to remove user: " + e.getMessage());
        }
    }
}