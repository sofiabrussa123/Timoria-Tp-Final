package niveles;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Actor;

import niveles.entorno.LlaveActivadora;
import niveles.entorno.Palanca;
import niveles.entorno.Plataforma;
import niveles.entorno.PlataformaMovil;
import niveles.entorno.PuertaLlegada;
import personajes.Enemigo;
import personajes.Jugador;
import personajes.accesorios.MejoraTemporal;

public class Nivel1 extends NivelBase {
	
	private int cantActores = 0;

    public Nivel1(Game juego) {

        this(juego, null, null);

    }

    // Constructor con mejoras
    public Nivel1(Game juego, MejoraTemporal mejorasJ1, MejoraTemporal mejorasJ2) {

        super(juego, "FondoNivel1.jpeg");
        // Crear jugadores con ID (añadir el parámetro 1 y 2 al final)
        super.jugador1 = new Jugador(mundo, "Jugador1", 100, 85, 1);
        super.jugador2 = new Jugador(mundo, "Jugador2", 120, 85, 2);

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
        Plataforma plataforma2 = new Plataforma(mundo, 495, 200, 75, 20, asignarIDActor());
        Plataforma plataforma3 = new Plataforma(mundo, 80, 250, 95, 20, asignarIDActor());
        Plataforma piso = new Plataforma(mundo, 0, 10, 800, 50, asignarIDActor());

        Palanca palanca = new Palanca(mundo, 170, 120, asignarIDActor());
        PlataformaMovil plataformaMovil = new PlataformaMovil(mundo, 200, 130, 2, 200, palanca, asignarIDActor());

        // Crear puerta
        PuertaLlegada puerta = new PuertaLlegada(mundo, 590, 50, asignarIDActor());

        //Crear boton
        LlaveActivadora llave = new LlaveActivadora(mundo, 532, 210, puerta, asignarIDActor());

        // Crear enemigo
        Enemigo enemigo = new Enemigo(mundo, 400, 150, this, asignarIDActor());        

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

    private int asignarIDActor() {
    	this.cantActores++;
    	return this.cantActores;
    }
}
