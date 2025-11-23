package use_case.create_chat;

import entity.Chat;
import entity.User;
import entity.ports.UserRepository;
import entity.ports.ChatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        final String targetUserID = createChatInputData.getTargetUserId();

        try {
            Optional<User> currentUserOpt = userRepository.findByUsername(currentUserID);

            if (currentUserOpt.isEmpty()) {
                CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                        null, null, null, null, false,
                        "Session error. Please log in again."
                );
                userPresenter.prepareFailView(createChatOutputData);
                return;
            }

            String currentUserId = currentUserOpt.get().getName();

            // Load target user into userRepository
            String targetUserId;
            Optional<User> targetUserOpt = userRepository.findByUsername(targetUserID);
            if (targetUserOpt.isEmpty()) {
                boolean load = this.userDataAccessObject.loadToEntity(targetUserID);
                if (!load) {
                    CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                            null, null, null, null, false,
                            "Null user not found."
                    );
                    userPresenter.prepareFailView(createChatOutputData);
                    return;
                }
                targetUserId = targetUserID;
            } else {
                targetUserId = targetUserOpt.get().getName();
            }

            // Find existing chat with both participants (and only these two)
            String chatId = null;
            List<String> chatUsers = new ArrayList<>();
            List<String> chatMessages = new ArrayList<>();
            this.userDataAccessObject.updateChatRepository(currentUserId);
            java.util.List<Chat> allChats = chatRepository.findAll();

            if (!allChats.isEmpty()) {
                for (Chat chat : allChats) {
                    java.util.List<String> participants = chat.getParticipantUserIds();  // Changed from getParticipants()
                    if (participants.size() == 2 &&
                            participants.contains(currentUserId) &&
                            participants.contains(targetUserId)) {
                        chatId = chat.getId();
                        chatUsers = chat.getParticipantUserIds();
                        chatMessages = chat.getMessageIds();
                        break;
                    }
                }
            }
            if (chatId == null) {
                // No existing chat found, create new one
                Chat newChat = new Chat(UUID.randomUUID().toString(), targetUserID);
                newChat.addParticipant(currentUserId);
                newChat.addParticipant(targetUserId);
                Chat chat = userDataAccessObject.saveChat(newChat);
                chatId = chat.getId();
                chatUsers = chat.getParticipantUserIds();
                chatMessages = chat.getMessageIds();
            }
            final CreateChatOutputData createChatOutputData = new CreateChatOutputData(chatId, targetUserId,
                    chatUsers, chatMessages, true, null);
            this.userPresenter.prepareSuccessView(createChatOutputData);
        }
        catch (Exception e) {
            // Handle any unexpected errors
            CreateChatOutputData createChatOutputData = new CreateChatOutputData(
                    null, null, null, null, false,
                    "Failed to create chat: " + e.getMessage()
            );
            userPresenter.prepareFailView(createChatOutputData);
        }
    }
}
