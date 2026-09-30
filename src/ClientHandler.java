import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String ClientName;

    ClientHandler(Socket socket){
        this.socket = socket;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e){
            closeEverything();
        }
    }
    @Override
    public void run(){
        try {
            this.ClientName = bufferedReader.readLine();

            Server.broadcastMSG("SERVER: " + ClientName + " has join the group chat", this);
            //Key break for client
            String MSGfromClient;
            while((MSGfromClient = bufferedReader.readLine()) != null){
                if(MSGfromClient.equalsIgnoreCase("BYE")){
                    Server.broadcastMSG(ClientName + " has left the chat", this);
                    break;
                }
                Server.broadcastMSG(ClientName + ": " + MSGfromClient, this);
            }
        }
        catch (IOException e) {
            closeEverything();
        }
    }

    public void sendMSG(String message){
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            closeEverything();
        }
    }
    public void closeEverything(){
            Server.removeClient(this);
            //If client still active receive unactive's client message
//            if(ClientName != null){
//                Server.broadcastMSG(ClientName + "has left the chat", this);
//            }
            try {
                if (socket != null && !socket.isClosed()) socket.close();
                if(bufferedWriter != null) bufferedWriter.close();
                if (bufferedReader != null) bufferedReader.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
