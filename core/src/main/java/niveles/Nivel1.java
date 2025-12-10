package niveles;

import com.badlogic.gdx.Game;

import niveles.entorno.LlaveActivadora;
import niveles.entorno.Palanca;
import niveles.entorno.Plataforma;
import niveles.entorno.PlataformaMovil;
import niveles.entorno.PuertaLlegada;
import personajes.Enemigo;
import personajes.Jugador;

public class Nivel1 extends NivelBase {

    public Nivel1(Game juego) {
        super(juego, "FondoNivel1.jpeg");

        this.setFriendlyFire(true);

        super.jugador1 = new Jugador(mundo, "Jugador1", 100, 85, 1, NivelBase.getMejorasJugador1());
        super.jugador2 = new Jugador(mundo, "Jugador2", 120, 85, 2, NivelBase.getMejorasJugador2());

        Plataforma plataforma2 = new Plataforma(mundo, 495, 200, 75, 20);
        Plataforma plataforma3 = new Plataforma(mundo, 80, 250, 95, 20);
        Plataforma piso = new Plataforma(mundo, 0, 10, 1000, 50);

        Palanca palanca = new Palanca(mundo, 170, 120);
        PlataformaMovil plataformaMovil = new PlataformaMovil(mundo, 200, 130, 2, 200, palanca);

        PuertaLlegada puerta = new PuertaLlegada(mundo, 590, 50, 50, 95);
        LlaveActivadora llave = new LlaveActivadora(mundo, 532, 210, puerta);

        Enemigo enemigo = new Enemigo(mundo, 400, 150, this);

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
}
