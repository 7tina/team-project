package usecase.groups.adduser;

import entity.Chat;
import entity.ports.ChatRepository;

import java.util.List;
import java.util.Optional;

public class AddUserInteractor implements AddUserInputBoundary {
    private final ChatRepository chatRepository;
    private final AddUserOutputBoundary outputBoundary;
    private final AddUserDataAccessInterface dataAccess;

    public AddUserInteractor(
            ChatRepository chatRepository,
            AddUserOutputBoundary outputBoundary,
            AddUserDataAccessInterface dataAccess) {
        this.chatRepository = chatRepository;
        this.outputBoundary = outputBoundary;
        this.dataAccess = dataAccess;
    }

    @Override
    public void execute(AddUserInputData inputData) {
        try {
            String chatId = inputData.getChatId();
            String usernameToAdd = inputData.getUsernameToAdd();

            // Validate input
            if (usernameToAdd == null || usernameToAdd.trim().isEmpty()) {
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
            String userIdToAdd = dataAccess.getUserIdByUsername(usernameToAdd.trim());
            if (userIdToAdd == null) {
                outputBoundary.prepareFailView("User not found: " + usernameToAdd);
                return;
            }

            List<String> currentParticipants = chat.getParticipantUserIds();

            // Check if user is already in the chat
            if (currentParticipants.contains(userIdToAdd)) {
                outputBoundary.prepareFailView("User is already a member of this chat");
                return;
            }

            if (currentParticipants.size() == 10) {
                outputBoundary.prepareFailView("Max number of participants reached");
                return;
            }

            // Add the user to the chat
            chat.addParticipant(userIdToAdd);
            dataAccess.addUser(chatId, userIdToAdd);

            // Save the updated chat
            Chat saved = dataAccess.saveChat(chat);

            // Prepare success output
            AddUserOutputData outputData = new AddUserOutputData(saved.getId(), usernameToAdd.trim());
            outputBoundary.prepareSuccessView(outputData);

        } catch (Exception e) {
            // Handle any unexpected errors
            outputBoundary.prepareFailView("Failed to add user: " + e.getMessage());
        }
    }
}