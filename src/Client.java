import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;

    Client(Socket socket, String username){
        this.socket = socket;
        try{
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        }
        catch (IOException e){
            closeEverything();
        }
    }
    public void writeMSG(){
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected() && !socket.isClosed()){
                String MSGFromClient = scanner.nextLine();
                bufferedWriter.write(MSGFromClient);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                if((MSGFromClient.equalsIgnoreCase("BYE"))){
                    closeEverything();
                    scanner.close();
                    break;
                }
            }
        } catch (IOException e) {
                closeEverything();
        }
    }
    public void listenMSG(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()){
                    String readMSGFromGP;
                    try {
                        readMSGFromGP = bufferedReader.readLine();
                        if(readMSGFromGP == null){
                            closeEverything();
                            break;
                        }
                        System.out.println(readMSGFromGP);
                    } catch (IOException e) {
                        closeEverything();
                        break;
                    }
                }
            }
        }).start();
    }
    public void closeEverything(){
        try {
            if(socket != null) socket.close();
            if(bufferedWriter != null) bufferedWriter.close();
            if(bufferedReader != null) bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        client.listenMSG();
        client.writeMSG();
    }
}
