import java.sql.SQLException;

public interface AuthService {
    boolean authenticate (String login, String password) throws SQLException;
    // Check if user with whose login and password exist in DB
    //return true if exist

    boolean PartialRegistration (String login, String password) throws SQLException;
    // Add user with (login password) to DB
    //return true on success
    boolean FullRegistration (String login, String password, String nickname) throws SQLException;
    // Add user with (login password nickname) to DB
    //return true on success

    String getNickname (String login) throws SQLException;
    // return nickname of the user with this login
    // return null if such login don't exist

    boolean setNickname (String login, String newNick) throws SQLException; //return true on success
    // change nickname for existing user
    // return true if successfully changes nickname
}
