package interfaces;

public interface GameController {
	void procesarAccionesEntidades(String[] mensaje);
	void cambiarPantalla();
	void recogerItem(int idLlave, int idJugador);
	void dañarJugador(int idJugador, int nuevaVida);
	void moverPlataformaMovil(int id, int posX, int posY);
	void abrirPuerta(int id);
	void actualizarPosicionEnemigo(int id, int posX, int posY);
	void desaparecerEnemigo(int id);
	void actualizarPosicionJugador(int id, int posX, int posY);
	void matarJugador(int id);
	void empezarJuego();
	void conectar(int idJugador);
    void desconectar();
	void volverAlMenu();
	void terminarJuego();
	void procesarAccionesJugador(String[] mensaje, int idJugador);
	void procesarAccionesEnemigo(String[] mensaje, int idEnemigo);
}
