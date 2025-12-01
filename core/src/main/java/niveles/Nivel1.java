package niveles;

import com.badlogic.gdx.Game;
import niveles.entorno.*;
import personajes.*;

public class Nivel1 extends NivelBase {

    public Nivel1(Game juego) {

        this(juego, null, null);

    }

    // Constructor con mejoras
    public Nivel1(Game juego, MejoraTemporal mejorasJ1, MejoraTemporal mejorasJ2) {

        super(juego, "FondoNivel1.jpeg");

        // Crear jugadores con ID (añadir el parámetro 1 y 2 al final)
        super.jugador1 = new Personaje(mundo, "Jugador1", 100, 85, 1);
        super.jugador2 = new Personaje(mundo, "Jugador2", 120, 85, 2);

        // Aplicar mejoras guardadas si existen
        if (mejorasJ1 != null) {
            copiarMejoras(mejorasJ1, super.jugador1.getMejoras());
            super.jugador1.actualizarVidaConMejoras();
        }

        if (mejorasJ2 != null) {
            copiarMejoras(mejorasJ2, super.jugador2.getMejoras());
            super.jugador2.actualizarVidaConMejoras();
        }

        // Crear plataformas
        //Plataforma plataforma1 = new Plataforma(mundo, 200, 130, 150, 20);
        Plataforma plataforma2 = new Plataforma(mundo, 495, 200, 75, 20);
        Plataforma plataforma3 = new Plataforma(mundo, 80, 250, 95, 20);
        Plataforma piso = new Plataforma(mundo, 0, 10, 800, 50);

        Palanca palanca = new Palanca(mundo, 170, 120);
        PlataformaMovil plataformaMovil = new PlataformaMovil(mundo, 200, 130, 2, 200, palanca);

        // Crear puerta
        PuertaLlegada puerta = new PuertaLlegada(mundo, 590, 50, 50, 95);

        //Crear boton
        LlaveActivadora llave = new LlaveActivadora(mundo, 532, 210, puerta);

        // Crear enemigo
        Enemigo enemigo = new Enemigo(mundo, 400, 150, 10, this);

        // Agregar todos los actores
        super.escena.addActor(super.jugador1);
        super.escena.addActor(super.jugador2);
        super.escena.addActor(puerta);
        super.escena.addActor(llave);
        super.escena.addActor(enemigo);
        super.escena.addActor(plataformaMovil);
        super.escena.addActor(palanca);
        super.escena.addActor(plataforma2);
        super.escena.addActor(plataforma3);
        super.escena.addActor(piso);
    }

    // Método auxiliar para copiar mejoras
    private void copiarMejoras(MejoraTemporal origen, MejoraTemporal destino) {
        destino.reset(); // Resetear primero

        // Copiar mejoras de vida
        for (int i = 0; i < origen.getMejorasVida(); i++) {
            destino.mejorarVida();
        }

        // Copiar mejoras de velocidad
        for (int i = 0; i < origen.getMejorasVelocidad(); i++) {
            destino.mejorarVelocidad();
        }

        // Copiar mejoras de salto
        for (int i = 0; i < origen.getMejorasSalto(); i++) {
            destino.mejorarSalto();
        }
    }

}
