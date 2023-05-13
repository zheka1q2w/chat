// UserDataTuple represent user's information {login password nickname}
// UserDataTuple may include nickname, but it is optional. In that case null will be returned after .getNickname
public class UserDataTuple {
    private final String login;
    private final String password;
    private final String nickname;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
    public UserDataTuple (String login, String password, String nickname) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
    }

    public UserDataTuple (String login, String password) {
        this.login = login;
        this.password = password;
        this.nickname = null;
    }
    @Override
    public String toString() {
        return "(" + login + " " + password + " " + nickname + ")";
    }
}
