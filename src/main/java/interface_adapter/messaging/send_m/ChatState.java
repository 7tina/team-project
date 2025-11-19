package interface_adapter.messaging.send_m;

import use_case.messaging.ChatMessageDto;

import java.util.ArrayList;
import java.util.List;

/**
 * The state object stored inside ChatViewModel.
 */
public class ChatState {

    private final List<ChatMessageDto> messages = new ArrayList<>();
    private String error;

    public List<ChatMessageDto> getMessages() {
        return new ArrayList<>(messages);
    }

    public void addMessage(ChatMessageDto message) {
        messages.add(message);
    }

    public void clearMessages() {
        messages.clear();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
