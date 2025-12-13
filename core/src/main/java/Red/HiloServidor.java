package Red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import interfaces.GameController;

public class HiloServidor extends Thread {

    private DatagramSocket socket;
    private int puertoServidor = 5555;
    private boolean fin = false;
    private final int MAX_CLIENTES = 2;
    private int clientesConectados = 0;
    private ArrayList<Cliente> clientes = new ArrayList<Cliente>();
    private GameController gameController;

    public HiloServidor(GameController gameController) {
    	this.gameController = gameController;
        try {
            socket = new DatagramSocket(puertoServidor);
        } catch (SocketException e) {
//            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        do {
            DatagramPacket paquete = new DatagramPacket(new byte[1024], 1024);
            try {
                socket.receive(paquete);
                procesarMensaje(paquete);
            } catch (IOException e) {
//                throw new RuntimeException(e);
            }
        } while(!fin);
    }

    private void procesarMensaje(DatagramPacket paquete) {
        String mensaje = (new String(paquete.getData())).trim();
        String[] partes = mensaje.split(":"); //Separar el mensaje por el símbolo :
        int indice = encontrarIndiceCliente(paquete);
        System.out.println("Mensaje recibido " + mensaje);

        if(partes[0].equals("Conectar")){

        	//Si el cliente ya está conectado lo rebota
            if(indice != -1) {
                System.out.println("Cliente ya conectado");
                this.enviarMensaje("YaConectado", paquete.getAddress(), paquete.getPort());
                return;
            }

            //Si es un cliente nuevo y no se llegó al máximo, guarda un cliente con los datos correpondientes
            if(clientesConectados < MAX_CLIENTES) {
            	clientesConectados++;
                Cliente nuevoCliente = new Cliente(clientesConectados, paquete.getAddress(), paquete.getPort());
                clientes.add(nuevoCliente);
                enviarMensaje("Conectado:"+clientesConectados, paquete.getAddress(), paquete.getPort());

                //Si ya se llegó al máximo se arranca el juego
                if(clientesConectados == MAX_CLIENTES) {
                    for(Cliente cliente : clientes) {
                        enviarMensaje("Empezar", cliente.getIp(), cliente.getPuerto());
                    }
                }

            } else {
            	//Si ya se alcanzó el máximo de jugadores, se rebota al que se quiera conectar
                enviarMensaje("Lleno", paquete.getAddress(), paquete.getPort());
            }
        } else if(indice==-1){ // Si se quiere hacer algo pero no se conectó antes, se lo rebota
            System.out.println("Cliente no conectado");
            this.enviarMensaje("NoConectado", paquete.getAddress(), paquete.getPort());
            return;
        } else { // Si alguno se quiere mover
            switch(partes[0]){
                case "Mover":
                	gameController.moverJugador(Integer.parseInt(partes[1]), Boolean.parseBoolean(partes[2]));
                    break;
                case "Saltar":
                	gameController.saltar(Integer.parseInt(partes[1]));
                	break;
                case "Atacar":
                	gameController.atacar(Integer.parseInt(partes[1]));
                	break;
            }
        }
    }

    private int encontrarIndiceCliente(DatagramPacket paquete) {
        int i = 0;
        int indiceCliente = -1;
        while(i < clientes.size() && indiceCliente == -1) {
            Cliente cliente = clientes.get(i);
            String identificador = paquete.getAddress().toString()+":"+paquete.getPort();
            if(identificador.equals(cliente.getId())){
                indiceCliente = i;
            }
            i++;

        }
        return indiceCliente;
    }

    public void enviarMensaje(String mensaje, InetAddress ipCliente, int puertoCliente) {
        byte[] bytesMensaje = mensaje.getBytes();
        DatagramPacket paquete = new DatagramPacket(bytesMensaje, bytesMensaje.length, ipCliente, puertoCliente);
        try {
            socket.send(paquete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void terminar(){
        this.fin = true;
        socket.close();
        this.interrupt();
    }

    public void enviarMensajeATodos(String mensaje) {
        for (Cliente cliente : clientes) {
            enviarMensaje(mensaje, cliente.getIp(), cliente.getPuerto());
        }
    }

    public void desconectarClientes() {
        for (Cliente cliente : clientes) {
            enviarMensaje("Disconnect", cliente.getIp(), cliente.getPuerto());
        }
        this.clientes.clear();
        this.clientesConectados = 0;
    }
}