package usecase.accesschat;

import java.util.List;
import java.util.Optional;

import entity.Chat;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.UserRepository;

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
     *
     * @param accessChatInputData The required input data to execute the use case.
     */
    public void execute(AccessChatInputData accessChatInputData) {
        final String currentUserID = accessChatInputData.getUserId();
        final String chatId = accessChatInputData.getChatId();

        final Optional<User> currentUserOpt = userRepository.findByUsername(currentUserID);

        if (currentUserOpt.isEmpty()) {
            accessChatPresenter.prepareFailView("Session error. Please log in again.");
        }
        else {
            final String currentUserId = currentUserOpt.get().getName();
            accessChatDataAccess.updateChatRepository(currentUserId);

            final Optional<Chat> chatOpt = chatRepository.findById(chatId);
            if (chatOpt.isEmpty()) {
                accessChatPresenter.prepareFailView("Chat not found.");
            }
            else {
                final Chat chat = chatOpt.get();
                final AccessChatOutputData outputData = new AccessChatOutputData(
                        chat.getParticipantUserIds().size() > 2,
                        chatId,
                        chat.getGroupName(),
                        chat.getParticipantUserIds(),
                        chat.getMessageIds(),
                        currentUserId
                );
                accessChatPresenter.prepareSuccessView(outputData);
            }
        }
    }
}
