package niveles;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.Array;
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
    private static final float ALTO_VIEWPORT_INICIAL = 15f;

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
    protected Body cuerpoPiso;
    private boolean juegoPausado = false;
    protected Screen pantallaRetorno;
    protected Jugador jugador1;
    protected Jugador jugador2;
    protected Jugador personaje;
    protected boolean friendlyFire = false;
    protected Map<Integer, Actor> entidades = new HashMap<>();
    protected HiloCliente hiloCliente;
    protected int cantEntidades = 0;
    protected int idJugadorActivo;

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

    public static MejoraTemporal getMejorasJugador1() {
        return mejorasJugador1;
    }

    public static MejoraTemporal getMejorasJugador2() {
        return mejorasJugador2;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
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
                        NivelBase.this.jugador1 = null;
                        NivelBase.this.escena.getActors().removeValue(jugador1, true);
                        NivelBase.this.jugador2 = null;
                        NivelBase.this.escena.getActors().removeValue(jugador2, true);
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
                    (b instanceof Jugador && (b instanceof Plataforma || b instanceof PlataformaMovil))) {

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

        // Solo crear barras si los jugadores ya existen
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

        this.hiloCliente = new HiloCliente(this);
        hiloCliente.start();
        hiloCliente.enviarMensaje("Conectar");
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

        this.jugador1.detener();
        this.jugador2.detener();


        if (this.jugador1.getVida() == 0 || this.jugador2.getVida() == 0) {
            int idJugadorMuerto = (this.jugador1.getVida() == 0) ? 1 : 2;
            this.cambiarEscena(new PantallaDeMuerte(this.juego, idJugadorMuerto));
        }



        if(idJugadorActivo == 1) {
            if (this.inputManager.getIsOPressed()) {
                jugador1.atacar(this.mundo, this.friendlyFire);
            } else {
                jugador1.resetearTeclaAtaque();
            }

            if (this.inputManager.getIsAPressed()) {
                jugador1.moverIzquierda();
            }

            if (this.inputManager.getIsDPressed()) {
                jugador1.moverDerecha();
            }
            if (this.inputManager.getIsWPressed()) {
                if (!this.jugador1.getEnElAire()) {
                    this.jugador1.saltar();
                }
            }
        } else {
            if (this.inputManager.getIsEPressed()) {
                jugador2.atacar(this.mundo, this.friendlyFire);
            } else {
                jugador2.resetearTeclaAtaque();
            }

            if (this.inputManager.getIsUpPressed()) {
                if (!this.jugador2.getEnElAire()) {
                    this.jugador2.saltar();
                }
            }

            if (this.inputManager.getIsLeftPressed()) {
                jugador2.moverIzquierda();
            }

            if (this.inputManager.getIsRightPressed()) {
                jugador2.moverDerecha();
            }
        }

        limpiarEntidades();

        super.render(delta);
        actualizarCamara();
        escena.getViewport().getCamera().combined.set(camaraBox2D.combined);
        mundo.step(1 / 60f, 6, 2);
    }

    private void limpiarEntidades() {
        Array<Actor> actores = escena.getActors();

        for (int i = actores.size - 1; i >= 0; i--) {
            Actor actor = actores.get(i);

            if (actor instanceof Enemigo) {
                Enemigo enemigo = (Enemigo) actor;

                if (enemigo.getMuerto()) {
                    enemigo.eliminar();
                    enemigo.dispose();
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);

        camaraBox2D.setToOrtho(
            false,
            viewport.getWorldWidth() * PIXELES_A_METROS,
            viewport.getWorldHeight() * PIXELES_A_METROS
        );

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

    @Override
    public void desconectar() {
        // Manejar desconexión del cliente
        if (this.hiloCliente != null) {
            this.hiloCliente.terminar();
        }
        volverAlMenu();
    }

    @Override
    public void procesarAccionesEntidades(String[] mensaje) {
        int idEntidad = Integer.parseInt(mensaje[1]);
        switch(mensaje[0]) {
            case "Jugador":
                procesarAccionesJugador(mensaje, idEntidad);
                break;
            case "Enemigo":
                procesarAccionesEnemigo(mensaje, idEntidad);
                break;

            case "Puerta":
                abrirPuerta(idEntidad);
                break;
            case "PlataformaMovil":
                moverPlataformaMovil(idEntidad, Integer.parseInt(mensaje[2]), Integer.parseInt(mensaje[3]));
                break;
            default: System.out.println("Mensaje desconocido"); break;
        }
    }

    @Override
    public void cambiarPantalla() {

    }

    @Override
    public void recogerLlave(int idLlave, int idJugador) {
    }

    @Override
    public void moverPlataformaMovil(int id, int posX, int posY) {
        Actor entidad = this.entidades.get(id);

        if (entidad != null) {
            // 2. Verificación y casting seguro
            if (entidad instanceof PlataformaMovil) {
                PlataformaMovil plataforma = (PlataformaMovil) entidad;

                plataforma.mover(posX, posY);
            }
        }
    }

    @Override
    public void abrirPuerta(int id) {
        Actor entidad = this.entidades.get(id);

        if (entidad != null) {
            // 2. Verificación y casting seguro
            if (entidad instanceof PuertaLlegada) {
                PuertaLlegada puerta = (PuertaLlegada) entidad;

                puerta.desbloquear();
            }
        }
    }

    public void procesarAccionesJugador(String[] mensaje, int idJugador) {
        switch(mensaje[1]) {
            case "ActualizarPosicion":
                actualizarPosicionJugador(idJugador, Integer.parseInt(mensaje[2]), Integer.parseInt(mensaje[3]));
                break;
            case "Dañar":
                dañarJugador(idJugador, Integer.parseInt(mensaje[2]));
                break;
            case "Matar":
                matarJugador(idJugador);
                break;
            default: System.out.println("Mensaje desconocido"); break;
        }
    }

    @Override
    public void actualizarPosicionJugador(int id, int posX, int posY) {
        if(id == 1) {
            this.jugador1.moverCuerpo(posX, posY);
        } else this.jugador2.moverCuerpo(posX, posY);
    }

    @Override
    public void matarJugador(int id) {
        if(id == 1) {
            this.jugador1.morir();
        } else this.jugador2.morir();
    }

    @Override
    public void dañarJugador(int idJugador, int nuevaVida) {
        if (idJugador == 1) {
            this.jugador1.recibirDaño(nuevaVida);
        } else {
            this.jugador2.recibirDaño(nuevaVida);
        }
    }

    public void procesarAccionesEnemigo(String[] mensaje, int idEnemigo) {
        switch(mensaje[1]) {
            case "ActualizarPosicion":
                actualizarPosicionEnemigo(idEnemigo, Integer.parseInt(mensaje[2]), Integer.parseInt(mensaje[3]));
                break;
            case "Desaparecer":
                desaparecerEnemigo(idEnemigo);
                break;
            default: System.out.println("Mensaje desconocido"); break;
        }
    }

    @Override
    public void actualizarPosicionEnemigo(int id, int posX, int posY) {
        Actor entidad = this.entidades.get(id);

        if (entidad != null) {
            // 2. Verificación y casting seguro
            if (entidad instanceof Enemigo) {
                Enemigo enemigo = (Enemigo) entidad;

                enemigo.mover(posX, posY);
            }
        }
    }

    @Override
    public void desaparecerEnemigo(int id) {
        Actor entidad = this.entidades.get(id);

        if (entidad != null) {
            // 2. Verificación y casting seguro
            if (entidad instanceof Enemigo) {
                Enemigo enemigo = (Enemigo) entidad;

                enemigo.eliminar();
                enemigo.dispose();
            }
        }
    }

    @Override
    public void empezarJuego() {
        cambiarEscena(new Nivel1(this.juego));
    }

    @Override
    public void conectar(int idJugador) {
        this.idJugadorActivo = idJugador;
    }

    @Override
    public void volverAlMenu() {
        cambiarEscena(new Menu(this.juego));
    }

    @Override
    public void terminarJuego() {
    }
}
