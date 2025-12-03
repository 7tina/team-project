package usecase.groups.creategroupchat;

import java.awt.Color;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import entity.Chat;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.UserRepository;
import usecase.create_chat.CreateChatInputData;
import usecase.create_chat.CreateChatInteractor;
import usecase.create_chat.CreateChatOutputBoundary;
import usecase.create_chat.CreateChatOutputData;
import usecase.create_chat.CreateChatUserDataAccessInterface;

public class CreateGroupChatInteractor extends CreateChatInteractor {
    private static final int MIN_GROUP_SIZE = 3;
    private static final int MIN_VALID_MAX_USERS = 2;

    private final Integer maxUsers;

    public CreateGroupChatInteractor(CreateChatOutputBoundary boundary,
                                     CreateChatUserDataAccessInterface dao,
                                     ChatRepository chatRepository,
                                     UserRepository userRepository,
                                     Integer maxUsers) {
        super(boundary, dao, chatRepository, userRepository);
        if (maxUsers > MIN_VALID_MAX_USERS) {
            this.maxUsers = maxUsers;
        }
        else {
            this.maxUsers = MIN_GROUP_SIZE;
        }
    }

    @Override
    public void execute(CreateChatInputData createChatInputData) {
        final String currentUserID = createChatInputData.getCurrentUserId();
        final List<String> participantUsernames = createChatInputData.getParticipantUsernames();
        final String groupName = createChatInputData.getGroupName();

        try {
            final Optional<User> currentUserOpt = validateUser(currentUserID);
            if (currentUserOpt == null) {
                return;
            }

            final String currentUserId = currentUserOpt.get().getName();

            // Validate input not null
            if (!super.validParticipantInput(participantUsernames)) {
                return;
            }

            boolean isGroup = participantUsernames.size() > 1;

            // Validate chat requirements
            if (isGroup) {
                if (!groupChatRequirements(groupName, participantUsernames)) {
                    return;
                }
            }
            else {
                if (!super.individualChatRequirements(groupName, participantUsernames)) {
                    return;
                }
            }

            // Load target user into userRepository
            final List<String> participantIds = validateUsers(currentUserId, participantUsernames, isGroup);
            if (participantIds == null) {
                return;
            }

            isGroup = participantIds.size() > 2;

            // Find existing chat with all participants
            Chat chat = null;
            this.userDataAccessObject.updateChatRepository(currentUserId);
            final List<Chat> allChats = chatRepository.findAll();
            chat = findOrMakeChat(allChats, participantIds, groupName, isGroup);
            if (chat == null) {
                return;
            }
            final String chatId = chat.getId();
            final String chatName = chat.getGroupName();
            final List<String> chatUsers = chat.getParticipantUserIds();
            final List<String> chatMessages = chat.getMessageIds();

            final CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                    isGroup, chatId, chatName, chatUsers, chatMessages, true, null, currentUserId);
            this.userPresenter.prepareSuccessView(createChatOutputData);
        }
        catch (IllegalArgumentException | IllegalStateException ex) {
            // Handle any unexpected errors
            final CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                    false, null, null, null, null, false,
                    "Failed to create chat: " + ex.getMessage()
            );
            userPresenter.prepareFailView(createChatOutputData);
        }
    }

    private boolean groupChatRequirements(String groupName, List<String> participantUsernames) {
        final int minGroupNum = 3;
        final int maxGroupName = 100;

        if (groupName == null || groupName.trim().isEmpty()) {
            final CreateChatOutputData outputData = new CreateChatOutputData(
                    true, null, null, null, null, false,
                    "Group name cannot be empty");
            this.userPresenter.prepareFailView(outputData);
            return false;
        }
        else if (groupName.length() > maxGroupName) {
            final CreateChatOutputData outputData = new CreateChatOutputData(
                    true, null, null, null, null, false,
                    "Group name is too long (max 100 characters)"
            );
            userPresenter.prepareFailView(outputData);
            return false;
        }
        if (participantUsernames.size() < minGroupNum) {
            final CreateChatOutputData outputData = new CreateChatOutputData(
                    true, null, null, null, null, false,
                    "Group chat requires at least 3 participants"
            );
            userPresenter.prepareFailView(outputData);
            return false;
        }
        else if (participantUsernames.size() > maxUsers) {
            final CreateChatOutputData outputData = new CreateChatOutputData(
                    true, null, null, null, null, false,
                    "Group chat max participants is " + maxUsers
            );
            userPresenter.prepareFailView(outputData);
            return false;
        }
        return true;
    }

    @Nullable
    private Chat findOrMakeChat(List<Chat> allChats, List<String> participantIds,
            String groupName, boolean isGroup) {
        Chat returnChat = null;
        String chatName = null;
        if (!allChats.isEmpty()) {
            for (Chat chat : allChats) {
                final java.util.List<String> participants = chat.getParticipantUserIds();
                Collections.sort(participants);
                Collections.sort(participantIds);
                if (participants.size() == participantIds.size()
                        && participants.equals(participantIds)
                        && groupName.equals(chat.getGroupName())) {
                    returnChat = chat;
                    chatName = chat.getGroupName();
                    break;
                }
            }
            if (returnChat != null && isGroup) {
                final CreateChatOutputData outputData = new CreateChatOutputData(
                        isGroup, null, null, null, null,
                        false, "Chat already exists by the name of: " + chatName);
                userPresenter.prepareFailView(outputData);
                return null;
            }
        }
        if (returnChat == null) {
            // No existing chat found, create new one
            final String chatId = UUID.randomUUID().toString();
            final Color backgroundColor = new Color(230, 230, 230);
            final Instant timeNow = Instant.now();
            final Chat newChat = new Chat(chatId, groupName, backgroundColor, timeNow);
            // Add all participants
            for (String userId : participantIds) {
                newChat.addParticipant(userId);
            }
            userDataAccessObject.saveChat(newChat);
            returnChat = newChat;
        }
        return returnChat;
    }
}
