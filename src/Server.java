import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {



    public static void main(String[] args) {
        ServerSocket server;


        try{
            server = new ServerSocket(5050,5);

            while (!server.isClosed()){

                Socket socket =  server.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                String playerName = dataInputStream.readUTF();
                PlayerActions playerActions = new PlayerActions(socket,playerName);
                Thread thread = new Thread(playerActions::waitingForMessages);
                thread.start();
                dataOutputStream.writeUTF("Bienvenid@ a 'Completa la historia' jugadores conectados:" +PlayerActions.playerList.size() + "/5");
                dataOutputStream.flush();
                System.out.println("SERVER:" + playerName + " se ha conectado.");

            }

        }catch (IOException e){
            System.out.println("Server***"+e.getMessage());
        }



    }
}
