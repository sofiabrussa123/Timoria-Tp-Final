package interfaces;

public interface GameController {
	void moverJugador(int idJugador, boolean derecha);
	void saltar(int idJugador);
	void atacar(int idJugador);
	void empezar();
}
