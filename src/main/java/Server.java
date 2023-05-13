import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Server {
    private final List<ClientHandler> clients;
    private final SQLAuthService authService;
    public SQLAuthService getAuthService() {
        return authService;
    }
    public Server () {
        clients = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(8189); SQLInterfaceImpl SQLConnector = new SQLInterfaceImpl()) {
            this.authService = new SQLAuthService(SQLConnector);
            while (true){
                System.out.println("Ожидается подключение клиента");
                Socket client = serverSocket.accept();
                    System.out.println("Клиент подключился с ip: " + client.getInetAddress());
                    (new Thread(() -> {
                        try {
                            new ClientHandler(this, client);

                        } catch (IOException | SQLException e) {
                            e.getStackTrace();
                        }
                    })).start();
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void broadCast (String nick, String msg) throws IOException {
        for (ClientHandler client:clients) {
            client.sendMessage(nick + ": " + msg +"\n");
        }
    }

    public synchronized void pointCast(String nick, String msg) throws IOException {
        for (ClientHandler client:clients) {
            if (Objects.equals(client.getNick(), nick)) {
                client.sendMessage(nick + ": " + msg + "\n");
            }
        }
    }
    public synchronized boolean nickListLookup(String nick) {
        for (ClientHandler client:clients) {
            if (Objects.equals(client.getNick(), nick)) {
                return false;
            }
        }
        return true;
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}
