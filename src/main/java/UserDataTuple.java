public class UserDataTuple {
    private final String login;
    private final String password;
    private String nickname;
    public UserDataTuple (String login, String password, String nickname) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
}
