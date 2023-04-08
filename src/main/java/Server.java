import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public AuthService getAuthService() {
        return authService;
    }

    private List<ClientHandler> clients;
    private final int PORT = 8189;
    private AuthService authService;
    public Server() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            authService = new InMemoryAuthServiceImpl();
            clients = new ArrayList<>();
            while(true) {
                System.out.println("Сервер ожидает подключения");
                Socket client = serverSocket.accept();
                System.out.println("Клиент подключился: " + client.getInetAddress());
                new ClientHandler(this, client);
            }
        } catch (IOException e) {
            System.out.println("Ошибка");
            e.printStackTrace();
        }
    }

    public synchronized void broadCast(String msg) {
        for (ClientHandler client : clients) {
            client.sendMassage(msg);
        }
        System.out.println(msg);
    }
    public synchronized void sendMassage (String nick, String msg) {
        for (ClientHandler client : clients) {
            if (nick.equals(client.getName())){
                client.sendMassage(msg);
            }
        }
        System.out.println(msg);
    }
    public synchronized void subscribe(ClientHandler client) {
        clients.add(client);
    }
    public synchronized void unSubscribe (ClientHandler client) {
        clients.remove(client);
    }
    public  synchronized  boolean isNickBusy (String nickname) {
        for (ClientHandler client: clients) {
            if (nickname.equals(client.getName())) {
                return true;
            }
        }
        return false;
    }
}
