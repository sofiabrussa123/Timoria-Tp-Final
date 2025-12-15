package Red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import interfaces.ConectionManager;
import interfaces.GameController;
import interfaces.ControladorDeConexiones;

public class HiloCliente extends Thread {

    private DatagramSocket socket;
    private int puertoServidor = 5555;
    private String ipServidorStr = "localhost";
    private InetAddress ipServidor;
    private boolean end = false;
    private GameController gameController;
    private int idAsignado;
    private ControladorDeConexiones controladorDeConexiones;

    public HiloCliente(ControladorDeConexiones controladorDeConexiones) {
        this.controladorDeConexiones = controladorDeConexiones;

        try {
            ipServidor = InetAddress.getByName(ipServidorStr);
            socket = new DatagramSocket();
        } catch (SocketException | UnknownHostException e) {
            System.err.println("Error al inicializar HiloCliente: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        do {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try {
                if(this.idAsignado == -1){
                    socket.setSoTimeout(5000); // 5 segundos timeout
                } else socket.setSoTimeout(0);

                socket.receive(packet);
                procesarMensaje(packet);
            } catch (java.net.SocketTimeoutException e) {
                System.err.println("⚠️ No se reciben datos del servidor (5s timeout)...");

                // ✅ Notificar desconexión del servidor
                if (controladorDeConexiones != null) {
                    controladorDeConexiones.servidorDesconectado();
                } else if (gameController != null) {
                    gameController.servidorDesconectado();
                }

                break; // Salir del loop

            } catch (IOException e) {
                if (!socket.isClosed()) {
                    System.err.println("❌ Error al recibir paquete: " + e.getMessage());

                    // ✅ Notificar desconexión
                    if (controladorDeConexiones != null) {
                        controladorDeConexiones.servidorDesconectado();
                    } else if (gameController != null) {
                        gameController.servidorDesconectado();
                    }

                    break;
                }
            }
        } while(!end);

        System.out.println("🛑 HiloCliente finalizado");
    }

