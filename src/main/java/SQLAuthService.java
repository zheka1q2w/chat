import java.sql.SQLException;
import java.util.Objects;

public class SQLAuthService implements AuthService{
    private final SQLInterface sqlInterface;
    @Override
    public boolean authenticate(String login, String password) throws SQLException {
        UserDataTuple user = sqlInterface.RetrieveUser(login);
        if (user.getLogin() == null) { //todo To test: if there no user account, then (user == null)
            return false;
        } else return Objects.equals(user.getPassword(), password);
    }

    @Override
    public boolean PartialRegistration(String login, String password) throws SQLException {
        UserDataTuple user = new UserDataTuple(login, password);
        return sqlInterface.CreateNewUser(user);
    }

    @Override
    public boolean FullRegistration(String login, String password, String nickname) throws SQLException {
        UserDataTuple user = new UserDataTuple(login, password, nickname);
        return sqlInterface.CreateNewUser(user);
    }


    @Override
    public String getNickname(String login) throws SQLException {
        return sqlInterface.RetrieveUser(login).getNickname();
    }

    @Override
    public boolean setNickname(String login, String nickname) throws SQLException {
        return sqlInterface.UpdateUserNick(login, nickname);
    }
    public SQLAuthService (SQLInterface sqlInterface) {
        this.sqlInterface = sqlInterface;
    }
}
