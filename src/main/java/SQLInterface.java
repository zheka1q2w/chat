import java.sql.SQLException;

public interface SQLInterface {
    boolean CreateNewUser (UserDataTuple user) throws SQLException; //return true if successful, login must be unique
    UserDataTuple RetrieveUser (String login) throws SQLException;
    boolean UpdateUserNick (String login, String nick) throws SQLException; //return true if successfully changes nick
    boolean RemoveUser (String login) throws SQLException; //return true if successfully removes existing user


}
