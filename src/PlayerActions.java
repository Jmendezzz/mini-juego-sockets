import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class PlayerActions {

    public static ArrayList<PlayerActions> playerList = new ArrayList<>();
    public static String history;
    private Socket socket;
    private String username;
    private DataInputStream flujoEntrada;
    private DataOutputStream flujoSalida;

    public PlayerActions(Socket socket, String username) {
        this.socket = socket;
        this.username = username;

        try {
            this.flujoEntrada = new DataInputStream(socket.getInputStream());
            this.flujoSalida = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        playerList.add(this);
        shareMessages(username + " se ha unido a la partida. " + playerList.size() + "/5");

        if (playerList.size() == 5) {
            miniGame();

        }

    }

    public void miniGame() {
        shareMessages("Empezando la partida....");
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        shareMessages("Historia a crear:" + selectTheme());

        while (position <5) {
            selectPlayer(); // Selecciona jugador se activa booleano
            endTurn(); // Espera 15 segundos para desactivar turno.
        }
        shareMessages("Historia final:" + history);


    }

    public void shareMessages(String message) {

        for (PlayerActions playerActions : playerList) {
            try {

                playerActions.flujoSalida.writeUTF(message);
                playerActions.flujoSalida.flush();


            } catch (IOException e) {
                close(); // Cuando se pierda la conexión se cerrara toda la comunicación con este socket.
                throw new RuntimeException(e);

            }
        }

    }

    public String selectTheme() {

        List<String> themes = Arrays.asList("Historia de vacaciones",
                "Historia de terror",
                "Historia graciosa ",
                "Historia de universidad"

        );
        Random random = new Random();
        int position = random.nextInt((playerList.size() - 1) - 0 + 1) + 0;
        return themes.get(0);


    }

    private int position = 0;

    public void selectPlayer() {
        shareMessages("Es el turno del jugador " + playerList.get(position).username);
        try {
            playerList.get(position).flujoSalida.writeUTF("B:True");
        } catch (IOException e) {
            close();
            throw new RuntimeException(e);

        }

    }

    public void endTurn() {
        System.out.println("tratando de terminar turno");

        try {
            Thread.sleep(15000);
            playerList.get(position).flujoSalida.writeUTF("B:False");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("15 segundos han pasado!");

        position++;
    }


    /**
     * Cada cliente tiene un unico socket que sera como su identificador.
     * El servidor lo que hará es almacenar todos estos sockets en la lista.
     * Entonces por cada instancia de esta clase el servidor ejecutara el siguiente hilo:
     * Como bien se sabe el socket es un puente de comunicacion entonces, cuando desde la clase Player se escriba algo
     * Este hilo que se está ejecutando lo va recibir porque esta a la escucha en ese puente, una ves lo reciba su tarea
     * va a ser compartir este mensaje con los demas puentes de comunicacion, entonces ese es el por que de este hilo
     */
    public void waitingForMessages() {
        try {
            while (!socket.isClosed()) {

                String message = flujoEntrada.readUTF();
                shareMessages(message);


            }
        } catch (IOException e) {
            System.out.println("Player actions" + e.getMessage());
            close(); // Cuando se pierda la conexión se cerrara toda la comunicación con este socket.
        }

    }

    public void close() {
        try {
            socket.close();
            flujoSalida.close();
            flujoEntrada.close();
            playerList.remove(this);

        } catch (IOException e) {
            System.out.println("****" + e.getMessage());
        }
    }

}
