import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Objects;

public class ClientHandler {
    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;

    public String getNick() {
        return nick;
    }

    public String nick;
    private String login;
    private final Server server;
    public ClientHandler(Server server, Socket client) throws IOException, SQLException {
                this.socket = client;
                this.in = new DataInputStream(client.getInputStream());
                this.out = new DataOutputStream(client.getOutputStream());
                this.server = server;
                try {
                    clientStarter();
                } finally {
                    closeConnection();
                }
    }

    private void clientStarter() throws IOException, SQLException {
        // these chain methods are used to automatically process user to chat
        // This method is used to allow user to get registration and authorization
        while (true) {
            out.writeUTF("Здраствйте! Вам необходимо авторизироваться, что бы продолжить. \n" +
                    "Введите /help чтобы увидеть доступные команды." );
            String[] parts = in.readUTF().split(" ");
            if (Objects.equals(parts[0], "/help")) {
                starterHelp(parts);
            } else if (Objects.equals(parts[0], "/authentication")) {
                if(authentication(parts)) {
                    break;
                }
            } else if (Objects.equals(parts[0], "/registration")) {
                registration(parts);
            } else if (Objects.equals(parts[0], "/exit")) {
                closeConnection();
            } else {
                out.writeUTF("Команда не распознана");
            }
        }
        // next phase

        clientMenu();
    }

    private boolean authentication(String[] command) throws SQLException, IOException {
        try {
            if (server.getAuthService().authenticate(command[1], command[2])) {
                out.writeUTF("Авторизация прошла успешно");
                this.login = command[1];
                this.nick = server.getAuthService().getNickname(login);
                return true;
            } else {
                out.writeUTF("Данная учетная записать не найдена.");
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            out.writeUTF("Введите логин и пароль для авторизации");
            return false;
        }
    }
    private void registration(String[] command) throws SQLException, IOException {
        if (command.length == 4) {
            if (server.getAuthService().FullRegistration(command[1], command[2], command[3])) {
                out.writeUTF("Регистрация прошла успешно.");
            } else {
                out.writeUTF("Регистрация провалилась, аккаунт с таким логином уже существует \n" +
                        "или произошла ошибка во время попытки регистрации");

            }

        } else {
            if (server.getAuthService().PartialRegistration(command[1], command[2])) {
                out.writeUTF("Регистрация прошла успешно.");
            } else {
                out.writeUTF("Регистрация провалилась, аккаунт с таким логином уже существует \n" +
                        "или произошла ошибка во время попытки регистрации");
            }
        }
    }

    private void starterHelp(String[] command) throws IOException {
        out.writeUTF("help"); //todo /help
    }

    private void clientMenu() throws IOException, SQLException {
        // This method is used to allow user change nick or some other preparation before starting chatting
        while (true) {
            out.writeUTF("Что бы войти в чат, введите /enter \n" +
                    "Вы также можете ввести /help что узнать другие доступные команды");
            String[] parts = in.readUTF().split(" ");
            if (Objects.equals(parts[0], "/help")) {
                menuHelp(parts);
            } else if (Objects.equals(parts[0], "/enter")) {
                if (checker()) { //check if there are problems with nick
                    break;
                }
            } else if (Objects.equals(parts[0], "/myNick")) {
                myNick();
            } else if (Objects.equals(parts[0], "/setNick")) {
                setNick(parts);
            } else if (Objects.equals(parts[0], "/exit")) {
                closeConnection();
            } else {
                out.writeUTF("Команда не распознана");
            }
        }
        // last phase
        readCommand();
    }

    private void menuHelp(String[] command) throws IOException {
        out.writeUTF("new help"); //todo /menuHelp
    }

    private boolean checker() throws IOException {
        if ((nick != null) & (!Objects.equals(nick, ""))) {
            if (server.nickListLookup(nick)) {
                return true;
            } else {
                out.writeUTF("Ник '" + nick + "' в данный момент занят, выберите другой");
                return false;
            }
        } else {
            out.writeUTF("Вы еще не выбрали ник! используйте команду /setNick что создать ник");
            return false;
        }
    }

    private void setNick(String[] command) throws SQLException, IOException {
        if(server.getAuthService().setNickname(login, command[1])) {
            this.nick = server.getAuthService().getNickname(login);
            out.writeUTF("Ник успешно изменен");
        } else {
            out.writeUTF("произошла непредвиденная ошибка при изменеии ника");
        }
    }

    private void myNick() throws SQLException, IOException {
        this.nick = server.getAuthService().getNickname(login);
        if ((nick != null) & (!Objects.equals(nick, ""))) {
            out.writeUTF("Ваш ник: " + nick);
        } else {
            out.writeUTF("Ник пока еще не существует, введите команду /setNick чтобы создать его");
        }
    }

    public void readCommand() throws IOException {
        // Add user to chat
        // after check if there should be any special command. If not, broadcast message
        /*
        Possible special command are:
        "/whisper name msg" - send message to client with nick "name" massage "msg"

        */
        try {
            server.subscribe(this);
            while (true) {
                String msg = in.readUTF();
                if (msg.contains("/whisper ")) {
                    String[] parts = msg.split(" ", 3);
                    server.pointCast(parts[1], parts[2]);
                }
                server.broadCast(nick, msg);
            }
        } finally {
            server.unsubscribe(this);
        }
    }

    public void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
    }

    private void closeConnection() throws IOException {
        socket.close();
    }
}
