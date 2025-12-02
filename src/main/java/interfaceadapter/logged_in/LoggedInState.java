package interfaceadapter.logged_in;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The State information representing the logged-in user.
 */
public class LoggedInState {
    private boolean loggedIn = false;

    private String username = "";
    private String userId = "";
    private String usernameError = null;

    private String password = "";
    private String passwordError;

    private List<String> chatNames = new ArrayList<>();
    private HashMap<String, String> nameToChatIds = new HashMap<>();
    private String recentChatsError = null;

    public LoggedInState(LoggedInState copy) {
        username = copy.username;
        userId = copy.userId;
        password = copy.password;
        passwordError = copy.passwordError;
    }

    // Because of the previous copy constructor, the default constructor must be explicit.
    public LoggedInState() {

    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getUsernameError() {
        return usernameError;
    }

    public void setUsernameError(String usernameError) {
        this.usernameError = usernameError;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }

    public String getPasswordError() {
        return passwordError;
    }

    public void setChatNames(List<String> chatNames) {
        this.chatNames = chatNames;
    }

    public List<String> getChatNames() {return chatNames;}

    public void clearChatNames() {chatNames.clear();}

    public String getNameToChatIds(String chatName) {
        return nameToChatIds.get(chatName);
    }

    public void setNameToChatIds(HashMap<String, String> nameToChatIds) {this.nameToChatIds = nameToChatIds;}

    public void clearNameToChatIds() {nameToChatIds.clear();}

    public void setRecentChatsError(String recentChatsError) {
        this.recentChatsError = recentChatsError;
    }

    public String getRecentChatsError() {
        return recentChatsError;
    }
}