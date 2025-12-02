package usecase.messaging.send_m;

public class SendMessageInputData {

    private final String chatId;
    private final String senderUserId;
    private final String repliedMessageId;
    private final String content;

    public SendMessageInputData(String chatId, String senderUserId,
                                String repliedMessageId, String content) {
        this.chatId = chatId;
        this.senderUserId = senderUserId;
        this.repliedMessageId = repliedMessageId;
        this.content = content;
    }

    public String getChatId() {
        return chatId;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public String getRepliedMessageId() { return repliedMessageId; }

    public String getContent() {
        return content;
    }
}
