package usecase.accesschat;

import entity.Chat;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.UserRepository;
import usecase.create_chat.CreateChatOutputData;

import java.util.List;
import java.util.Optional;

public class AccessChatInteractor implements AccessChatInputBoundary {
    private final AccessChatDataAccessInterface accessChatDataAccess;
    private final AccessChatOutputBoundary accessChatPresenter;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public AccessChatInteractor(AccessChatDataAccessInterface accessChatDataAccess,
                                AccessChatOutputBoundary accessChatPresenter,
                                UserRepository userRepository, ChatRepository chatRepository) {
        this.accessChatDataAccess = accessChatDataAccess;
        this.accessChatPresenter = accessChatPresenter;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    /**
     * Execute the Access Chat use case.
     * @param accessChatInputData The required input data to execute the use case.
     */
    public void execute(AccessChatInputData accessChatInputData) {
        final String currentUserID = accessChatInputData.getUserId();
        final String chatId = accessChatInputData.getChatId();

        final Optional<User> currentUserOpt = userRepository.findByUsername(currentUserID);

        if (currentUserOpt.isEmpty()) {
            accessChatPresenter.prepareFailView("Session error. Please log in again.");
            return;
        }

        final String currentUserId = currentUserOpt.get().getName();

        accessChatDataAccess.updateChatRepository(currentUserId);

        final Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            accessChatPresenter.prepareFailView("Chat not found.");
            return;
        }

        final Chat chat = chatOpt.get();
        final String name = chat.getGroupName();
        final List<String> userIds = chat.getParticipantUserIds();
        final List<String> messageIds = chat.getMessageIds();
        final boolean isGroup = userIds.size() > 2;
        final AccessChatOutputData outputData = new AccessChatOutputData(
                isGroup, chatId, name, userIds, messageIds, currentUserId
        );
        accessChatPresenter.prepareSuccessView(outputData);
    }
}
