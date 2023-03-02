import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Scanner;

public class Player {

    static Socket  socket;
    static DataInputStream flujoEntrada;
    static DataOutputStream flujoSalida;
    static ObjectInputStream objectInputStream;

    static Scanner sc = new Scanner(System.in);

    static Boolean turno = false;

    public static void main(String[] args) {

        System.out.println("Ingresa tú nombre de usuario");
        String name = sc.nextLine();

        try{
            socket = new Socket("localhost",5050);
            flujoSalida = new DataOutputStream(socket.getOutputStream());
            flujoEntrada = new DataInputStream(socket.getInputStream());
            flujoSalida.writeUTF(name);
            Thread threadMesages = new Thread(Player::waitingMessages);
            threadMesages.start();


            while (socket.isConnected()){
                if(turno){
                    System.out.print("Es tu turno!");
                    String tuMensaje = sc.nextLine();
                    PlayerActions.test();
                    flujoSalida.writeUTF(tuMensaje);
                }

            }

        }catch (IOException e){
            System.out.println("Jugador: "+e.getMessage());

        }


    }
    public static void waitingMessages(){
        try{
        while (socket.isConnected()){

                String message = flujoEntrada.readUTF();
                if(message.split(":")[0].equals("B") && message.split(":")[1].equals("True")){
                    System.out.println("Es tu turno");
                    turno=true;
                }else if(message.split(":")[0].equals("B") && message.split(":")[1].equals("False")){
                    System.out.println("Se acabo tu tiempo!");
                    turno=false;
                }
                else{
                    System.out.println(message);
                }
        }
        }catch (IOException err){

            System.out.println("Jugador: "+ err.getMessage());

            close();

        }
    }

    public static void close(){
        try{
            socket.close();
            flujoSalida.close();
            flujoEntrada.close();



        }catch (IOException e){
            System.out.println("Jugador: "+ e.getMessage());
        }
    }
}
