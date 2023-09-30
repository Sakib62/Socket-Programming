package MultipleClientsToServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private int clientNumber;

    public ClientHandler(Socket clientSocket, int clientNumber) {
        this.clientSocket = clientSocket;
        this.clientNumber = clientNumber;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public void sendMessage(String message) throws IOException {
        if (dataOutputStream != null) {
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
        }
    }

    public void run() {
        try {
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            String message;
            while (true) {
                message = dataInputStream.readUTF();
                System.out.println("Client-" + clientNumber + " says: " + message);

                if (message.equalsIgnoreCase("_stop"))
                    break;
            }
            Server.removeClient(this);
            clientSocket.close();
        } catch (Exception e) {
            //System.out.println(e);
            Server.removeClient(this);
        }
    }
}
