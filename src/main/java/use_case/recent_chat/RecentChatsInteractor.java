package use_case.recent_chat;

import entity.Chat;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
            messageRepository.clear();
            recentChatsUserDataAccess.updateChatRepository(recentChatsInputData.getUserId());
            List<Chat> allChats = chatRepository.findAll();
            List<Chat> myChats = new ArrayList<>();
            for (Chat chat : allChats) {
                List<String> participants = chat.getParticipantUserIds();
                if (participants.contains(recentChatsInputData.getUserId())) {
                    myChats.add(chat);
                }
            }

            myChats.sort(new Comparator<Chat>() {
                @Override
                public int compare(Chat c1, Chat c2) {
                    Instant t1 = c1.getLastMessage();
                    Instant t2 = c2.getLastMessage();
                    if (t1 == null && t2 == null) return 0;
                    if (t1 == null) return 1;
                    if (t2 == null) return -1;
                    return t2.compareTo(t1);
                }
            });

            HashMap<String, String> nameToChatIds = new HashMap<>();
            List<String> chatNames = new ArrayList<>();
            List<String> chatNameExist = new ArrayList<>();
            for (Chat chat : myChats) {
                nameToChatIds.put(chat.getGroupName(), chat.getId());
                chatNames.add(chat.getGroupName());
            }

            RecentChatsOutputData recentChatsOutputData = new RecentChatsOutputData(chatNames, nameToChatIds);
            recentChatsPresenter.prepareSuccessView(recentChatsOutputData);
        }
        catch (Exception e) {
            recentChatsPresenter.prepareFailView(e.getMessage());
        }

    }
}
