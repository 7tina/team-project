package usecase.groups.adduser;

import java.util.List;
import java.util.Optional;

import entity.Chat;
import entity.ports.ChatRepository;

/**
 * Interactor (use case) for adding a user to a group chat.
 * Implements the business logic for validating and executing the add user operation.
 */
public class AddUserInteractor implements AddUserInputBoundary {
    private static final int MAX_PARTICIPANTS = 10;

    private final ChatRepository chatRepository;
    private final AddUserOutputBoundary outputBoundary;
    private final AddUserDataAccessInterface dataAccess;

    /**
     * Constructs an AddUserInteractor with required dependencies.
     *
     * @param chatRepository the repository for accessing chat entities
     * @param outputBoundary the output boundary for presenting results
     * @param dataAccess the data access interface for user and chat operations
     */
    public AddUserInteractor(
            ChatRepository chatRepository,
            AddUserOutputBoundary outputBoundary,
            AddUserDataAccessInterface dataAccess) {
        this.chatRepository = chatRepository;
        this.outputBoundary = outputBoundary;
        this.dataAccess = dataAccess;
    }

    /**
     * Executes the add user use case.
     *
     * @param inputData the input data containing chat ID and username to add
     */
    @Override
    public void execute(AddUserInputData inputData) {
        String errorMessage = null;
        AddUserOutputData outputData = null;

        try {
            final String chatId = inputData.getChatId();
            final String usernameToAdd = inputData.getUsernameToAdd();

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
                        else if (currentParticipants.size() == MAX_PARTICIPANTS) {
                            errorMessage = "Max number of participants reached";
                        }
                        else {
                            chat.addParticipant(userIdToAdd);
                            dataAccess.addUser(chatId, userIdToAdd);
                            final Chat saved = dataAccess.saveChat(chat);
                            outputData = new AddUserOutputData(saved.getId(), usernameToAdd.trim());
                        }
                    }
                }
            }
        }
        catch (IllegalArgumentException | IllegalStateException ex) {
            errorMessage = "Failed to add user: " + ex.getMessage();
        }

        if (errorMessage != null) {
            outputBoundary.prepareFailView(errorMessage);
        }
        else {
            outputBoundary.prepareSuccessView(outputData);
        }
    }
}
