import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    //Thread-safe array to hold active clients
    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Listening to port 1234...");

            // This while loop for server to keep running
            while(true){
                //Add each client to list
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler (socket);
                clients.add(clientHandler);

                //Thread for each client
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Client connection error" + e.getMessage());
        }
    }
    public static void broadcastMSG(String message, ClientHandler sender){
        for(ClientHandler client : clients){
            if(client != sender){
                client.sendMSG(message);
            }
        }
    }
    public static void removeClient(ClientHandler client){
        clients.remove(client);
    }

}
// This server serve only 2 main purpose
// 1. Accepting all the request from client
// 2. Give client with each thread to work
