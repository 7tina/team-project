package usecase.create_chat;

import entity.Chat;
import entity.User;
import entity.ports.UserRepository;
import entity.ports.ChatRepository;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class CreateChatInteractor implements CreateChatInputBoundary{
    private final CreateChatUserDataAccessInterface userDataAccessObject;
    private final CreateChatOutputBoundary userPresenter;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public CreateChatInteractor(CreateChatOutputBoundary boundary,
                                CreateChatUserDataAccessInterface dao,
                                ChatRepository chatRepository,
                                UserRepository userRepository) {
        this.userDataAccessObject = dao;
        this.userPresenter = boundary;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public void execute(CreateChatInputData createChatInputData){
        final String currentUserID = createChatInputData.getCurrentUserId();
        final List<String> participantUsernames = createChatInputData.getParticipantUsernames();
        final String groupName = createChatInputData.getGroupName();
        final boolean isGroupChat = createChatInputData.isGroupChat();

        try {
            Optional<User> currentUserOpt = userRepository.findByUsername(currentUserID);

            if (currentUserOpt.isEmpty()) {
                CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                        isGroupChat, null, null, null, null, false,
                        "Session error. Please log in again."
                );
                userPresenter.prepareFailView(createChatOutputData);
                return;
            }

            String currentUserId = currentUserOpt.get().getName();

            // Validate input
            if (!validParticipantInput(participantUsernames, isGroupChat)) return;

            // Validate groupchat requirements
            if (isGroupChat) {
                if (!groupChatRequirements(groupName, participantUsernames)) return;
            }
            else {
                if (!individualChatRequirements(groupName, participantUsernames)) return;
            }

            // Load target user into userRepository
            List<String> participantIds = validateUsers(currentUserId, participantUsernames, isGroupChat);
            if (participantIds == null) return;

            // Find existing chat with all participants
            Chat chat = null;
            this.userDataAccessObject.updateChatRepository(currentUserId);
            List<Chat> allChats = chatRepository.findAll();
            chat = findOrMakeChat(allChats, participantIds, groupName, isGroupChat);
            if (chat == null) return;
            String chatId = chat.getId();
            String chatName = chat.getGroupName();
            List<String> chatUsers = chat.getParticipantUserIds();
            List<String> chatMessages = chat.getMessageIds();

            final CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                    isGroupChat, chatId, chatName, chatUsers, chatMessages, true, null, currentUserId);
            this.userPresenter.prepareSuccessView(createChatOutputData);
        }
        catch (Exception e) {
            // Handle any unexpected errors
            CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                    isGroupChat, null, null, null, null, false,
                    "Failed to create chat: " + e.getMessage()
            );
            userPresenter.prepareFailView(createChatOutputData);
        }
    }

    @Nullable
    private Chat findOrMakeChat(List<Chat> allChats, List<String> participantIds,
                                String groupName, boolean isGroupChat) {
        Chat returnChat = null;
        String chatName = null;
        if (!allChats.isEmpty()) {
            for (Chat chat : allChats) {
                java.util.List<String> participants = chat.getParticipantUserIds();
                Collections.sort(participants);
                Collections.sort(participantIds);
                if (participants.size() == participantIds.size() && participants.equals(participantIds)) {
                    // For group chats, also check if the group name matches
                    if (isGroupChat) {
                        if (chat.getGroupName().equals(groupName)) {
                            returnChat = chat;
                            chatName = chat.getGroupName();
                            break;
                        }
                        // If participants match but name is different, continue searching
                    } else {
                        // For individual chats, same participants = same chat
                        returnChat = chat;
                        chatName = chat.getGroupName();
                        break;
                    }
                }
            }
            if (returnChat != null && isGroupChat) {
                CreateChatOutputData outputData = new CreateChatOutputData(
                        true, null, null, null, null,
                        false, "Group chat already exists by the name of: " + chatName);
                userPresenter.prepareFailView(outputData);
                return null;
            }
        }
        if (returnChat == null) {
            // No existing chat found, create new one
            String chatId = UUID.randomUUID().toString();
            Color backgroundColor = new Color(230, 230, 230);
            Instant timeNow = Instant.now();
            Chat newChat = new Chat(chatId, groupName, backgroundColor, timeNow);
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
    private List<String> validateUsers(String currentUserId, List<String> participantUsernames, boolean isGroupChat) {
        List<String> participantIds = new ArrayList<>();
        participantIds.add(currentUserId); // Add creator

        for (String username : participantUsernames) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                boolean load = this.userDataAccessObject.loadToEntity(username);
                if (!load) {
                    CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                            isGroupChat, null, null, null, null, false,
                            "Null user not found."
                    );
                    userPresenter.prepareFailView(createChatOutputData);
                    return null;
                }
            } else {
                String userId = userOpt.get().getName();
                if (!participantIds.contains(userId)) {
                    participantIds.add(userId);
                }
            }
        }
        return participantIds;
    }

    private boolean validParticipantInput(List<String> participantUsernames, boolean isGroupChat) {
        if (participantUsernames == null || participantUsernames.isEmpty()) {
            CreateChatOutputData outputData = new CreateChatOutputData(
                    isGroupChat, null, null, null, null, false,
                    "No participants provided"
            );
            userPresenter.prepareFailView(outputData);
            return false;
        }
        return true;
    }

    private boolean individualChatRequirements(String groupName, List<String> participantUsernames) {
        if ((!groupName.isEmpty()) || participantUsernames.size() != 1) {
            CreateChatOutputData outputData = new CreateChatOutputData(
                    false, null, null, null, null, false,
                    "An error has occurred when initializing your chat");
            System.out.println(groupName + participantUsernames);
            this.userPresenter.prepareFailView(outputData);
            return false;
        }
        return true;
    }

    private boolean groupChatRequirements(String groupName, List<String> participantUsernames) {
        if (groupName == null || groupName.trim().isEmpty()){
            CreateChatOutputData outputData = new CreateChatOutputData(
                    true, null, null, null, null, false,
                    "Group name cannot be empty");
            this.userPresenter.prepareFailView(outputData);
            return false;
        }
        else if (groupName.length() > 100) {
            CreateChatOutputData outputData = new CreateChatOutputData(
                    true, null, null, null, null,false,
                    "Group name is too long (max 100 characters)"
            );
            userPresenter.prepareFailView(outputData);
            return false;
        }
        if (participantUsernames.size() < 2) {
            CreateChatOutputData outputData = new CreateChatOutputData(
                    true, null, null, null, null,false,
                    "Group chat requires at least 2 participants"
            );
            userPresenter.prepareFailView(outputData);
            return false;
        }
        else if (participantUsernames.size() > 10) {
            CreateChatOutputData outputData = new CreateChatOutputData(
                    true, null, null, null, null,false,
                    "Group chat handles no more than 10 participants"
            );
            userPresenter.prepareFailView(outputData);
            return false;
        }
        return true;
    }
}