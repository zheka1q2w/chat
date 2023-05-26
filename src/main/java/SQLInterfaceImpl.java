import java.sql.*;
import java.util.Objects;

public class SQLInterfaceImpl implements SQLInterface, AutoCloseable{
    private final Statement stmt;
    private final Connection connection;

    public static void main(String[] args) {
        try (SQLInterfaceImpl sqlTest = new SQLInterfaceImpl()) {
            try {
                System.out.println(sqlTest.stmt.isClosed());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
//            String CreateTable = "CREATE TABLE Users " +
//                    "(login VARCHAR(255) not NULL, " +
//                    "password VARCHAR(255), " +
//                    "nickname VARCHAR(255), " +
//                    "PRIMARY KEY ( login )); ";
//
//            try {
//                sqlTest.stmt.execute(CreateTable);
//            } catch (SQLException e) {
//                System.err.println("Error in creating User table");
//                throw new RuntimeException(e);
//            }
            sqlTest.CreateNewUser(new UserDataTuple("123", "123"));
            sqlTest.CreateNewUser(new UserDataTuple("123", "123")); // second call
            sqlTest.CreateNewUser(new UserDataTuple("1234", "1234", "1234"));

            System.out.println(sqlTest.RetrieveUser("123"));
            System.out.println(sqlTest.RetrieveUser("3211")); // non-existing

            sqlTest.UpdateUserNick("1234", "4321");
            System.out.println(sqlTest.RetrieveUser("123"));
            sqlTest.UpdateUserNick("1234", "321"); // nick was null
            System.out.println(sqlTest.RetrieveUser("1234"));
            sqlTest.UpdateUserNick("321","4444"); // non-existing
            System.out.println(sqlTest.RetrieveUser("321"));

            sqlTest.RemoveUser("123");
            System.out.println(sqlTest.RetrieveUser("123"));
            sqlTest.RemoveUser("1234");
            System.out.println(sqlTest.RetrieveUser("1234"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @Override
    //return true if successful, login must be unique
    public boolean CreateNewUser(UserDataTuple user) throws SQLException {
        if (RetrieveUser(user.getLogin()) == null) {
            stmt.execute("INSERT INTO Users(login, password, nickname) VALUES" +
                    "('" + user.getLogin() + "', " +
                    "'" + user.getPassword() + "', " +
                    "'" + user.getNickname() + "');" );
            return true;
        } else {
            return false;
        }

    }

    @Override
    public UserDataTuple RetrieveUser(String login) throws SQLException {
        ResultSet RetrieveStmt = stmt.executeQuery("SELECT * FROM Users WHERE " +
                "login = " + login);
        if (RetrieveStmt.next()) {
            return new UserDataTuple(RetrieveStmt.getString(1),
                    RetrieveStmt.getString(2),
                    RetrieveStmt.getString(3));
        } else {
            return null;
        }
    }

    @Override
    //return true if successfully changes nick
    public boolean UpdateUserNick(String login, String nick) throws SQLException {
        return stmt.execute("UPDATE Users SET nickname = '" +
                nick + "' WHERE login = '" +
                login + "'");
    }

    @Override
    //return true if successfully removes existing user
    public boolean RemoveUser(String login) throws SQLException {
        return stmt.execute("DELETE FROM Users WHERE " +
                "login = '" + login + "'");
    }

    public SQLInterfaceImpl () throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/my_db", "local_user", "hC3>w$La");
        stmt = connection.createStatement();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
