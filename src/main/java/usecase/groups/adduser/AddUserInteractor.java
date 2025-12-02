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

            if (usernameToAdd == null || usernameToAdd.trim().isEmpty()) {
                outputBoundary.prepareFailView("Username cannot be empty");
                return;
            }

            Optional<Chat> chatOpt = chatRepository.findById(chatId);

            if (chatOpt.isEmpty()) {
                outputBoundary.prepareFailView("Chat not found");
                return;
            }

            Chat chat = chatOpt.get();

            String userIdToAdd = dataAccess.getUserIdByUsername(usernameToAdd.trim());
            if (userIdToAdd == null) {
                outputBoundary.prepareFailView("User not found: " + usernameToAdd);
                return;
            }

            List<String> currentParticipants = chat.getParticipantUserIds();

            if (currentParticipants.contains(userIdToAdd)) {
                outputBoundary.prepareFailView("User is already a member of this chat");
                return;
            }

            if (currentParticipants.size() == 10) {
                outputBoundary.prepareFailView("Max number of participants reached");
                return;
            }

            chat.addParticipant(userIdToAdd);
            dataAccess.addUser(chatId, userIdToAdd);
            Chat saved = dataAccess.saveChat(chat);
            AddUserOutputData outputData = new AddUserOutputData(saved.getId(), usernameToAdd.trim());
            outputBoundary.prepareSuccessView(outputData);

        } catch (Exception e) {
            outputBoundary.prepareFailView("Failed to add user: " + e.getMessage());
        }
    }
}