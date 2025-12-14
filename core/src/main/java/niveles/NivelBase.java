package niveles;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import Red.HiloCliente;
import globales.EsceneManager;
import interfaces.GameController;
import interfaces.IdManager;
import interfaces.Menu;
import interfaces.MenuPausa;
import interfaces.PantallaDeMuerte;
import interfaces.PantallaGanaste;
import niveles.entorno.BarraInventario;
import niveles.entorno.BarraVida;
import niveles.entorno.LlaveActivadora;
import niveles.entorno.Palanca;
import niveles.entorno.Plataforma;
import niveles.entorno.PlataformaMovil;
import niveles.entorno.PuertaLlegada;
import personajes.Enemigo;
import personajes.Jugador;
import personajes.MejoraTemporal;

public abstract class NivelBase extends EscenaBase implements GameController{

    public static final float PIXELES_A_METROS = 1 / 100f;

    protected final int anchoPantalla = 800;
    protected final int altoPantalla = 800;

    protected static MejoraTemporal mejorasJugador1 = new MejoraTemporal();
    protected static MejoraTemporal mejorasJugador2 = new MejoraTemporal();

    protected World mundo;
    protected Box2DDebugRenderer depuradorBox2D;
    protected OrthographicCamera camaraBox2D;
    protected ExtendViewport viewport;
    protected float anchoViewport;
    protected float altoViewport;
    private boolean juegoPausado = false;
    protected Screen pantallaRetorno;
    protected Jugador jugador1;
    protected Jugador jugador2;
    protected Jugador personaje;
    protected Map<Integer, Actor> entidades = new HashMap<>();
    protected HiloCliente hiloCliente;
    protected int cantEntidades = 0;
    protected int idJugadorActivo;

    private Queue<Runnable> accionesBox2DPendientes = new Queue<Runnable>();

    // ✅ Control de estados previos para evitar envíos repetidos
    private String estadoAnteriorJ1 = "";
    private String estadoAnteriorJ2 = "";
    private boolean teclaWSaltoPrevioJ1 = false;
    private boolean teclaWSaltoPrevioJ2 = false;
    private boolean teclaUpSaltoPrevioJ2 = false;

    public NivelBase(Game juego, String fondo) {
        super(juego, fondo);

        this.mundo = new World(new Vector2(0f, -25f), true);
        this.depuradorBox2D = new Box2DDebugRenderer();
        this.viewport = new ExtendViewport(anchoPantalla, altoPantalla);
        this.camaraBox2D = new OrthographicCamera();
        this.anchoViewport = anchoPantalla * PIXELES_A_METROS;
        this.altoViewport = altoPantalla * PIXELES_A_METROS;

        this.establecerContactos();
    }

    public void encolarAccionBox2D(Runnable accion) {
        accionesBox2DPendientes.addLast(accion);
    }

