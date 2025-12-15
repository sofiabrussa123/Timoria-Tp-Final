package Red;

import com.badlogic.gdx.Game;

public class ServidorManager {

    private static HiloServidor instanciaServidor = null;
    private static boolean servidorIniciado = false;

    public static HiloServidor obtenerServidor(Game juego) {
        if (instanciaServidor == null) {
            instanciaServidor = new HiloServidor(juego);
            instanciaServidor.start();
            servidorIniciado = true;
        }

        return instanciaServidor;
    }

    public static void reiniciarServidor(Game juego) {
        if (instanciaServidor != null) {
            instanciaServidor.desconectarClientes();
        }
    }

    public static void terminarServidor() {
        if (instanciaServidor != null) {
            instanciaServidor.terminar();
            instanciaServidor = null;
            servidorIniciado = false;
        }
    }

    public static boolean estaIniciado() {
        return servidorIniciado && instanciaServidor != null;
    }
}
