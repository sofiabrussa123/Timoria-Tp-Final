package io.github.timoria;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import personajes.Personaje;

public class Nivel1 extends NivelBase {
	
	private Personaje jugador;
	
	public Nivel1(Principal juego, Skin skin) {
		
		super(juego, "FondoNivel1.jpeg");
		
        // Crear el personaje usando el world del nivel base
        jugador = new Personaje(world, "Jugador1", super.viewportWidth);
        
        // Añadir el personaje al stage (si NivelBase extiende de Stage)
        super.escena.addActor(jugador);
		
	}
}
