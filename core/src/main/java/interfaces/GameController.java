package interfaces;

public interface GameController {
    void procesarAccionesEntidades(String[] mensaje);
    void cambiarPantalla();
    void recogerItem(int idLlave, int idJugador);
    void dañarJugador(int idJugador, int nuevaVida);
    void moverPlataformaMovil(int id, float posX, float posY);
    void abrirPuerta(int id);
    void servidorDesconectado();

    void actualizarPosicionEnemigo(int id, float posX, float posY);
    void desaparecerEnemigo(int id);
    void actualizarPosicionJugador(int id, float posX, float posY, boolean mirandoDerecha);

    void matarJugador(int id);
    void empezarJuego();
    void conectar(int idJugador);
    void desconectar();
    void volverAlMenu();
    void terminarJuego();
    void procesarAccionesJugador(String[] mensaje, int idJugador);
    void procesarAccionesEnemigo(String[] mensaje, int idEnemigo);

    // Métodos existentes
    void activarPalanca(int idPalanca);
    void aplicarMejora(int idJugador, String tipoMejora);
    void mostrarAtaqueEnemigo(int idEnemigo);

    // ✅ NUEVO: Mostrar animación de ataque del jugador
    void mostrarAtaqueJugador(int idJugador);
}
