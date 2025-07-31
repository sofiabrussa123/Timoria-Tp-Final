package io.github.timoria;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import entorno.Plataforma;
import entorno.PuertaLlegada;
import personajes.Enemigo;
import personajes.InputPersonaje;
import personajes.Personaje;


public class Nivel1 extends NivelBase {

	private Personaje jugador;

	public Nivel1(Principal juego, Skin skin) {
		super(juego, "FondoNivel1.jpeg");

		jugador = new Personaje(mundo, "Jugador1", 100, 85); // posición en píxeles
		super.escena.addActor(jugador);
		Plataforma plataforma = new Plataforma(mundo, 200, 130, 150, 20);
		Plataforma piso = new Plataforma(mundo, 0, 0, 800, 50);
		PuertaLlegada puerta = new PuertaLlegada(mundo, 600, 50, 40, 70);
		Enemigo enemigo1 = new Enemigo(mundo, 400, 150, 10, jugador);
		escena.addActor(enemigo1);
		escena.addActor(puerta);
		super.escena.addActor(plataforma);
		super.escena.addActor(piso);
	}
	
	@Override
	protected InputMultiplexer crearMultiplexer() {
	    InputMultiplexer multiplexer = new InputMultiplexer();
	    multiplexer.addProcessor(new InputPersonaje(jugador));
	    multiplexer.addProcessor(escena);
	    return multiplexer;
	}
}
