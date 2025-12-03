package usecase.messaging.sendmessage;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import entity.Chat;
import entity.Message;
import entity.User;
import entity.ports.ChatRepository;
import entity.ports.MessageRepository;
import entity.ports.UserRepository;

/**
 * Interactor for the send message use case.
 *
 * <p>
 * It validates the chat and sender, creates a new {@link Message},
 * delegates persistence to the data access interface, and then notifies
 * the presenter with the result.
 */
public class SendMessageInteractor implements SendMessageInputBoundary {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final SendMessageOutputBoundary presenter;
    private final SendMessageDataAccessInterface dataAccess;

    /**
     * Constructs a {@code SendMessageInteractor}.
     *
     * @param chatRepository    repository for accessing chats
     * @param messageRepository repository for accessing messages
     * @param userRepository    repository for accessing users
     * @param presenter         output boundary to present the result
     * @param dataAccess        data access interface that persists messages
     *                          and updates chat metadata
     */
    public SendMessageInteractor(ChatRepository chatRepository,
                                 MessageRepository messageRepository,
                                 UserRepository userRepository,
                                 SendMessageOutputBoundary presenter,
                                 SendMessageDataAccessInterface dataAccess) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.presenter = presenter;
        this.dataAccess = dataAccess;
    }

    /**
     * Executes the send message use case.
     *
     * <p>
     * Steps:
     * <ol>
     *     <li>Validate that the chat exists.</li>
     *     <li>Validate that the sender user exists.</li>
     *     <li>Create a new {@link Message} entity.</li>
     *     <li>Persist the message and update the chat via {@link SendMessageDataAccessInterface}.</li>
     *     <li>Build {@link SendMessageOutputData} and call the presenter.</li>
     * </ol>
     *
     * @param inputData input data for the use case, including chat ID,
     *                  sender ID, replied message ID and content
     */
    @Override
    public void execute(SendMessageInputData inputData) {
        final String chatId = inputData.getChatId();
        final String senderId = inputData.getSenderUserId();
        final String repliedMessageId = inputData.getRepliedMessageId();
        final String content = inputData.getContent();

        final Optional<Chat> chatOpt = chatRepository.findById(chatId);

        if (chatOpt.isEmpty()) {
            presenter.prepareFailView("Chat not found: " + chatId);
        }
        else {
            final Chat chat = chatOpt.get();

            final Optional<User> senderOpt = userRepository.findByUsername(senderId);

            if (senderOpt.isEmpty()) {
                presenter.prepareFailView("Sender not found: " + senderId);
            }
            else {
                final Message message = new Message(
                        UUID.randomUUID().toString(),
                        chatId,
                        senderId,
                        repliedMessageId,
                        content,
                        Instant.now()
                );

                chat.setLastMessage(Instant.now());
                final Message saved = dataAccess.sendMessage(message);
                dataAccess.updateChat(chatId, message.getId(), chat.getLastMessage());

                // Array index order: [messageId, senderDisplayName, messageContent, messageTimestamp, repliedId]
                final String senderName = senderOpt.get().getName();
                final String[] msg = {
                        saved.getId(), senderName,
                        saved.getContent(),
                        makeString(saved.getTimestamp()),
                        saved.getRepliedMessageId(),
                };

                final SendMessageOutputData outputData = new SendMessageOutputData(chatId, msg);
                presenter.prepareSuccessView(outputData);
            }
        }
    }

    /**
     * Formats the message timestamp into a human-readable string.
     *
     * @param timestamp the timestamp to format
     * @return a formatted timestamp string in the pattern {@code dd-MM-yyyy HH:mm:ss},
     *         using UTC time zone
     */
    private String makeString(Instant timestamp) {
        // Specify the desired time zone
        final ZoneId zone = ZoneId.of("UTC");
        final ZonedDateTime zdt = timestamp.atZone(zone);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return zdt.format(formatter);
    }
}
