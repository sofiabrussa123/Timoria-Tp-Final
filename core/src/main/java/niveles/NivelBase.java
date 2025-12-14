package niveles;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import Red.HiloServidor;
import interfaces.GameController;
import niveles.entorno.LlaveActivadora;
import niveles.entorno.Palanca;
import niveles.entorno.Plataforma;
import niveles.entorno.PlataformaMovil;
import niveles.entorno.PuertaLlegada;
import personajes.Enemigo;
import personajes.Jugador;
import personajes.accesorios.MejoraTemporal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class NivelBase implements Screen, GameController {

    public static final float PIXELES_A_METROS = 1 / 100f;

    protected static MejoraTemporal mejorasJugador1 = new MejoraTemporal();
    protected static MejoraTemporal mejorasJugador2 = new MejoraTemporal();

    protected World mundo;
    protected HiloServidor hiloServidor;
    protected boolean friendlyFire = false;

    protected Jugador jugador1;
    protected Jugador jugador2;

    protected Map<Integer, Object> entidades = new HashMap<>();

    protected Game juego;

    private float acumuladorTiempo = 0f;
    private final float PASO_FISICO = 1 / 60f;

    // ✅ Control de broadcast de posiciones
    private float acumuladorBroadcast = 0f;
    private final float INTERVALO_BROADCAST = 1 / 20f; // 20 veces por segundo

    private ArrayList<Enemigo> enemigosAEliminar = new ArrayList<>();

    public NivelBase(Game juego) {
        this.juego = juego;
        this.mundo = new World(new Vector2(0f, -25f), true);
        this.establecerContactos();
    }

    public static MejoraTemporal getMejorasJugador1() {
        return mejorasJugador1;
    }

    public static MejoraTemporal getMejorasJugador2() {
        return mejorasJugador2;
    }

    public Jugador getJugador1() {
        return this.jugador1;
    }

    public Jugador getJugador2() {
        return this.jugador2;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    private void establecerContactos() {
        this.mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object a = contact.getFixtureA().getBody().getUserData();
                Object b = contact.getFixtureB().getBody().getUserData();

                // Lógica puerta
                if ((a instanceof Jugador && b instanceof PuertaLlegada) ||
                    (b instanceof Jugador && a instanceof PuertaLlegada)) {

                    PuertaLlegada puerta = (a instanceof PuertaLlegada) ? (PuertaLlegada) a : (PuertaLlegada) b;
                    if (puerta.sePuedeCruzar()) {
                        if (hiloServidor != null) {
                            hiloServidor.enviarMensajeATodos("CambiarPantalla:PantallaGanaste");
                        }
                        System.out.println("🏆 ¡Nivel completado!");
                    }
                }

                // Lógica activar la llave
                if (a instanceof Jugador && b instanceof LlaveActivadora ||
                    b instanceof Jugador && a instanceof LlaveActivadora) {
                    LlaveActivadora llave = a instanceof LlaveActivadora ? (LlaveActivadora) a : (LlaveActivadora) b;
                    Jugador personaje = a instanceof Jugador ? (Jugador) a : (Jugador) b;
                    llave.activarConJugador(personaje);

                    if (hiloServidor != null) {
                        hiloServidor.enviarMensajeATodos("Llave:" + llave.getID() + ":Recoger:" + personaje.getIdJugador());
                    }
                }

                // Lógica jugador activar palanca
                if (a instanceof Jugador && b instanceof Palanca ||
                    b instanceof Jugador && a instanceof Palanca) {
                    Palanca palanca = a instanceof Palanca ? (Palanca) a : (Palanca) b;
                    palanca.activar();

                    if (hiloServidor != null) {
                        hiloServidor.enviarMensajeATodos("Palanca:" + palanca.getID() + ":Activar");
                    }
                }

                // Lógica jugador apoyarse en plataforma
                if ((a instanceof Jugador && (b instanceof Plataforma || b instanceof PlataformaMovil)) ||
                    (b instanceof Jugador && (a instanceof Plataforma || a instanceof PlataformaMovil))) {

                    Jugador personaje = (a instanceof Jugador) ? (Jugador) a : (Jugador) b;
                    personaje.setEnElAire(false);
                }
            }

            @Override public void endContact(Contact contact) { }
            @Override public void preSolve(Contact contact, Manifold oldManifold) { }
            @Override public void postSolve(Contact contact, ContactImpulse impulse) { }
        });
    }

    @Override
    public void show() {
        if (this.hiloServidor != null) {
            System.out.println("✅ Registrando nivel como GameController");
            this.hiloServidor.setGameController(this);
        }
    }

    @Override
    public void render(float delta) {
        // ✅ Actualizar física
        acumuladorTiempo += delta;

        while (acumuladorTiempo >= PASO_FISICO) {
            mundo.step(PASO_FISICO, 6, 2);
            acumuladorTiempo -= PASO_FISICO;

            actualizarEntidades(PASO_FISICO);
        }

        // ✅ CRÍTICO: Broadcast posiciones periódicamente
        acumuladorBroadcast += delta;
        if (acumuladorBroadcast >= INTERVALO_BROADCAST) {
            broadcastPosiciones();
            acumuladorBroadcast = 0f;
        }

        // Verificar muerte de jugadores
        verificarMuerteJugadores();

        limpiarEntidades();
    }

    // ✅ NUEVO: Enviar posiciones de todos los jugadores a todos los clientes
    private void broadcastPosiciones() {
        if (hiloServidor == null) return;

        // Enviar posición de J1
        if (jugador1 != null && jugador1.getCuerpo() != null) {
            String estado = obtenerEstadoJugador(1);
            if (estado != null) {
                hiloServidor.enviarMensajeATodos(estado);
            }
        }

        // Enviar posición de J2
        if (jugador2 != null && jugador2.getCuerpo() != null) {
            String estado = obtenerEstadoJugador(2);
            if (estado != null) {
                hiloServidor.enviarMensajeATodos(estado);
            }
        }

        // ✅ También broadcast posiciones de plataformas móviles
        for (Object obj : entidades.values()) {
            if (obj instanceof PlataformaMovil) {
                PlataformaMovil plat = (PlataformaMovil) obj;
                if (plat.getCuerpo() != null) {
                    float x = plat.getCuerpo().getPosition().x;
                    float y = plat.getCuerpo().getPosition().y;
                    hiloServidor.enviarMensajeATodos(
                        String.format(java.util.Locale.US, "PlataformaMovil:%d:%.2f:%.2f",
                            plat.getID(), x, y)
                    );
                }
            }
        }
    }

    private void verificarMuerteJugadores() {
        if (this.jugador1 != null && this.jugador1.getVida() == 0) {
            System.out.println("💀 Jugador 1 murió");
            // Notificar a los clientes
            if (hiloServidor != null) {
                hiloServidor.enviarMensajeATodos("Jugador:1:Matar");
            }
        }

        if (this.jugador2 != null && this.jugador2.getVida() == 0) {
            System.out.println("💀 Jugador 2 murió");
            if (hiloServidor != null) {
                hiloServidor.enviarMensajeATodos("Jugador:2:Matar");
            }
        }
    }

    private void actualizarEntidades(float delta) {
        for (Object obj : entidades.values()) {
            if (obj instanceof Enemigo) {
                Enemigo enemigo = (Enemigo) obj;
                if (!enemigo.getMuerto()) {
                    enemigo.act(delta);
                }
            } else if (obj instanceof PlataformaMovil) {
                PlataformaMovil plataforma = (PlataformaMovil) obj;
                plataforma.act(delta);
            }
        }
    }

    private void limpiarEntidades() {
        enemigosAEliminar.clear();

        for (Object obj : entidades.values()) {
            if (obj instanceof Enemigo) {
                Enemigo enemigo = (Enemigo) obj;

                if (enemigo.getMuerto()) {
                    enemigosAEliminar.add(enemigo);
                }
            }
        }

        for (Enemigo enemigo : enemigosAEliminar) {
            enemigo.eliminar();
            enemigo.dispose();
            entidades.remove(enemigo.getID());

            if (hiloServidor != null) {
                hiloServidor.enviarMensajeATodos("Enemigo:" + enemigo.getID() + ":Desaparecer");
            }
        }
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        if (mundo != null) {
            mundo.dispose();
        }
    }

    protected void agregarEntidad(Object entidad, int id) {
        this.entidades.put(id, entidad);
    }

    @Override
    public void moverJugador(int id, boolean derecha) {
        Jugador jugador = (id == 1) ? this.jugador1 : this.jugador2;
        if (jugador != null) {
            if(derecha) jugador.moverDerecha();
            else jugador.moverIzquierda();
        }
    }

    @Override
    public void saltar(int idJugador) {
        Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;
        if (jugador != null && !jugador.getEnElAire()) {
            jugador.saltar();
        }
    }

    @Override
    public void atacar(int idJugador) {
        Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;
        if (jugador != null) {
            jugador.atacar(this.mundo, this.friendlyFire);
        }
    }

    @Override
    public void empezar() { }

    @Override
    public void detener(int idJugador) {
        Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;
        if (jugador != null) {
            jugador.detener();
        }
    }

    @Override
    public String obtenerEstadoJugador(int idJugador) {
        Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;

        if (jugador == null || jugador.getCuerpo() == null) {
            return null;
        }

        float x = jugador.getCuerpo().getPosition().x;
        float y = jugador.getCuerpo().getPosition().y;
        boolean mirandoDerecha = jugador.getMirandoDerecha();

        return String.format(java.util.Locale.US, "Estado:Jugador:%d:%.2f:%.2f:%b",
            idJugador, x, y, mirandoDerecha);
    }

    @Override
    public ArrayList<String> obtenerEstadosEnemigos() {
        return null;
    }

    @Override
    public void procesarMejora(int idJugador, String tipoMejora) {
        Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;
        MejoraTemporal mejoras = (idJugador == 1) ? mejorasJugador1 : mejorasJugador2;

        if (jugador == null || mejoras == null) return;

        boolean mejoraAplicada = false;

        switch(tipoMejora) {
            case "Vida":
                mejoraAplicada = mejoras.mejorarVida();
                if (mejoraAplicada) {
                    jugador.actualizarVidaConMejoras();
                    System.out.println("❤️ Jugador " + idJugador + " mejoró vida");
                }
                break;
            case "Velocidad":
                mejoraAplicada = mejoras.mejorarVelocidad();
                if (mejoraAplicada) {
                    System.out.println("⚡ Jugador " + idJugador + " mejoró velocidad");
                }
                break;
            case "Salto":
                mejoraAplicada = mejoras.mejorarSalto();
                if (mejoraAplicada) {
                    System.out.println("🦘 Jugador " + idJugador + " mejoró salto");
                }
                break;
            case "Daño":
                mejoraAplicada = mejoras.mejorarDaño();
                if (mejoraAplicada) {
                    System.out.println("⚔️ Jugador " + idJugador + " mejoró daño");
                }
                break;
            default:
                System.err.println("❌ Tipo de mejora desconocido: " + tipoMejora);
                break;
        }
    }

    @Override
    public void sincronizarActivacionPalanca(int idPalanca) {
        Object entidad = this.entidades.get(idPalanca);

        if (entidad instanceof Palanca) {
            Palanca palanca = (Palanca) entidad;
            palanca.activar();
            System.out.println("🔧 Palanca " + idPalanca + " sincronizada en servidor");
        } else {
            System.err.println("❌ No se encontró palanca con ID: " + idPalanca);
        }
    }

    public void despausar() { }
}
