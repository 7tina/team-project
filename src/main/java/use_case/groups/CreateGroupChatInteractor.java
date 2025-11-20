package use_case.groups;

import entity.Chat;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interactor for creating a group chat.
 * Handles the business logic for creating a new group chat with multiple participants.
 */
public class CreateGroupChatInteractor implements CreateGroupChatInputBoundary {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final CreateGroupChatOutputBoundary outputBoundary;

    public CreateGroupChatInteractor(ChatRepository chatRepository,
                                     UserRepository userRepository,
                                     CreateGroupChatOutputBoundary outputBoundary) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(CreateGroupChatInputData inputData) {
        try {
            // Validate input
            if (inputData.getParticipantUsernames() == null || inputData.getParticipantUsernames().isEmpty()) {
                CreateGroupChatOutputData outputData = new CreateGroupChatOutputData(
                        null, null, null, false,
                        "No participants provided"
                );
                outputBoundary.prepareFailView(outputData);
                return;
            }

            if (inputData.getParticipantUsernames().size() < 2) {
                CreateGroupChatOutputData outputData = new CreateGroupChatOutputData(
                        null, null, null, false,
                        "Group chat requires at least 2 participants"
                );
                outputBoundary.prepareFailView(outputData);
                return;
            }

            // Validate group name
            String groupName = inputData.getGroupName();
            if (groupName == null || groupName.trim().isEmpty()) {
                CreateGroupChatOutputData outputData = new CreateGroupChatOutputData(
                        null, null, null, false,
                        "Group name cannot be empty"
                );
                outputBoundary.prepareFailView(outputData);
                return;
            }

            if (groupName.length() > 100) {
                CreateGroupChatOutputData outputData = new CreateGroupChatOutputData(
                        null, null, null, false,
                        "Group name is too long (max 100 characters)"
                );
                outputBoundary.prepareFailView(outputData);
                return;
            }

            // Verify creator exists
            Optional<User> creatorOpt = userRepository.findByUsername(inputData.getCreatorUserId());
            if (creatorOpt.isEmpty()) {
                CreateGroupChatOutputData outputData = new CreateGroupChatOutputData(
                        null, null, null, false,
                        "Creator user not found"
                );
                outputBoundary.prepareFailView(outputData);
                return;
            }

            // Resolve usernames to user IDs
            List<String> participantIds = new ArrayList<>();
            participantIds.add(inputData.getCreatorUserId()); // Add creator

            for (String username : inputData.getParticipantUsernames()) {
                Optional<User> userOpt = userRepository.findByUsername(username);
                if (userOpt.isEmpty()) {
                    CreateGroupChatOutputData outputData = new CreateGroupChatOutputData(
                            null, null, null, false,
                            "User not found: " + username
                    );
                    outputBoundary.prepareFailView(outputData);
                    return;
                }

                String userId = userOpt.get().getName();
                if (!participantIds.contains(userId)) {
                    participantIds.add(userId);
                }
            }

            // Create the group chat
            String chatId = UUID.randomUUID().toString();
            Chat chat = new Chat(chatId);
            chat.setGroupName(groupName.trim());

            // Add all participants
            for (String userId : participantIds) {
                chat.addParticipant(userId);
            }

            // Save the chat
            chat = chatRepository.save(chat);

            // Prepare success output
            CreateGroupChatOutputData outputData = new CreateGroupChatOutputData(
                    chat.getId(),
                    chat.getGroupName(),
                    participantIds,
                    true,
                    null
            );

            outputBoundary.prepareSuccessView(outputData);

        } catch (Exception e) {
            // Handle any unexpected errors
            CreateGroupChatOutputData outputData = new CreateGroupChatOutputData(
                    null, null, null, false,
                    "Failed to create group chat: " + e.getMessage()
            );
            outputBoundary.prepareFailView(outputData);
        }
    }
}