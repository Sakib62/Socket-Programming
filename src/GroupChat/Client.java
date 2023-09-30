package GroupChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private String username;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public Client(Socket socket, String username){
        this.socket=socket;
        this.username= username;
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(username);
            dataOutputStream.flush();
        }catch (IOException ex){
            closeEverything(socket,dataInputStream,dataOutputStream);
        }
    }

    public void sendMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
            try{
                Scanner scanner = new Scanner(System.in);
                while (socket.isConnected()){
                    String message = scanner.nextLine();
                    dataOutputStream.writeUTF(message);
                    dataOutputStream.flush();
                }

            }catch (IOException ex){
                closeEverything(socket,dataInputStream,dataOutputStream);
            }
            }
        }).start();
    }

    public void showMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                try{
                    while(socket.isConnected()){
                        message= dataInputStream.readUTF();
                        System.out.println(message);
                    }
                }catch (IOException ex){
                    closeEverything(socket,dataInputStream, dataOutputStream);
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, DataInputStream dataInputStream,DataOutputStream dataOutputStream){
        System.out.println(username+" has been disconnected from the server !!!");
        try{
            if(socket != null){
                socket.close();
            }
            if(dataInputStream != null){
                dataInputStream.close();
            }
            if(dataOutputStream != null){
                dataOutputStream.close();
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter your username for the group chat : ");
        String username = scanner.nextLine();
        try{
            Socket socket = new Socket("localhost", 1111);
            Client client = new Client(socket,username);
            client.showMessage();
            client.sendMessage();
        }catch (IOException ex){
            System.out.println("Connection Error!");
        }
    }

}