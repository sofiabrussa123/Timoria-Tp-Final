package interfaces;

public interface GameController {
    void procesarAccionesEntidades(String[] mensaje);
<<<<<<< HEAD
=======
    void cambiarPantalla();
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
    void recogerItem(int idLlave, int idJugador);
    void dañarJugador(int idJugador, int nuevaVida);
    void moverPlataformaMovil(int id, float posX, float posY);
    void abrirPuerta(int id);
    void servidorDesconectado();

    void actualizarPosicionEnemigo(int id, float posX, float posY);
    void desaparecerEnemigo(int id);
    void actualizarPosicionJugador(int id, float posX, float posY, boolean mirandoDerecha);

    void matarJugador(int id);
<<<<<<< HEAD
    void conectar(int idJugador);
    void volverAlMenu();
=======
    void empezarJuego();
    void conectar(int idJugador);
    void desconectar();
    void volverAlMenu();
    void terminarJuego();
    void procesarAccionesJugador(String[] mensaje, int idJugador);
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
    void procesarAccionesEnemigo(String[] mensaje, int idEnemigo);

    // Métodos existentes
    void activarPalanca(int idPalanca);
    void aplicarMejora(int idJugador, String tipoMejora);
<<<<<<< HEAD

    // ✅ NUEVO: Mostrar animación de ataque del jugador
    void mostrarAtaqueEnemigo(int idEnemigo);
    void mostrarAtaqueJugador(int idJugador);

    // ✅ AGREGAR ESTE
    void mostrarDañoEnemigo(int idEnemigo);
=======
    void mostrarAtaqueEnemigo(int idEnemigo);

    // ✅ NUEVO: Mostrar animación de ataque del jugador
    void mostrarAtaqueJugador(int idJugador);
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
}
