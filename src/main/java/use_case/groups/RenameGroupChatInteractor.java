package use_case.groups;

import entity.ports.ChatRepository;
import java.util.Optional;

/**
 * Interactor for renaming a group chat.
 * Implements the business logic for changing a chat group's name.
 */
public class RenameGroupChatInteractor implements ChangeGroupNameInputBoundary {

    private final ChatRepository chatRepository;
    private final ChangeGroupNameOutputBoundary outputBoundary;

    public RenameGroupChatInteractor(
            ChatRepository chatRepository,
            ChangeGroupNameOutputBoundary outputBoundary) {
        this.chatRepository = chatRepository;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(ChangeGroupNameInputData inputData) {
        try {
            // Validate input
            if (inputData.getNewGroupName() == null || inputData.getNewGroupName().trim().isEmpty()) {
                ChangeGroupNameOutputData outputData = new ChangeGroupNameOutputData(
                        inputData.getChatId(),
                        null,
                        false,
                        "Group name cannot be empty"
                );
                outputBoundary.prepareFailView(outputData);
                return;
            }

            // Validate group name length
            if (inputData.getNewGroupName().length() > 100) {
                ChangeGroupNameOutputData outputData = new ChangeGroupNameOutputData(
                        inputData.getChatId(),
                        null,
                        false,
                        "Group name is too long (max 100 characters)"
                );
                outputBoundary.prepareFailView(outputData);
                return;
            }

            // Retrieve the chat from repository
            Optional<entity.Chat> chatOpt = chatRepository.findById(inputData.getChatId());

            if (chatOpt.isEmpty()) {
                ChangeGroupNameOutputData outputData = new ChangeGroupNameOutputData(
                        inputData.getChatId(),
                        null,
                        false,
                        "Chat not found"
                );
                outputBoundary.prepareFailView(outputData);
                return;
            }

            entity.Chat chat = chatOpt.get();

            // Update the group name
            String trimmedName = inputData.getNewGroupName().trim();
            chat.setGroupName(trimmedName);

            // Save the updated chat
            chat = chatRepository.save(chat);

            // Prepare success output
            ChangeGroupNameOutputData outputData = new ChangeGroupNameOutputData(
                    chat.getId(),
                    chat.getGroupName(),
                    true,
                    null
            );

            outputBoundary.prepareSuccessView(outputData);

        } catch (Exception e) {
            // Handle any unexpected errors
            ChangeGroupNameOutputData outputData = new ChangeGroupNameOutputData(
                    inputData.getChatId(),
                    null,
                    false,
                    "Failed to rename group: " + e.getMessage()
            );
            outputBoundary.prepareFailView(outputData);
        }
    }
}