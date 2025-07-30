package io.github.timoria;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import entorno.Plataforma;
import personajes.InputPersonaje;
import personajes.Personaje;

public class Nivel1 extends NivelBase {

	private Personaje jugador;

	public Nivel1(Principal juego, Skin skin) {
		super(juego, "FondoNivel1.jpeg");

		jugador = new Personaje(mundo, "Jugador1", anchoViewport);
		super.escena.addActor(jugador);

		Plataforma plataforma = new Plataforma(mundo, 200, 100, 150, 30);
		super.escena.addActor(plataforma);
	}
	
	@Override
	protected InputMultiplexer crearMultiplexer() {
	    InputMultiplexer multiplexer = new InputMultiplexer();
	    multiplexer.addProcessor(new InputPersonaje(jugador));
	    multiplexer.addProcessor(escena);
	    return multiplexer;
	}
}
