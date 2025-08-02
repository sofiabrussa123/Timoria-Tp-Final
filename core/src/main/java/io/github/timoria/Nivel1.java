package io.github.timoria;

import com.badlogic.gdx.InputMultiplexer;
import entorno.Plataforma;
import entorno.PuertaLlegada;
import personajes.Enemigo;
import personajes.InputPersonaje;
import personajes.Personaje;

public class Nivel1 extends NivelBase {

    private Personaje jugador;

    public Nivel1(Principal juego) {
        super(juego, "FondoNivel1.jpeg");


        // Crear jugador
        jugador = new Personaje(mundo, "Jugador1", 100, 85, juego);
        escena.addActor(jugador);

        // Crear plataformas
        Plataforma plataforma1 = new Plataforma(mundo, 200, 130, 150, 20);
        Plataforma plataforma2 = new Plataforma(mundo, 495, 200, 95, 20);
        Plataforma plataforma3 = new Plataforma(mundo, 80, 250, 95, 20);
        Plataforma piso = new Plataforma(mundo, 0, 0, 800, 50);

        // Crear puerta bloqueada
        PuertaLlegada puerta = new PuertaLlegada(mundo, 600, 50, 40, 70);
        escena.addActor(puerta);


        // Crear enemigo
        Enemigo enemigo1 = new Enemigo(mundo, 400, 150, 10, jugador);
        escena.addActor(enemigo1);

        // Agregar plataformas
        escena.addActor(plataforma1);
        escena.addActor(plataforma2);
        escena.addActor(plataforma3);
        escena.addActor(piso);
    }

    @Override
    protected InputMultiplexer crearMultiplexer() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputPersonaje(jugador));
        multiplexer.addProcessor(escena);
        return multiplexer;
    }
}
