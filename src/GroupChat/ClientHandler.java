package GroupChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlerList = new ArrayList<>();
    private Socket socket;
    private String username;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    ClientHandler(Socket socket){
        try{
            this.socket = socket;
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            username = dataInputStream.readUTF();
            clientHandlerList.add(this);
            messageToAll("SERVER : "+username+" has entered the group chat.");
        }catch(IOException ex){
            closeEverything(socket,dataInputStream,dataOutputStream);
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()){
            try {
                String sample;
                sample = dataInputStream.readUTF();
                String message = username +" : "+sample;
                messageToAll(message);
            }catch (IOException ex){
                System.out.println("Client has been disconnected !!!");
                closeEverything(socket,dataInputStream,dataOutputStream);
                break;
            }
        }
    }

    public void messageToAll(String message){
        for(ClientHandler clientHandler: clientHandlerList){
            try{
                if(!clientHandler.equals(this)){
                    clientHandler.dataOutputStream.writeUTF(message);
                    clientHandler.dataOutputStream.flush();
                }
            }catch (IOException ex){
                closeEverything(socket,dataInputStream,dataOutputStream);
            }
        }
    }

    public void closeEverything(Socket socket, DataInputStream dataInputStream,DataOutputStream dataOutputStream){
        removeClient();
        try{
            if(dataInputStream != null){
                dataInputStream.close();
            }
            if(dataOutputStream != null){
                dataOutputStream.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void removeClient(){
        clientHandlerList.remove(this);
        messageToAll("Server: "+username+" has left the chat");
    }
}