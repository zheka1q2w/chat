import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost",8189)){
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            Scanner scanner = new Scanner(System.in);
            Thread tInput = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String str = in.readUTF();
                            System.out.println(str);
                        } catch (IOException e) {
                            System.out.println("Сокет закрыт ос стороны сервера");
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            tInput.setDaemon(true);
            tInput.start();
            while (true){
                String s = scanner.nextLine();
                out.writeUTF(s);
                if (s.equals("/end")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
