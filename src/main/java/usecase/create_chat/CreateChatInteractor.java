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

public class CreateChatInteractor implements CreateChatInputBoundary {
    protected final CreateChatUserDataAccessInterface userDataAccessObject;
    protected final CreateChatOutputBoundary userPresenter;
    protected final ChatRepository chatRepository;
    protected final UserRepository userRepository;

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
                // Validation failed, error already reported
            }
            else if (!validParticipantInput(participantUsernames)) {
                // Validation failed, error already reported
            }
            else if (!individualChatRequirements(groupName, participantUsernames)) {
                // Validation failed, error already reported
            }
            else {
                // Load target user into userRepository
                final List<String> participantIds = validateUsers(
                        currentUserOpt.get().getName(), participantUsernames, false);

                if (participantIds != null) {
                    // Find existing chat with all participants
                    this.userDataAccessObject.updateChatRepository(currentUserOpt.get().getName());
                    final List<Chat> allChats = chatRepository.findAll();
                    final Chat chat = findOrMakeIndividualChat(allChats, participantIds, groupName);

                    final String chatId = chat.getId();
                    final String chatName = chat.getGroupName();
                    final List<String> chatUsers = chat.getParticipantUserIds();
                    final List<String> chatMessages = chat.getMessageIds();

                    final CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                            false, chatId, chatName, chatUsers, chatMessages, true, null,
                            currentUserOpt.get().getName());
                    this.userPresenter.prepareSuccessView(createChatOutputData);
                }
            }
        }
        catch (Exception ex) {
            // Handle any unexpected errors
            final CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                    false, null, null, null, null, false,
                    "Failed to create chat: " + ex.getMessage()
            );
            userPresenter.prepareFailView(createChatOutputData);
        }
    }

    @Nullable
    private Chat findOrMakeIndividualChat(List<Chat> allChats, List<String> participantIds, String groupName) {
        Chat returnChat = null;
        if (!allChats.isEmpty()) {
            for (Chat chat : allChats) {
                final List<String> participants = chat.getParticipantUserIds();
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

        List<String> result = participantIds;

        for (String username : participantUsernames) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                final boolean load = this.userDataAccessObject.loadToEntity(username);
                if (!load) {
                    final CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                            isGroup, null, null, null, null, false,
                            "Null user not found."
                    );
                    userPresenter.prepareFailView(createChatOutputData);
                    result = null;
                    break;
                }
                else {
                    // After successful load, query the repository again to get the user
                    userOpt = userRepository.findByUsername(username);
                }
            }

            // Now userOpt should be present (either was already there, or just loaded)
            if (userOpt.isPresent()) {
                final String userId = userOpt.get().getName();
                if (!participantIds.contains(userId)) {
                    participantIds.add(userId);
                }
            }
        }
        return result;
    }

    protected boolean validParticipantInput(List<String> participantUsernames) {
        final boolean isValid = participantUsernames != null && !participantUsernames.isEmpty();
        if (!isValid) {
            final CreateChatOutputData outputData = new CreateChatOutputData(
                    false, null, null, null, null, false,
                    "No participants provided"
            );
            userPresenter.prepareFailView(outputData);
        }
        return isValid;
    }

    protected boolean individualChatRequirements(String groupName, List<String> participantUsernames) {
        final boolean isValid = groupName.isEmpty() && participantUsernames.size() == 1;
        if (!isValid) {
            final CreateChatOutputData outputData = new CreateChatOutputData(
                    false, null, null, null, null, false,
                    "An error has occurred when initializing your chat");
            System.out.println(groupName + participantUsernames);
            this.userPresenter.prepareFailView(outputData);
        }
        return isValid;
    }

    @Nullable
    protected Optional<User> validateUser(String currentUserID) {
        final Optional<User> currentUserOpt = userRepository.findByUsername(currentUserID);
        Optional<User> result = currentUserOpt;

        if (currentUserOpt.isEmpty()) {
            final CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                    false, null, null, null, null, false,
                    "Session error. Please log in again."
            );
            userPresenter.prepareFailView(createChatOutputData);
            result = null;
        }
        return result;
    }
}
