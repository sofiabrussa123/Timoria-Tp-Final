package Red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import com.badlogic.gdx.Game;

import com.badlogic.gdx.Gdx;
import interfaces.GameController;
import pantallas.Menu;

public class HiloCliente extends Thread {

    private DatagramSocket socket;
    private int puertoServidor = 5555;
    private String ipServidorStr = "localhost";
    private InetAddress ipServidor;
    private boolean end = false;
    private GameController gameController;
    private int idAsignado;
    private ControladorDeConexiones controladorDeConexiones;
    private Game juego;

    public HiloCliente(ControladorDeConexiones controladorDeConexiones, Game juego) {
        this.juego = juego;
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
                    socket.setSoTimeout(5000);
                }
                socket.receive(packet);
                procesarMensaje(packet);
            } catch (java.net.SocketTimeoutException e) {
                System.err.println("⚠️ No se reciben datos del servidor (5s timeout)...");

                if (controladorDeConexiones != null) {
                    controladorDeConexiones.servidorDesconectado();
                } else if (gameController != null) {
                    gameController.servidorDesconectado();
                }
                break;

            } catch (IOException e) {
                if (!socket.isClosed()) {
                    System.err.println("❌ Error al recibir paquete: " + e.getMessage());

                    if (controladorDeConexiones != null) {
                        controladorDeConexiones.servidorDesconectado();
                    } else if (gameController != null) {
                        gameController.servidorDesconectado();
                    }
                    break;
                }
            }
        } while(!end);
    }

    private void procesarMensaje(DatagramPacket packet) {
        String message = (new String(packet.getData())).trim();
        String[] parts = message.split(":");

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
                    desconectar();

                case "Desconectar":
                    desconectar();
                    break;

                case "ReiniciarNivel":
                    if (controladorDeConexiones != null) {
                        controladorDeConexiones.reiniciarNivel();
                    }
                    break;

                case "Estado":
                    if (this.gameController != null && parts.length >= 6) {
                        if (parts[1].equals("Jugador")) {
                            int idJugador = Integer.parseInt(parts[2]);
                            float posX = Float.parseFloat(parts[3]);
                            float posY = Float.parseFloat(parts[4]);
                            boolean direccion = Boolean.parseBoolean(parts[5]);

                            this.gameController.actualizarPosicionJugador(idJugador, posX, posY, direccion);
                        }
                    }
                    break;

                case "Jugador":
                    if (this.gameController != null && parts.length >= 3) {
                        int idJugador = Integer.parseInt(parts[1]);

                        if (parts[2].equals("Dañar") && parts.length >= 4) {
                            int nuevaVida = Integer.parseInt(parts[3]);
                            this.gameController.dañarJugador(idJugador, nuevaVida);
                        }
                        else if (parts[2].equals("Matar")) {
                            this.gameController.matarJugador(idJugador);
                        }
                        else if (parts[2].equals("ActualizarVida") && parts.length >= 5) {
                            int vida = Integer.parseInt(parts[3]);
                            int vidaMax = Integer.parseInt(parts[4]);
                            this.gameController.dañarJugador(idJugador, vida);
                        }
                        else if (parts[2].equals("MejoraAplicada") && parts.length >= 4) {
                            String tipoMejora = parts[3];
                            this.gameController.aplicarMejora(idJugador, tipoMejora);
                        }
                        else if (parts[2].equals("Atacar")) {
                            this.gameController.mostrarAtaqueJugador(idJugador);
                        }
                    }
                    break;

                case "Enemigo":
                    if (this.gameController != null && parts.length >= 3) {
                        int idEnemigo = Integer.parseInt(parts[1]);

                        if (parts[2].equals("ActualizarPosicion") && parts.length >= 5) {
                            float posX = Float.parseFloat(parts[3]);
                            float posY = Float.parseFloat(parts[4]);
                            this.gameController.actualizarPosicionEnemigo(idEnemigo, posX, posY);
                        }
                        else if (parts[2].equals("Atacando")) {
                            this.gameController.mostrarAtaqueEnemigo(idEnemigo);
                        }
                        else if (parts[2].equals("RecibirDaño")) {
                            this.gameController.mostrarDañoEnemigo(idEnemigo);
                        }
                        else if (parts[2].equals("Desaparecer")) {
                            this.gameController.desaparecerEnemigo(idEnemigo);
                        }
                        else {
                            this.gameController.procesarAccionesEnemigo(parts, idEnemigo);
                        }
                    }
                    break;

                case "Puerta":
                    if (this.gameController != null && parts.length >= 3) {
                        int idPuerta = Integer.parseInt(parts[1]);

                        if (parts[2].equals("Abrir") || parts[2].equals("Desbloquear")) {

                            final int idPuertaFinal = idPuerta;
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    gameController.abrirPuerta(idPuertaFinal);
                                }
                            });
                        }
                    }
                    break;

                case "PlataformaMovil":
                    if (this.gameController != null && parts.length >= 4) {
                        int idPlataforma = Integer.parseInt(parts[1]);
                        float posX = Float.parseFloat(parts[2]);
                        float posY = Float.parseFloat(parts[3]);

                        this.gameController.moverPlataformaMovil(idPlataforma, posX, posY);
                    }
                    break;

                case "Palanca":
                    if (this.gameController != null && parts.length >= 3 && parts[2].equals("Activar")) {
                        int idPalanca = Integer.parseInt(parts[1]);
                        this.gameController.activarPalanca(idPalanca);
                    }
                    break;

                case "Llave":

                    if (this.gameController != null && parts.length >= 4) {
                        int idLlave = Integer.parseInt(parts[1]);
                        int idJugador = Integer.parseInt(parts[3]);

                        this.gameController.recogerItem(idLlave, idJugador);
                    } else {
                        System.err.println("❌ [HILOCLIENTE] No se puede procesar - gameController: " +
                            (this.gameController != null) + ", parts.length: " + parts.length);
                    }
                    break;

                case "CambiarPantalla":
                    if (this.gameController != null && parts.length >= 2) {
                        if (parts[1].equals("PantallaGanaste")) {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    if (controladorDeConexiones != null) {
                                        controladorDeConexiones.cambiarAPantallaGanaste();
                                    }
                                }
                            });
                        }
                    }
                    break;

                default:
                    if (this.gameController != null) {
                        this.gameController.procesarAccionesEntidades(parts);
                    }
                    break;
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ Error al parsear números en: " + message);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("❌ Mensaje mal formado: " + message);
        } catch (Exception e) {
            System.err.println("❌ Error al procesar: " + message);
            e.printStackTrace();
        }
    }

    public void enviarMensaje(String message) {
        if (socket == null || socket.isClosed()) {
            System.err.println("❌ Socket cerrado, no se puede enviar mensaje");
            return;
        }

        if (ipServidor == null) {
            System.err.println("❌ Dirección de servidor no establecida");
            return;
        }

        byte[] byteMessage = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, ipServidor, puertoServidor);

        try {
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("❌ Error al enviar mensaje: " + e.getMessage());
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

    private void desconectar(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                terminar();
                juego.setScreen(new Menu(juego));
            }
        });
    }
}
