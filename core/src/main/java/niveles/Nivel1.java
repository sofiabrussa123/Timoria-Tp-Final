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

    private boolean inicializado = false;

    public Nivel1(Game juego) {
        super(juego, "FondoNivel1.jpeg");
        this.setFriendlyFire(true);
    }

    @Override
    public void show() {
        // Primero llamar al show del padre para inicializar hiloCliente
        super.show();

        // Ahora crear los elementos del nivel si no se han creado
        if (!inicializado) {
            inicializarNivel();
            inicializado = true;
        }
    }

    private void inicializarNivel() {
        // Ahora hiloCliente ya está inicializado
        super.jugador1 = new Jugador(mundo, "Jugador1", 100, 85, 1, NivelBase.getMejorasJugador1(), super.hiloCliente);
        super.jugador2 = new Jugador(mundo, "Jugador2", 120, 85, 2, NivelBase.getMejorasJugador2(), super.hiloCliente);

        Plataforma plataforma2 = new Plataforma(mundo, 495, 200, 75, 20, asignarIdEntidad());
        Plataforma plataforma3 = new Plataforma(mundo, 80, 250, 95, 20, asignarIdEntidad());
        Plataforma piso = new Plataforma(mundo, 0, 10, 1000, 50, asignarIdEntidad());

        Palanca palanca = new Palanca(mundo, 170, 120, asignarIdEntidad());
        PlataformaMovil plataformaMovil = new PlataformaMovil(mundo, 200, 130, palanca, asignarIdEntidad());

        PuertaLlegada puerta = new PuertaLlegada(mundo, 590, 50, asignarIdEntidad());
        LlaveActivadora llave = new LlaveActivadora(mundo, 532, 210, puerta, asignarIdEntidad());

        Enemigo enemigo = new Enemigo(mundo, 400, 150, asignarIdEntidad());

        añadirElemento(super.jugador1);
        añadirElemento(super.jugador2);
        añadirElemento(puerta);
        añadirElemento(llave);
        añadirElemento(enemigo);
        añadirElemento(plataformaMovil);
        añadirElemento(palanca);
        añadirElemento(plataforma2);
        añadirElemento(plataforma3);
        añadirElemento(piso);
    }
}
