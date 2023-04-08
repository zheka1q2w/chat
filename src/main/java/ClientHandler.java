import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final Socket socket;
    private final DataOutputStream out;
    private final DataInputStream in;
    private String name;
    private final Server server;

    public String getName() {
        return name;
    }

    public ClientHandler(Server server, Socket client) {
        try {
            this.socket = client;
            this.in = new DataInputStream(client.getInputStream());
            this.out = new DataOutputStream(client.getOutputStream());
            this.server = server;

            new Thread(() -> {
                try {
                    if(authentication()) {
                        readMessage();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readMessage() throws IOException {
        while (true) {

            String msg = in.readUTF();
            if (msg.equals("/end")) {
                return;
            }
            if (msg.startsWith("@")) {
                privateMassage(msg);
            }
            else server.broadCast(name + ": " + msg + "\n");
        }
    }

    private void closeConnection() {
        server.unSubscribe(this);
        if (!name.isEmpty()) {
            server.broadCast(name + " Вышел из чата\n");
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMassage(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean authentication () throws IOException {
        AuthService authService = server.getAuthService();
        out.writeUTF("Авторизируйтесь, что бы продолжить. \n Для авторизации ввидите '/auth login password'");
        out.writeUTF("Введите '/reg login password' чтобы зарегестрироваться или '/end' чтобы выйти.");
        while (true) {
            out.writeUTF("Ожидание команды.");
            String s = in.readUTF();
            if(s.startsWith("/auth")) {
                if (s.equals("/auth")) {
                    out.writeUTF("Что бы авторизироваться, введите после '/auth' свой логин и пороль через пробел");
                    continue;
                }
                String[] parts = s.split(" ");
                String login = parts[1];
                String password = parts[2];
                if (authService.authenticate(login,password)) {
                    while (true) {
                        if (server.isNickBusy(authService.getNickname(login))) {
                            out.writeUTF("Ник занят, выберите другой\n");
                            String nickname = in.readUTF();
                            authService.setNickname(login, nickname);
                            continue;
                        }
                        break;
                    }
                    this.name = authService.getNickname(login);
                    server.subscribe(this);
                    out.writeUTF("Вы авторизированны как "+name+", введите /continue чтобы войти или /nick nickname чтобы сменить ник");
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/continue")) {
                            break;
                        }
                        if (str.startsWith("/nick")) {
                            String nStr = str.split(" ")[1];
                            if (server.isNickBusy(nStr)) {
                                    out.writeUTF("Ник занят\n");
                                    continue;
                            }
                            name = nStr;
                            out.writeUTF("Ваш новый ник: " + name);
                        }
                    }
                    return true;
                }
                out.writeUTF("Неправильный логин или пороль.\n");

            }
            if(s.equals("/end")) {
                return false;
            }
            if (s.startsWith("/reg")) {
                if (s.equals("/reg")) {
                    out.writeUTF("Что бы авторизироваться, введите после '/reg' свой логин и пороль через пробел");
                    continue;
                }
                String[] parts = s.split(" ");
                String login = parts[1];
                String password = parts[2];
                if(authService.authenticate(login,password)) {
                    out.writeUTF("Такой аккаунт уже существует. Введите другие данные или войдите.");
                    continue;
                }
                out.writeUTF("Введите ник для общения");
                while (true) {
                    String nickname = in.readUTF();
                    if (server.isNickBusy(nickname)) {
                        out.writeUTF("Такой ник существует, введите другой или введите /end чтобы вернуться");
                        continue;
                    }
                    else if (nickname.equals("/end")){
                        break;
                    }
                    authService.registration(login,password,nickname);
                    this.name = nickname;
                    out.writeUTF("Вы успешно зарегестрированы, теперь вы можете войти в свой аккаунт");
                    break;
                }
                continue;
            }
            out.writeUTF("Неизвестное выражение\n");
        }
    }
    public void privateMassage(String msg) throws IOException {
        String nick = msg.substring(1, msg.indexOf(" "));
        String massage = msg.substring(msg.indexOf(" "));
        if (server.isNickBusy(nick)) {
            server.sendMassage(nick, name + ":" + massage);
        }
        else {
            out.writeUTF("Ник не найден!");
        }
    }
}
