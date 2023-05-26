import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost",8189)){
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
            Scanner scanner = new Scanner(System.in);
            Thread tInput = new Thread(new Runnable() {
                @Override
                public void run() {
                        try (FileOutputStream outFile = new FileOutputStream("Message_log.txt",true)){
                            while (true) {
                                String str = in.readUTF();
                                System.out.println(str);
                                outFile.write((dtf.format(LocalDateTime.now()) + " " + str + "\n").getBytes());
                            }
                        } catch (FileNotFoundException nf) {
                            System.out.println("Произошла ошибка создания или доступа 'Massage_log.txt'");
                        } catch (IOException e) {
                            System.out.println("Сокет закрыт со стороны сервера");
                            throw new RuntimeException(e);
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
    private static synchronized void logToFile (String msg, File file) {

        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(msg.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
