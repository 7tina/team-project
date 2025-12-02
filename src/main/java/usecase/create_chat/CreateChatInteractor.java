package usecase.create_chat;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import entity.Chat;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.UserRepository;

/**
 * Interactor for creating chat use case.
 */
public class CreateChatInteractor implements CreateChatInputBoundary {
    private static final int DEFAULT_BACKGROUND_RED = 230;
    private static final int DEFAULT_BACKGROUND_GREEN = 230;
    private static final int DEFAULT_BACKGROUND_BLUE = 230;
    private static final int MIN_GROUP_CHAT_PARTICIPANTS = 3;
    private static final int INDIVIDUAL_CHAT_PARTICIPANTS = 1;

    private final CreateChatUserDataAccessInterface userDataAccessObject;
    private final CreateChatOutputBoundary userPresenter;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    /**
     * Constructor for CreateChatInteractor.
     *
     * @param boundary the output boundary
     * @param dao the data access object
     * @param chatRepository the chat repository
     * @param userRepository the user repository
     */
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
     * Executes the create chat use case.
     *
     * @param createChatInputData the input data for creating a chat
     */
    public void execute(CreateChatInputData createChatInputData) {
        final String currentUserID = createChatInputData.getCurrentUserId();
        final List<String> participantUsernames = createChatInputData.getParticipantUsernames();
        final String groupName = createChatInputData.getGroupName();
        final boolean isGroupChat = createChatInputData.isGroupChat();

        final Optional<User> currentUserOpt = userRepository.findByUsername(currentUserID);

        if (currentUserOpt.isEmpty()) {
            handleFailure(isGroupChat, "Session error. Please log in again.");
        }
        else {
            final String currentUserId = currentUserOpt.get().getName();
            processChat(currentUserId, participantUsernames, groupName, isGroupChat);
        }
    }

    private void processChat(String currentUserId, List<String> participantUsernames,
                             String groupName, boolean isGroupChat) {
        if (!validParticipantInput(participantUsernames, isGroupChat)) {
            return;
        }

        final boolean requirementsMet = isGroupChat
                ? groupChatRequirements(groupName, participantUsernames)
                : individualChatRequirements(groupName, participantUsernames);

        if (!requirementsMet) {
            return;
        }

        final List<String> participantIds = validateUsers(currentUserId,
                participantUsernames, isGroupChat);
        if (participantIds != null) {
            createOrFindChat(currentUserId, participantIds, groupName, isGroupChat);
        }
    }

    private void createOrFindChat(String currentUserId, List<String> participantIds,
                                  String groupName, boolean isGroupChat) {
        this.userDataAccessObject.updateChatRepository(currentUserId);
        final List<Chat> allChats = chatRepository.findAll();
        final Chat chat = findOrMakeChat(allChats, participantIds, groupName, isGroupChat);

        if (chat != null) {
            final CreateChatOutputData outputData = new CreateChatOutputData(
                    isGroupChat, chat.getId(), chat.getGroupName(),
                    chat.getParticipantUserIds(), chat.getMessageIds(),
                    true, null, currentUserId);
            this.userPresenter.prepareSuccessView(outputData);
        }
    }

    @Nullable
    private Chat findOrMakeChat(List<Chat> allChats, List<String> participantIds,
                                String groupName, boolean isGroupChat) {
        Chat returnChat = findExistingChat(allChats, participantIds, groupName, isGroupChat);

        if (returnChat == null) {
            returnChat = createNewChat(participantIds, groupName);
        }

        return returnChat;
    }

    @Nullable
    private Chat findExistingChat(List<Chat> allChats, List<String> participantIds,
                                  String groupName, boolean isGroupChat) {
        for (Chat chat : allChats) {
            if (chatMatchesParticipants(chat, participantIds)) {
                if (isGroupChat && chat.getGroupName().equals(groupName)) {
                    return chat;
                }
                else if (!isGroupChat) {
                    return chat;
                }
            }
        }
        return null;
    }

    private boolean chatMatchesParticipants(Chat chat, List<String> participantIds) {
        final List<String> participants = new ArrayList<>(chat.getParticipantUserIds());
        final List<String> sortedParticipantIds = new ArrayList<>(participantIds);
        Collections.sort(participants);
        Collections.sort(sortedParticipantIds);
        return participants.size() == sortedParticipantIds.size()
                && participants.equals(sortedParticipantIds);
    }

    private Chat createNewChat(List<String> participantIds, String groupName) {
        final String chatId = UUID.randomUUID().toString();
        final Color backgroundColor = new Color(DEFAULT_BACKGROUND_RED,
                DEFAULT_BACKGROUND_GREEN, DEFAULT_BACKGROUND_BLUE);
        final Instant timeNow = Instant.now();
        final Chat newChat = new Chat(chatId, groupName, backgroundColor, timeNow);
        for (String userId : participantIds) {
            newChat.addParticipant(userId);
        }
        userDataAccessObject.saveChat(newChat);
        return newChat;
    }

    @Nullable
    private List<String> validateUsers(String currentUserId,
                                       List<String> participantUsernames,
                                       boolean isGroupChat) {
        final List<String> participantIds = new ArrayList<>();
        participantIds.add(currentUserId);

        for (String username : participantUsernames) {
            final String userId = loadAndGetUserId(username, isGroupChat);
            if (userId == null) {
                return null;
            }
            if (!participantIds.contains(userId)) {
                participantIds.add(userId);
            }
        }
        return participantIds;
    }

    @Nullable
    private String loadAndGetUserId(String username, boolean isGroupChat) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            final boolean load = this.userDataAccessObject.loadToEntity(username);
            if (!load) {
                handleFailure(isGroupChat, "User not found: " + username);
                return null;
            }
            userOpt = userRepository.findByUsername(username);
        }
        return userOpt.isPresent() ? userOpt.get().getName() : null;
    }

    private boolean validParticipantInput(List<String> participantUsernames,
                                          boolean isGroupChat) {
        final boolean isValid = participantUsernames != null && !participantUsernames.isEmpty();
        if (!isValid) {
            handleFailure(isGroupChat, "No participants provided");
        }
        return isValid;
    }

    private boolean individualChatRequirements(String groupName,
                                               List<String> participantUsernames) {
        final boolean isValid = groupName.isEmpty()
                && participantUsernames.size() == INDIVIDUAL_CHAT_PARTICIPANTS;
        if (!isValid) {
            handleFailure(false, "An error has occurred when initializing your chat");
        }
        return isValid;
    }

    private boolean groupChatRequirements(String groupName,
                                          List<String> participantUsernames) {
        if (groupName == null || groupName.trim().isEmpty()) {
            handleFailure(true, "Group name cannot be empty");
            return false;
        }
        if (participantUsernames.size() < MIN_GROUP_CHAT_PARTICIPANTS) {
            handleFailure(true, "Group chat requires at least 3 participants");
            return false;
        }
        return true;
    }

    private void handleFailure(boolean isGroupChat, String errorMessage) {
        final CreateChatOutputData outputData = new CreateChatOutputData(
                isGroupChat, null, null, null, null, false, errorMessage);
        userPresenter.prepareFailView(outputData);
    }
}
