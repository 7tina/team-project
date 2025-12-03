package usecase.recent_chat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import entity.Chat;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;

public class RecentChatsInteractor implements RecentChatsInputBoundary {
    private final RecentChatsOutputBoundary recentChatsPresenter;
    private final RecentChatsUserDataAccessInterface recentChatsUserDataAccess;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public RecentChatsInteractor(RecentChatsOutputBoundary recentChatsPresenter,
                                 RecentChatsUserDataAccessInterface recentChatsUserDataAccess,
                                 MessageRepository messageRepository,
                                 UserRepository userRepository,
                                 ChatRepository chatRepository) {
        this.recentChatsPresenter = recentChatsPresenter;
        this.recentChatsUserDataAccess = recentChatsUserDataAccess;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    @Override
    public void execute(RecentChatsInputData recentChatsInputData) {
        try {
            final String currentUsername = recentChatsInputData.getUserId();
            messageRepository.clear();
            recentChatsUserDataAccess.updateChatRepository(currentUsername);
            final List<Chat> allChats = chatRepository.findAll();
            final List<Chat> myChats = new ArrayList<>();
            for (Chat chat : allChats) {
                final List<String> participants = chat.getParticipantUserIds();
                if (participants.contains(currentUsername)) {
                    myChats.add(chat);
                }
            }

            myChats.sort(new Comparator<Chat>() {
                @Override
                public int compare(Chat c1, Chat c2) {
                    final Instant t1 = c1.getLastMessage();
                    final Instant t2 = c2.getLastMessage();
                    if (t1 == null && t2 == null) {
                        return 0;
                    }
                    if (t1 == null) {
                        return 1;
                    }
                    if (t2 == null) {
                        return -1;
                    }
                    return t2.compareTo(t1);
                }
            });

            final HashMap<String, String> nameToChatIds = new HashMap<>();
            final List<String> chatNames = new ArrayList<>();
            for (Chat chat : myChats) {
                String name = chat.getGroupName();
                if (chat.getParticipantUserIds().size() == 2) {
                    final List<String> users = chat.getParticipantUserIds();
                    name = users.get(0).equals(currentUsername) ? users.get(1) : users.get(0);
                }
                if (chatNames.contains(name)) {
                    name = name + "(copy)";
                }
                nameToChatIds.put(name, chat.getId());
                chatNames.add(name);
            }

            final RecentChatsOutputData recentChatsOutputData = new RecentChatsOutputData(chatNames, nameToChatIds);
            recentChatsPresenter.prepareSuccessView(recentChatsOutputData);
        }
        catch (Exception e) {
            recentChatsPresenter.prepareFailView(e.getMessage());
        }

    }
}
