package MultipleClientsToServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8080);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // Create a thread to receive and print messages from the server
            new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            String serverMessage = dataInputStream.readUTF();
                            System.out.println(serverMessage);
                        }
                    } catch (Exception e) {
                        System.out.println("Connection to server closed.");
                    }
                }
            }).start();

            while (true) {
                System.out.print("input> ");
                String message = scanner.nextLine();
                dataOutputStream.writeUTF(message);
                if (message.equalsIgnoreCase("_stop"))
                    break;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}