    private void procesarAccionesBox2DPendientes() {
        while (accionesBox2DPendientes.size > 0) {
            try {
                accionesBox2DPendientes.removeFirst().run();
            } catch (Exception e) {
                System.err.println("❌ Error Box2D: " + e.getMessage());
            }
        }
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

    public void setPersonaje(Jugador personaje) {
        this.personaje = personaje;
    }

    protected void actualizarCamara() {
        if (personaje == null) return;

        Vector2 objetivo = personaje.getCuerpo().getPosition();
        camaraBox2D.position.x += (objetivo.x - camaraBox2D.position.x) * 0.1f;
        camaraBox2D.position.y += (objetivo.y - camaraBox2D.position.y) * 0.1f;

        camaraBox2D.update();
    }

    private void establecerContactos() {
        this.mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object a = contact.getFixtureA().getBody().getUserData();
                Object b = contact.getFixtureB().getBody().getUserData();

                if ((a instanceof Jugador && b instanceof PuertaLlegada) ||
                    (b instanceof Jugador && a instanceof PuertaLlegada)) {
                    PuertaLlegada puerta = (a instanceof PuertaLlegada) ? (PuertaLlegada) a : (PuertaLlegada) b;
                    if (puerta.sePuedeCruzar()) {
                        cambiarEscena(new PantallaGanaste(juego));
                    }
                }

                if (a instanceof Jugador && b instanceof LlaveActivadora ||
                    b instanceof Jugador && a instanceof LlaveActivadora) {
                    LlaveActivadora llave = a instanceof LlaveActivadora ? (LlaveActivadora) a : (LlaveActivadora) b;
                    Jugador personaje = a instanceof Jugador ? (Jugador) a : (Jugador) b;
                    llave.activarConJugador(personaje);
                }

                if (a instanceof Jugador && b instanceof Palanca ||
                    b instanceof Jugador && a instanceof Palanca) {
                    Palanca palanca = a instanceof Palanca ? (Palanca) a : (Palanca) b;
                    palanca.activar();
                }

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
        Gdx.input.setInputProcessor(this.inputManager);

        if (this.jugador1 != null) {
            BarraVida barra1 = new BarraVida(this.jugador1, true);
            this.jugador1.setBarraVida(barra1);
            this.escena.addActor(barra1);

            BarraInventario inventario1 = new BarraInventario(this.jugador1, true);
            this.jugador1.setBarraInventario(inventario1);
            this.escena.addActor(inventario1);
        }

        if (this.jugador2 != null) {
            BarraVida barra2 = new BarraVida(this.jugador2, false);
            this.jugador2.setBarraVida(barra2);
            this.escena.addActor(barra2);

            BarraInventario inventario2 = new BarraInventario(this.jugador2, false);
            this.jugador2.setBarraInventario(inventario2);
            this.escena.addActor(inventario2);
        }

        if (this.hiloCliente != null) {
            this.hiloCliente.setGameController(this);
        }
    }

    @Override
    public void render(float delta) {
        if (this.inputManager.getIsEscPressed() || this.inputManager.getIsPPressed()) {
            this.juegoPausado = !this.juegoPausado;
            if (this.juegoPausado) {
                EsceneManager.setEscenaActual(this);
                this.cambiarEscena(new MenuPausa(this.juego));
            }
        }

        // ✅ OPTIMIZADO: Aplicar controles SOLO cuando cambian
        aplicarControlesOptimizados();

        // Verificar muerte
        if (this.jugador1 != null && this.jugador1.getVida() == 0) {
            this.cambiarEscena(new PantallaDeMuerte(this.juego, 1, hiloCliente));
        }

        if (this.jugador2 != null && this.jugador2.getVida() == 0) {
            this.cambiarEscena(new PantallaDeMuerte(this.juego, 2, hiloCliente));
        }

        super.render(delta);
        actualizarCamara();
        escena.getViewport().getCamera().combined.set(camaraBox2D.combined);

        mundo.step(1 / 60f, 6, 2);
        procesarAccionesBox2DPendientes();
    }

    // ✅ Controles optimizados - Solo enviar cuando CAMBIA el estado
    private void aplicarControlesOptimizados() {
        if (idJugadorActivo == 1 && this.jugador1 != null) {
            String estadoActual = "";

            if (this.inputManager.getIsAPressed()) {
                estadoActual = "MoverIzquierda";
                if (!estadoAnteriorJ1.equals(estadoActual)) {
                    jugador1.moverIzquierda(); // Predicción local
                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:1:MoverIzquierda");
                    }
                }
            } else if (this.inputManager.getIsDPressed()) {
                estadoActual = "MoverDerecha";
                if (!estadoAnteriorJ1.equals(estadoActual)) {
                    jugador1.moverDerecha();
                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:1:MoverDerecha");
                    }
                }
            } else {
                estadoActual = "Detener";
                if (!estadoAnteriorJ1.equals(estadoActual)) {
                    jugador1.detener();
                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:1:Detener");
                    }
                }
            }

            estadoAnteriorJ1 = estadoActual;

