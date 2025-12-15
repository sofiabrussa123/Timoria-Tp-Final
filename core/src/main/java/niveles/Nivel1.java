package niveles;

import com.badlogic.gdx.Game;

import Red.HiloCliente;
import niveles.entorno.LlaveActivadora;
import niveles.entorno.Palanca;
import niveles.entorno.Plataforma;
import niveles.entorno.PlataformaMovil;
import niveles.entorno.PuertaLlegada;
import personajes.Enemigo;
import personajes.Jugador;

public class Nivel1 extends NivelBase {

    private boolean inicializado = false;

    // ✅ RECIBIR HiloCliente en el constructor
    public Nivel1(Game juego, HiloCliente hiloCliente) {
        super(juego, "FondoNivel1.jpeg");

        // ✅ ASIGNAR HILOCLIENTE
        this.hiloCliente = hiloCliente;
        System.out.println("✅ [CLIENTE] HiloCliente asignado: " + (hiloCliente != null));

        this.setFriendlyFire(true);

        // ✅ CREAR JUGADORES EN EL CONSTRUCTOR
        System.out.println("🎮 [CLIENTE] Creando jugadores en constructor");
        super.jugador1 = new Jugador("Jugador1", 100, 85, 1,
            NivelBase.getMejorasJugador1(), hiloCliente);
        super.jugador2 = new Jugador("Jugador2", 120, 85, 2,
            NivelBase.getMejorasJugador2(), hiloCliente);
        System.out.println("✅ Jugadores creados");
    }

    @Override
    public void show() {
        System.out.println("🎬 [CLIENTE] Nivel1.show() llamado");

        // ✅ Asignar GameController al hiloCliente
        if (this.hiloCliente != null) {
            this.hiloCliente.setGameController(this);
            System.out.println("🔌 GameController asignado a HiloCliente");
        }

        // ✅ AHORA SÍ LLAMAR A super.show() (creará barras)
        super.show();

        // ✅ CONFIGURAR QUIÉN ES MI JUGADOR (para sonidos)
        if (idJugadorActivo == 1) {
            super.jugador1.setEsMiJugador(true);
            super.jugador2.setEsMiJugador(false);
        } else if (idJugadorActivo == 2) {
            super.jugador1.setEsMiJugador(false);
            super.jugador2.setEsMiJugador(true);
        }

        // ✅ CREAR RESTO DE ELEMENTOS
        if (!inicializado) {
            inicializarNivel();
            inicializado = true;
        }
    }

    private void inicializarNivel() {
        System.out.println("🗺️ Inicializando resto del nivel");

        // ✅ IMPORTANTE: Los IDs deben coincidir con el servidor
        // El servidor asigna IDs en orden, así que hacemos lo mismo aquí

        // Servidor crea: piso(1), plat2(2), plat3(3), palanca(4), platMovil(5), puerta(6), llave(7), enemigo(8)

        int idPiso = 1;
        int idPlat2 = 2;
        int idPlat3 = 3;
        int idPalanca = 4;
        int idPlatMovil = 5;
        int idPuerta = 6;
        int idLlave = 7;
        int idEnemigo = 8;

        // Crear plataformas
        Plataforma piso = new Plataforma(0, 10, 1000, 50, idPiso);
        Plataforma plataforma2 = new Plataforma(495, 200, 75, 20, idPlat2);
        Plataforma plataforma3 = new Plataforma(80, 250, 95, 20, idPlat3);

        // Crear palanca y plataforma móvil
        Palanca palanca = new Palanca(170, 120, idPalanca);
        PlataformaMovil plataformaMovil = new PlataformaMovil(200, 130, palanca, idPlatMovil);

        // Crear puerta y llave
        PuertaLlegada puerta = new PuertaLlegada(590, 50, idPuerta);
        LlaveActivadora llave = new LlaveActivadora(532, 210, puerta, idLlave);

        // Crear enemigo
        Enemigo enemigo = new Enemigo(400, 150, idEnemigo);

        // ✅ Agregar jugadores al stage Y al mapa de entidades
        añadirElemento(super.jugador1);
        añadirElemento(super.jugador2);
        añadirElemento(piso);
        añadirElemento(plataforma2);
        añadirElemento(plataforma3);
        añadirElemento(palanca);
        añadirElemento(plataformaMovil);
        añadirElemento(puerta);
        añadirElemento(llave);
        añadirElemento(enemigo);

        System.out.println("✅ Nivel inicializado con " + entidades.size() + " entidades");
        System.out.println("📋 IDs en mapa: " + entidades.keySet());
    }

    @Override
    public void mostrarAtaqueEnemigo(int idEnemigo) {

    }
}