    private void procesarMensaje(DatagramPacket packet) {
        String message = (new String(packet.getData())).trim();
        String[] parts = message.split(":");

        //System.out.println("Mensaje recibido: " + message);

        try {
            switch(parts[0]) {
                case "YaConectado":
                    System.out.println("Ya estas conectado");
                    break;

                case "Conectado":
                    System.out.println("Conectado al servidor");
                    this.ipServidor = packet.getAddress();
                    if (parts.length > 1) {
                        controladorDeConexiones.asignarId(Integer.parseInt(parts[1]));
                        this.idAsignado = Integer.parseInt(parts[1]);
                    }
                    break;

                case "Lleno":
                    System.out.println("Servidor lleno");
                    this.end = true;
                    break;

                case "EmpezarHistoria":
                    if (controladorDeConexiones != null) {
                        controladorDeConexiones.empezarHistoria();
                    }
                    break;

                case "EmpezarJuego":
                    if (controladorDeConexiones != null) {
                        controladorDeConexiones.empezarJuego();
                    }
                    break;

                case "TerminarJuego":
                    if (this.gameController != null) {
                        this.gameController.terminarJuego();
                    }
                    break;

                case "Desconectar":
                    if (this.gameController != null) {
                        this.gameController.desconectar();
                    }
                    break;

                // ✅ NUEVO: Reiniciar nivel
                case "ReiniciarNivel":
                    if (controladorDeConexiones != null) {
                        controladorDeConexiones.reiniciarNivel();
                    }
                    break;

                case "Estado":
                    System.out.println("📄 Procesando estado: " + message);
                    if (this.gameController != null) {
                        if (parts[1].equals("Jugador")) {
                            int idJugador = Integer.parseInt(parts[2]);
                            float posX = Float.parseFloat(parts[3]);
                            float posY = Float.parseFloat(parts[4]);
                            boolean direccion = Boolean.parseBoolean(parts[5]);

                            System.out.println("👤 Actualizando jugador " + idJugador + " en (" + posX + ", " + posY + ")");

                            this.gameController.actualizarPosicionJugador(idJugador, posX, posY, direccion);
                        }
                    } else {
                        System.out.println("⚠️ GameController null o mensaje incompleto");
                    }
                    break;

                // En HiloCliente.java - Actualizar el case "Jugador"

                case "Jugador":
                    if (this.gameController != null && parts.length >= 2) {
                        int idJugador = Integer.parseInt(parts[1]);

                        // Procesar según la acción
                        if (parts.length >= 4 && parts[2].equals("MejoraAplicada")) {
                            String tipoMejora = parts[3];
                            this.gameController.aplicarMejora(idJugador, tipoMejora);
                            System.out.println("⬆️ Cliente: Jugador " + idJugador + " mejoró " + tipoMejora);
                        }
                        // ✅ NUEVO: Procesar ataque
                        else if (parts.length >= 3 && parts[2].equals("Atacar")) {
                            this.gameController.mostrarAtaqueJugador(idJugador);
                            System.out.println("⚔️ Cliente: Jugador " + idJugador + " atacando");
                        }
                        else if (parts.length >= 4 && parts[2].equals("Dañar")) {
                            int nuevaVida = Integer.parseInt(parts[3]);
                            this.gameController.dañarJugador(idJugador, nuevaVida);
                            System.out.println("🩸 Cliente: Jugador " + idJugador + " dañado → Vida: " + nuevaVida);
                        }
                        else if (parts.length >= 3 && parts[2].equals("Matar")) {
                            this.gameController.matarJugador(idJugador);
                            System.out.println("💀 Cliente: Jugador " + idJugador + " murió");
                        }
                        else {
                            this.gameController.procesarAccionesJugador(parts, idJugador);
                        }
                    }
                    break;

                case "Enemigo":
                    System.out.println("👹 Acción de enemigo: " + message);
                    if (this.gameController != null && parts.length >= 3) {
                        int idEnemigo = Integer.parseInt(parts[1]);

                        if (parts[2].equals("ActualizarPosicion") && parts.length >= 5) {
                            float posX = Float.parseFloat(parts[3]);
                            float posY = Float.parseFloat(parts[4]);

                            System.out.println("👹 Moviendo enemigo " + idEnemigo + " a (" + posX + ", " + posY + ")");

                            this.gameController.actualizarPosicionEnemigo(idEnemigo, posX, posY);
                        } else if (parts[2].equals("Atacando")) {
                            // ✅ NUEVO: Mostrar animación de ataque del enemigo
                            this.gameController.mostrarAtaqueEnemigo(idEnemigo);
                        } else {
                            this.gameController.procesarAccionesEnemigo(parts, idEnemigo);
                        }
                    }
                    break;

                case "Puerta":
                    if (this.gameController != null && parts.length >= 3) {
                        int idPuerta = Integer.parseInt(parts[1]);
                        if (parts[2].equals("Abrir") || parts[2].equals("Desbloquear")) {
                            this.gameController.abrirPuerta(idPuerta);
                        }
                    }
                    break;

                // ✅ NUEVO: Procesar movimiento de plataforma móvil
                case "PlataformaMovil":
                    if (this.gameController != null && parts.length >= 4) {
                        int idPlataforma = Integer.parseInt(parts[1]);
                        float posX = Float.parseFloat(parts[2]);
                        float posY = Float.parseFloat(parts[3]);
                        this.gameController.moverPlataformaMovil(idPlataforma, posX, posY);
                    }
                    break;

                // ✅ NUEVO: Procesar activación de palanca
                case "Palanca":
                    if (this.gameController != null && parts.length >= 3 && parts[2].equals("Activar")) {
                        int idPalanca = Integer.parseInt(parts[1]);
                        this.gameController.activarPalanca(idPalanca);
                        System.out.println("🔧 Cliente: Palanca " + idPalanca + " activada");
                    }
                    break;

                case "Llave":
                    if (this.gameController != null && parts.length >= 4) {
                        int idLlave = Integer.parseInt(parts[1]);
                        int idJugador = Integer.parseInt(parts[3]);
                        this.gameController.recogerItem(idLlave, idJugador);
                    }
                    break;

                default:
                    if (this.gameController != null) {
                        this.gameController.procesarAccionesEntidades(parts);
                    }
                    break;
            }
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear números en mensaje: " + message);
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Mensaje mal formado: " + message);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error al procesar mensaje: " + message);
            e.printStackTrace();
        }
    }

    public void enviarMensaje(String message) {
        if (socket == null || socket.isClosed()) {
            System.err.println("Socket cerrado, no se puede enviar mensaje");
            return;
        }

        if (ipServidor == null) {
            System.err.println("Dirección de servidor no establecida");
            return;
        }

        byte[] byteMessage = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, ipServidor, puertoServidor);

        try {
            socket.send(packet);
            System.out.println("Mensaje enviado: " + message);
        } catch (IOException e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
        }
    }

    public void terminar() {
        this.end = true;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        this.interrupt();
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public int getIdAsignado() {
        return this.idAsignado;
    }

    public boolean tieneGameController(){
        if(this.gameController != null){
            return true;
        } else return false;
    }
}