            // Salto - solo una vez por presión
            if (this.inputManager.getIsWPressed()) {
                if (!teclaWSaltoPrevioJ1 && !this.jugador1.getEnElAire()) {
                    this.jugador1.saltar();
                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:1:Saltar");
                    }
                }
                teclaWSaltoPrevioJ1 = true;
            } else {
                teclaWSaltoPrevioJ1 = false;
            }

            // Ataque - solo una vez por presión
            if (this.inputManager.getIsOPressed()) {
                if (hiloCliente != null) {
                    hiloCliente.enviarMensaje("Input:1:Atacar");
                }
            } else {
                jugador1.resetearTeclaAtaque();
            }

        } else if (idJugadorActivo == 2 && this.jugador2 != null) {
            String estadoActual = "";

            if (this.inputManager.getIsLeftPressed()) {
                estadoActual = "MoverIzquierda";
                if (!estadoAnteriorJ2.equals(estadoActual)) {
                    jugador2.moverIzquierda();
                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:2:MoverIzquierda");
                    }
                }
            } else if (this.inputManager.getIsRightPressed()) {
                estadoActual = "MoverDerecha";
                if (!estadoAnteriorJ2.equals(estadoActual)) {
                    jugador2.moverDerecha();
                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:2:MoverDerecha");
                    }
                }
            } else {
                estadoActual = "Detener";
                if (!estadoAnteriorJ2.equals(estadoActual)) {
                    jugador2.detener();
                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:2:Detener");
                    }
                }
            }

            estadoAnteriorJ2 = estadoActual;

            if (this.inputManager.getIsUpPressed()) {
                if (!teclaUpSaltoPrevioJ2 && !this.jugador2.getEnElAire()) {
                    this.jugador2.saltar();
                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:2:Saltar");
                    }
                }
                teclaUpSaltoPrevioJ2 = true;
            } else {
                teclaUpSaltoPrevioJ2 = false;
            }

            if (this.inputManager.getIsEPressed()) {
                if (hiloCliente != null) {
                    hiloCliente.enviarMensaje("Input:2:Atacar");
                }
            } else {
                jugador2.resetearTeclaAtaque();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camaraBox2D.setToOrtho(false, viewport.getWorldWidth() * PIXELES_A_METROS, viewport.getWorldHeight() * PIXELES_A_METROS);
        camaraBox2D.update();
    }

    public void draw(float delta) {
        super.render(delta);
        this.actualizarCamara();
        this.escena.getViewport().getCamera().combined.set(this.camaraBox2D.combined);
    }

    public void despausar() {
        this.juegoPausado = false;
        this.inputManager.resetPauseKeys();
        Gdx.input.setInputProcessor(this.inputManager);
    }

    protected void añadirElemento(IdManager elemento) {
        this.escena.addActor((Actor)elemento);
        entidades.put(elemento.getId(), (Actor)elemento);
    }

    protected int asignarIdEntidad() {
        this.cantEntidades++;
        return this.cantEntidades;
    }

    public void setHiloCliente(HiloCliente hiloCliente) {
        this.hiloCliente = hiloCliente;
    }

    // =========================================================================
    // GameController - Recepción de Estados
    // =========================================================================

    @Override
    public void procesarAccionesEntidades(String[] mensaje) {
        // No usado
    }

    @Override
    public void cambiarPantalla() {
        // No usado
    }

    @Override
    public void recogerItem(int idLlave, int idJugador) {
        Actor entidad = this.entidades.get(idLlave);
        if (entidad instanceof LlaveActivadora) {
            LlaveActivadora llave = (LlaveActivadora) entidad;
            Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;
            if (jugador != null) {
                llave.activarConJugador(jugador);
            }
        }
    }

    @Override
    public void moverPlataformaMovil(int id, float posX, float posY) {
        final Actor entidad = this.entidades.get(id);
        if (entidad instanceof PlataformaMovil) {
            encolarAccionBox2D(new Runnable() {
                @Override
                public void run() {
                    ((PlataformaMovil) entidad).moverDesdeServidor(posX, posY);
                }
            });
        }
    }

    @Override
    public void abrirPuerta(int id) {
        Actor entidad = this.entidades.get(id);
        if (entidad instanceof PuertaLlegada) {
            ((PuertaLlegada) entidad).desbloquear();
        }
    }

    @Override
    public void procesarAccionesJugador(String[] mensaje, int idJugador) {
        // Manejado por casos específicos
    }

    @Override
    public void actualizarPosicionJugador(int id, float posX, float posY, boolean mirandoDerecha) {
        // ✅ NUEVO ENFOQUE: Actualizar AMBOS jugadores, pero con interpolación diferente

        final Jugador jugador = (id == 1) ? this.jugador1 : this.jugador2;
        if (jugador == null) return;

        encolarAccionBox2D(new Runnable() {
            @Override
            public void run() {
                if (id == idJugadorActivo) {
                    // ✅ MI jugador: Interpolación suave (corrección ligera)
                    jugador.corregirPosicionSuave(posX, posY, mirandoDerecha);
                } else {
                    // ✅ OTRO jugador: Actualización directa
                    jugador.actualizarDesdeServidor(posX, posY, mirandoDerecha);
                }
            }
        });
    }

    public void renderFondoPausado(float delta) {
        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar el escenario tal como está (sin actualizar)
        escena.getViewport().apply();
        escena.getViewport().getCamera().combined.set(camaraBox2D.combined);
        escena.draw();
    }

    @Override
    public void actualizarPosicionEnemigo(int id, float posX, float posY) {
        final Actor entidad = this.entidades.get(id);
        if (entidad instanceof Enemigo) {
            encolarAccionBox2D(new Runnable() {
                @Override
                public void run() {
                    Enemigo enemigo = (Enemigo) entidad;
                    if (enemigo.getCuerpo() != null) {
                        enemigo.getCuerpo().setTransform(posX, posY, 0);
                    }
                }
            });
        }
    }

    @Override
    public void servidorDesconectado() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                volverAlMenu();
            }
        });
    }

    @Override
    public void matarJugador(int id) {
        Jugador jugador = (id == 1) ? this.jugador1 : this.jugador2;
        if (jugador != null) {
            jugador.morir();
        }
    }

    @Override
    public void dañarJugador(int idJugador, int nuevaVida) {
        Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;
        if (jugador != null) {
            int vidaAnterior = jugador.getVida();
            jugador.setVida(nuevaVida);

            // ✅ REPRODUCIR SONIDO si es MI jugador Y recibió daño (no curación)
            if (idJugador == idJugadorActivo && nuevaVida < vidaAnterior) {
                jugador.reproducirSonidoDaño();
            }
        }
    }

    @Override
    public void procesarAccionesEnemigo(String[] mensaje, int idEnemigo) {
        // Manejado
    }

    @Override
    public void desaparecerEnemigo(int id) {
        Actor entidad = this.entidades.get(id);
        if (entidad instanceof Enemigo) {
            Enemigo enemigo = (Enemigo) entidad;
            enemigo.eliminar();
            enemigo.dispose();
        }
    }

    @Override
    public void activarPalanca(int idPalanca) {
        Actor entidad = this.entidades.get(idPalanca);
        if (entidad instanceof Palanca) {
            ((Palanca) entidad).activar();
        }
    }

    @Override
    public void aplicarMejora(int idJugador, String tipoMejora) {
        Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;
        MejoraTemporal mejoras = (idJugador == 1) ? mejorasJugador1 : mejorasJugador2;

        if (jugador == null || mejoras == null) return;

        switch(tipoMejora) {
            case "Vida":
                mejoras.mejorarVida();
                jugador.actualizarVidaConMejoras();
                break;
            case "Velocidad":
                mejoras.mejorarVelocidad();
                break;
            case "Salto":
                mejoras.mejorarSalto();
                break;
            case "Daño":
                mejoras.mejorarDaño();
                break;
        }
    }

    @Override
    public void mostrarAtaqueJugador(int idJugador) {
        Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;
        if (jugador != null) {
            // Mostrar animación de ataque visualmente
            jugador.setFrameAnimacion(0, jugador.getMirandoDerecha());
        }
    }

    @Override
    public void empezarJuego() {
        // No usado
    }

    @Override
    public void conectar(int idJugador) {
        this.idJugadorActivo = idJugador;

        if (jugador1 != null && jugador2 != null) {
            jugador1.setEsMiJugador(idJugador == 1);
            jugador2.setEsMiJugador(idJugador == 2);
        }
    }

    @Override
    public void volverAlMenu() {
        if (this.hiloCliente != null) {
            hiloCliente.enviarMensaje("VolverAlMenu");
        }
        cambiarEscena(new Menu(this.juego));
    }

    @Override
    public void terminarJuego() {
        volverAlMenu();
    }

    @Override
    public void desconectar() {

    }

    protected void setFriendlyFire(boolean b) {
    }
}
