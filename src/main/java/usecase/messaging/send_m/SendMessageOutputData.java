package usecase.messaging.send_m;

public class SendMessageOutputData {

    private final String chatId;
    private final String[] message;

    public SendMessageOutputData(String chatId, String[] message) {
        this.chatId = chatId;
        this.message = message;
    }

    public String getChatId() {
        return chatId;
    }

    public String[] getMessage() {
        return message;
    }
}

