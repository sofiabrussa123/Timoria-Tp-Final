package niveles;

import io.github.timoria.Principal;
import niveles.entorno.BotonActivador;
import niveles.entorno.Plataforma;
import niveles.entorno.PuertaLlegada;
import personajes.Enemigo;
import personajes.Personaje;

public class Nivel1 extends NivelBase {

    public Nivel1(Principal juego) {
    	
        super(juego, "FondoNivel1.jpeg");

        // Crear jugador
        super.jugador = new Personaje(mundo, "Jugador1", 100, 85, juego);
        super.escena.addActor(super.jugador);

        // Crear plataformas
        Plataforma plataforma1 = new Plataforma(mundo, 200, 130, 150, 20);
        Plataforma plataforma2 = new Plataforma(mundo, 495, 200, 75, 20);
        Plataforma plataforma3 = new Plataforma(mundo, 80, 250, 95, 20);
        Plataforma piso = new Plataforma(mundo, 0, 10, 800, 50);

        // Crear puerta 
        PuertaLlegada puerta = new PuertaLlegada(mundo, 590, 50, 50, 95);

        //Crear boton
        BotonActivador boton = new BotonActivador(mundo, 532, 210, 30, 45, puerta);        

        // Crear enemigo
        Enemigo enemigo1 = new Enemigo(mundo, 400, 150, 10, jugador);        

        // Agregar todos los actores
        super.escena.addActor(puerta);
        super.escena.addActor(boton);
        super.escena.addActor(enemigo1);
        super.escena.addActor(plataforma1);
        super.escena.addActor(plataforma2);
        super.escena.addActor(plataforma3);
        super.escena.addActor(piso);
    }
}
