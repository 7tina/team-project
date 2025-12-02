package usecase.groups.adduser;

import java.util.List;
import java.util.Optional;

import entity.Chat;
import entity.ports.ChatRepository;

/**
 * Interactor (use case) for adding a user to a group chat.
 */
public class AddUserInteractor implements AddUserInputBoundary {
    private static final int MAX_PARTICIPANTS = 10;

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
        String errorMessage = null;
        AddUserOutputData outputData = null;

        try {
            final String chatId = inputData.getChatId();
            final String usernameToAdd = inputData.getUsernameToAdd();

            if (usernameToAdd == null || usernameToAdd.trim().isEmpty()) {
                outputBoundary.prepareFailView("Username cannot be empty");
                return;
            }

            final Optional<Chat> chatOpt = chatRepository.findById(chatId);

            if (chatOpt.isEmpty()) {
                outputBoundary.prepareFailView("Chat not found");
                return;
            }

            final Chat chat = chatOpt.get();
            final String userIdToAdd = dataAccess.getUserIdByUsername(usernameToAdd.trim());

            if (userIdToAdd == null) {
                outputBoundary.prepareFailView("User not found: " + usernameToAdd);
                return;
            }

            final List<String> currentParticipants = chat.getParticipantUserIds();

            if (currentParticipants.contains(userIdToAdd)) {
                outputBoundary.prepareFailView("User is already a member of this chat");
            }
            else {
                if (currentParticipants.contains(userIdToAdd)) {
                    errorMessage = "User is already a member of this chat";
                }
                else if (currentParticipants.size() == MAX_PARTICIPANTS) {
                    errorMessage = "Max number of participants reached";
                }
                else {
                    chat.addParticipant(userIdToAdd);
                    dataAccess.addUser(chatId, userIdToAdd);
                    dataAccess.saveChat(chat);
                    outputData = new AddUserOutputData(chat.getId(), usernameToAdd.trim());
                    outputBoundary.prepareSuccessView(outputData);

                }
            }
        } catch (Exception e) {
            outputBoundary.prepareFailView("Failed to add user: " + e.getMessage());
        }
    }
}
