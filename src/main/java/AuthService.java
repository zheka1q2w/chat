public interface AuthService {
    boolean authenticate(String login, String password);
    void registration(String login, String password, String nickname);

    String getNickname(String nickname);
    void setNickname (String login, String nickname);

}
