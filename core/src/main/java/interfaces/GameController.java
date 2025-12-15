package interfaces;

public interface GameController {
    void recogerItem(int idLlave, int idJugador);
    void dañarJugador(int idJugador, int nuevaVida);
    void moverPlataformaMovil(int id, float posX, float posY);
    void abrirPuerta(int id);
    void servidorDesconectado();

    void actualizarPosicionEnemigo(int id, float posX, float posY);
    void desaparecerEnemigo(int id);
    void actualizarPosicionJugador(int id, float posX, float posY, boolean mirandoDerecha);

    void matarJugador(int id);
    void conectar(int idJugador);
    void volverAlMenu();
    void procesarAccionesEnemigo(String[] mensaje, int idEnemigo);

    void activarPalanca(int idPalanca);
    void aplicarMejora(int idJugador, String tipoMejora);

    void mostrarAtaqueEnemigo(int idEnemigo);
    void mostrarAtaqueJugador(int idJugador);

    void mostrarDañoEnemigo(int idEnemigo);
}
