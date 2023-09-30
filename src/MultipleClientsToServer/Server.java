package MultipleClientsToServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static Scanner scanner = new Scanner(System.in);
    private static int maxClients = 5;
    private static List < ClientHandler > clients = new ArrayList<>();
    private static int clientCounter = 0;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(maxClients);

        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Listening to port: 8080");

            // Start a thread to read and broadcast server input
            Thread inputThread = new Thread(Server::readAndBroadcastInput);
            inputThread.start();

            boolean show = false;
            while (true) {
                if (clients.size() >= maxClients) {
                    // Handle excess client connections gracefully
                    if(show) System.out.println("Max client limit(" + maxClients + ") reached. Rejecting new connection.");
                    if(!show) show = true;
                    Socket clientSocket = serverSocket.accept();
                    sendRejectionMessage(clientSocket);
                    clientSocket.close();
                } else {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("client-" + ++clientCounter + " connected");
                    show = false;

                    // Create a new thread to handle the client
                    ClientHandler clientHandlerSystem = new ClientHandler(clientSocket, clientCounter);
                    clients.add(clientHandlerSystem);
                    executor.submit(clientHandlerSystem);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Broadcast a message to all connected clients
    public static void broadcastMessage(String message) {
        for (Iterator<ClientHandler> iterator = clients.iterator(); iterator.hasNext();) {
            ClientHandler client = iterator.next();
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                // Handle the "connection reset by peer" error
                System.out.println("Error broadcasting message to client-" + client.getClientNumber() + ": " + e.getMessage());
                // Remove the disconnected client from the list
                iterator.remove();
                System.out.println("client-" + client.getClientNumber() + " is disconnected.");
            }
        }
    }

    // Read input from the server console and broadcast it to clients
    public static void readAndBroadcastInput() {
        while (true) {
            String input = scanner.nextLine();
            broadcastMessage("\nServer: " + input);
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client-" + client.getClientNumber() + " disconnected.");
    }

    private static void sendRejectionMessage(Socket clientSocket) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeUTF("Server: Max client limit reached. Please try again later.");
            dataOutputStream.flush();
        } catch (IOException e) {
            System.out.println("Error sending rejection message: " + e.getMessage());
        }
    }
}