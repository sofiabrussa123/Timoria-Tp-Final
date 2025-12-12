package Red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import interfaces.GameController;

public class HiloCliente extends Thread {

    private DatagramSocket socket;
    private int puertoServidor = 5555;
    private String puertoServidorStr = "255.255.255.255";
    private InetAddress ipServidor;
    private boolean end = false;
    private GameController gameController;

    public HiloCliente(GameController gameController) {
    	this.gameController = gameController;
        try {
        	ipServidor = InetAddress.getByName(puertoServidorStr);
            socket = new DatagramSocket();
        } catch (SocketException | UnknownHostException e) {
//            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        do {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try {
                socket.receive(packet);
                procesarMensaje(packet);
            } catch (IOException e) {
//                throw new RuntimeException(e);
            }
        } while(!end);
    }

    private void procesarMensaje(DatagramPacket packet) {
        String message = (new String(packet.getData())).trim();
        String[] parts = message.split(":");

        System.out.println("Mensaje recibido: " + message);

        switch(parts[0]){
            case "YaConectado":
                System.out.println("Ya estas conectado");
                break;
            case "Conectado":
                System.out.println("Conectado al servidor");
                this.ipServidor = packet.getAddress();
                gameController.conectar(Integer.parseInt(parts[1]));
                break;
            case "Lleno":
                System.out.println("Servidor lleno");
                this.end = true;
                break;
            case "Empezar":
                this.gameController.empezarJuego();
                break;
            case "TerminarJuego":
                this.gameController.terminarJuego();
                break;
            case "Desconectar":
                this.gameController.desconectar();
                break;
            default: 
            	this.gameController.procesarAccionesEntidades(parts);
            	break;
        }

    }

    public void enviarMensaje(String message) {
        byte[] byteMessage = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, ipServidor, puertoServidor);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void terminar() {
        this.end = true;
        socket.close();
        this.interrupt();
    }
}
