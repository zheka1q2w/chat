import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthServiceImpl implements AuthService {
    private final List<UserDataTuple> userData = new ArrayList<>(List.of(
            new UserDataTuple("zheka","123","zheka"),
            new UserDataTuple("smartphone","123","smartphone"),
            new UserDataTuple("user3","pass3","nick3")
    ));
    @Override
    public boolean authenticate(String login, String password) {
        if (login.isEmpty()) {
            return false;
        }
        if (password.isEmpty()) {
            return false;
        }

        for (UserDataTuple userData: userData) {
            if (login.equals(userData.getLogin())) {
                if (password.equals(userData.getPassword())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void registration(String login, String password, String nickname) {
        userData.add(new UserDataTuple(login,password,nickname));
    }
    public void setNickname(String login ,String nickname) {
        for (UserDataTuple user:userData) {
            if (user.getLogin().equals(login)) {
                user.setNickname(nickname);
                break;
            }
        }
    }

    @Override
    public String getNickname(String login) {
        for (UserDataTuple userData: userData) {
            if (login.equals(userData.getLogin())) {
                return userData.getNickname();
            }
        }
        return null;
    }
}
