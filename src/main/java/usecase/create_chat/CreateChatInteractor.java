package usecase.create_chat;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import entity.Chat;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.UserRepository;

public class CreateChatInteractor implements CreateChatInputBoundary {
    protected final CreateChatUserDataAccessInterface userDataAccessObject;
    protected final CreateChatOutputBoundary userPresenter;
    protected final ChatRepository chatRepository;
    protected final UserRepository userRepository;

    public CreateChatInteractor(CreateChatOutputBoundary boundary,
                                CreateChatUserDataAccessInterface dao,
                                ChatRepository chatRepository,
                                UserRepository userRepository) {
        this.userDataAccessObject = dao;
        this.userPresenter = boundary;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    /**
     * Executes the use case by verifying information on the input data,
     * making a Chat entity, and storing it in database.
     * @param createChatInputData the input data for this use case
     */
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

            // Validate input
            if (!validParticipantInput(participantUsernames)) {
                return;
            }

            // Validate chat requirements
            if (!individualChatRequirements(groupName, participantUsernames)) {
                return;
            }

            // Load target user into userRepository
            final List<String> participantIds = validateUsers(currentUserId, participantUsernames, false);
            if (participantIds == null) {
                return;
            }

            // Find existing chat with all participants
            Chat chat = null;
            this.userDataAccessObject.updateChatRepository(currentUserId);
            final List<Chat> allChats = chatRepository.findAll();
            chat = findOrMakeIndividualChat(allChats, participantIds, groupName);
            if (chat == null) {
                return;
            }
            final String chatId = chat.getId();
            final String chatName = chat.getGroupName();
            final List<String> chatUsers = chat.getParticipantUserIds();
            final List<String> chatMessages = chat.getMessageIds();

            final CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                    false, chatId, chatName, chatUsers, chatMessages, true, null, currentUserId);
            this.userPresenter.prepareSuccessView(createChatOutputData);
        }
        catch (Exception e) {
            // Handle any unexpected errors
            final CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                    false, null, null, null, null, false,
                    "Failed to create chat: " + e.getMessage()
            );
            userPresenter.prepareFailView(createChatOutputData);
        }
    }

    @Nullable
    private Chat findOrMakeIndividualChat(List<Chat> allChats, List<String> participantIds, String groupName) {
        Chat returnChat = null;
        if (!allChats.isEmpty()) {
            for (Chat chat : allChats) {
                final java.util.List<String> participants = chat.getParticipantUserIds();
                Collections.sort(participants);
                Collections.sort(participantIds);
                if (participants.size() == 2 && participantIds.size() == 2
                        && participants.equals(participantIds)
                        && groupName.equals(chat.getGroupName())) {
                    returnChat = chat;
                    break;
                }
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

    @Nullable
    protected List<String> validateUsers(String currentUserId,
                                       List<String> participantUsernames, boolean isGroup) {
        final List<String> participantIds = new ArrayList<>();
        // Add creator
        participantIds.add(currentUserId);

        for (String username : participantUsernames) {
            final Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                final boolean load = this.userDataAccessObject.loadToEntity(username);
                if (!load) {
                    final CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                            isGroup, null, null, null, null, false,
                            "Null user not found."
                    );
                    userPresenter.prepareFailView(createChatOutputData);
                    return null;
                }
            }
            else {
                final String userId = userOpt.get().getName();
                if (!participantIds.contains(userId)) {
                    participantIds.add(userId);
                }
            }
        }
        return participantIds;
    }

    protected boolean validParticipantInput(List<String> participantUsernames) {
        if (participantUsernames == null || participantUsernames.isEmpty()) {
            final CreateChatOutputData outputData = new CreateChatOutputData(
                    false, null, null, null, null, false,
                    "No participants provided"
            );
            userPresenter.prepareFailView(outputData);
            return false;
        }
        return true;
    }

    protected boolean individualChatRequirements(String groupName, List<String> participantUsernames) {
        if (!groupName.isEmpty() || participantUsernames.size() != 1) {
            final CreateChatOutputData outputData = new CreateChatOutputData(
                    false, null, null, null, null, false,
                    "An error has occurred when initializing your chat");
            System.out.println(groupName + participantUsernames);
            this.userPresenter.prepareFailView(outputData);
            return false;
        }
        return true;
    }

    @Nullable
    protected Optional<User> validateUser(String currentUserID) {
        final Optional<User> currentUserOpt = userRepository.findByUsername(currentUserID);

        if (currentUserOpt.isEmpty()) {
            final CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                    false, null, null, null, null, false,
                    "Session error. Please log in again."
            );
            userPresenter.prepareFailView(createChatOutputData);
            return null;
        }
        return currentUserOpt;
    }
}