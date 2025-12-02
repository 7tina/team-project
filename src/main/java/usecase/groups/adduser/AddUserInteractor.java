package usecase.groups.adduser;

import java.util.List;
import java.util.Optional;

import entity.Chat;
import entity.ports.ChatRepository;

/**
 * Interactor (use case) for adding a user to a group chat.
 */
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
            final String chatId = inputData.getChatId();
            final String usernameToAdd = inputData.getUsernameToAdd();
            String errorMessage = "";

            if (usernameToAdd == null || usernameToAdd.trim().isEmpty()) {
                errorMessage = "Username cannot be empty";
            }
            else {
                final Optional<Chat> chatOpt = chatRepository.findById(chatId);

                if (chatOpt.isEmpty()) {
                    errorMessage = "Chat not found";
                }
                else {
                    final Chat chat = chatOpt.get();
                    final String userIdToAdd = dataAccess.getUserIdByUsername(usernameToAdd.trim());

                    if (userIdToAdd == null) {
                        errorMessage = "User not found: " + usernameToAdd;
                    }
                    else {
                        final List<String> currentParticipants = chat.getParticipantUserIds();

                        if (currentParticipants.contains(userIdToAdd)) {
                            errorMessage = "User is already a member of this chat";
                        }
                        else {
                            chat.addParticipant(userIdToAdd);
                            dataAccess.addUser(chatId, userIdToAdd);
                            dataAccess.saveChat(chat);
                            final AddUserOutputData outputData = new AddUserOutputData(chat.getId(), usernameToAdd.trim());
                            outputBoundary.prepareSuccessView(outputData);
                        }
                    }
                }
            }

            if (!errorMessage.isEmpty()) {
                outputBoundary.prepareFailView(errorMessage);
            }
        }
        catch (Exception e) {
            outputBoundary.prepareFailView("Encountered Error: " + e.getMessage());
        }
    }
}
