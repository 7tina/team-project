package interface_adapter.messaging.send_m;

import interface_adapter.ViewModel;

/**
 * ViewModel for the ChatView.
 */
public class ChatViewModel extends ViewModel<ChatState> {

    public ChatViewModel() {
        super("chat");
        this.setState(new ChatState());
    }

    public ChatState getChatState() {
        ChatState state = this.getState();
        if (state == null) {
            state = new ChatState();
            this.setState(state);
        }
        return state;
    }
}
