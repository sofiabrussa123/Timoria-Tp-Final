package niveles;

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

import Red.HiloServidor;
import interfaces.GameController;
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
import personajes.accesorios.MejoraTemporal;

public abstract class NivelBase extends EscenaBase implements GameController{

    public static final float PIXELES_A_METROS = 1 / 100f;
    private static final float ALTO_VIEWPORT_INICIAL = 15f;

    protected final int anchoPantalla = 800;
    protected final int altoPantalla = 800;

    // Mejoras estáticas compartidas entre niveles
    protected static MejoraTemporal mejorasJugador1 = new MejoraTemporal();
    protected static MejoraTemporal mejorasJugador2 = new MejoraTemporal();

    protected World mundo;
    protected Box2DDebugRenderer depuradorBox2D;
    protected OrthographicCamera camaraBox2D;
    protected ExtendViewport viewport;
    protected float anchoViewport;
    protected float altoViewport;
    protected Body cuerpoPiso;
    protected Screen pantallaRetorno;
    protected Jugador jugador1;
    protected Jugador jugador2;
    protected Jugador personaje; // Personaje seguido por la cámara
    protected HiloServidor hiloServidor;
    protected boolean friendlyFire = false;

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

    // Getters para mejoras estáticas
    public static MejoraTemporal getMejorasJugador1() {
        return mejorasJugador1;
    }

    public static MejoraTemporal getMejorasJugador2() {
        return mejorasJugador2;
    }

    // Getters y setters
    public Jugador getJugador1() {
        return this.jugador1;
    }

    public Jugador getJugador2() {
        return this.jugador2;
    }

    public void setPersonaje(Jugador personaje) {
        this.personaje = personaje;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
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

                // Lógica puerta
                if ((a instanceof Jugador && b instanceof PuertaLlegada) ||
                    (b instanceof Jugador && a instanceof PuertaLlegada)) {

                    PuertaLlegada puerta = (a instanceof PuertaLlegada) ? (PuertaLlegada) a : (PuertaLlegada) b;
                    if (puerta.sePuedeCruzar()) {
                        NivelBase.this.jugador1 = null;
                        NivelBase.this.escena.getActors().removeValue(jugador1, true);
                        NivelBase.this.jugador2 = null;
                        NivelBase.this.escena.getActors().removeValue(jugador2, true);
                        cambiarEscena(new PantallaGanaste(juego));
                        
                        if (hiloServidor != null) {
                            hiloServidor.enviarMensajeATodos("CambiarPantalla:PantallaGanaste");
                        }
                    }
                }

                // Lógica activar la llave
                if (a instanceof Jugador && b instanceof LlaveActivadora ||
                    b instanceof Jugador && a instanceof LlaveActivadora) {
                    LlaveActivadora llave = a instanceof LlaveActivadora ? (LlaveActivadora) a : (LlaveActivadora) b;
                    Jugador personaje = a instanceof Jugador ? (Jugador) a : (Jugador) b;
                    llave.activarConJugador(personaje);
                }

                // Lógica jugador activar palanca
                if (a instanceof Jugador && b instanceof Palanca ||
                    b instanceof Jugador && a instanceof Palanca) {
                    Palanca palanca = a instanceof Palanca ? (Palanca) a : (Palanca) b;
                    palanca.activar();
                }

                // Lógica jugador apoyarse en plataforma
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
        // Inicializar servidor si no existe
        if (hiloServidor == null) {
            hiloServidor = new HiloServidor(this);
        }

        Gdx.input.setInputProcessor(this.inputManager);

        // Configurar barras de vida
        BarraVida barra1 = new BarraVida(this.jugador1, true);
        BarraVida barra2 = new BarraVida(this.jugador2, false);
        this.jugador1.setBarraVida(barra1);
        this.jugador2.setBarraVida(barra2);
        this.escena.addActor(barra1);
        this.escena.addActor(barra2);

        // Configurar inventarios
        if (this.jugador1 != null) {
            BarraInventario inventario1 = new BarraInventario(this.jugador1, true);
            this.jugador1.setBarraInventario(inventario1);
            this.escena.addActor(inventario1);
        }

        if (this.jugador2 != null) {
            BarraInventario inventario2 = new BarraInventario(this.jugador2, false);
            this.jugador2.setBarraInventario(inventario2);
            this.escena.addActor(inventario2);
        }
        
        this.hiloServidor.start();
    }

    @Override
    public void render(float delta) {

        this.jugador1.detener();
        this.jugador2.detener();

        // Verificar muerte de jugadores
        if (this.jugador1.getVida() == 0) {
            this.cambiarEscena(new PantallaDeMuerte(this.juego, this.jugador1, this, this.hiloServidor));
        }

        if (this.jugador2.getVida() == 0) {
            this.cambiarEscena(new PantallaDeMuerte(this.juego, this.jugador2, this, this.hiloServidor));
        }

        // Controles Jugador 1
        if (this.inputManager.getIsWPressed()) {
            if (!this.jugador1.getEnElAire()) {
                this.jugador1.saltar();
            }
        }

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

        // Controles Jugador 2
        if (this.inputManager.getIsUpPressed()) {
            if (!this.jugador2.getEnElAire()) {
                this.jugador2.saltar();
            }
        }

        if (this.inputManager.getIsEPressed()) {
            jugador2.atacar(this.mundo, this.friendlyFire);
        } else {
            jugador2.resetearTeclaAtaque();
        }

        if (this.inputManager.getIsLeftPressed()) {
            jugador2.moverIzquierda();
        }

        if (this.inputManager.getIsRightPressed()) {
            jugador2.moverDerecha();
        }

        limpiarEntidades();

        super.render(delta);
        actualizarCamara();
        escena.getViewport().getCamera().combined.set(camaraBox2D.combined);
        mundo.step(1 / 60f, 6, 2);
    }

    private void limpiarEntidades() {
        Array<Actor> actores = escena.getActors();

        // Iteramos en reversa para eliminar de forma segura del Array
        for (int i = actores.size - 1; i >= 0; i--) {
            Actor actor = actores.get(i);

            if (actor instanceof Enemigo) {
                Enemigo enemigo = (Enemigo) actor;

                if (enemigo.getMuerto()) {
                    enemigo.eliminar();
                    enemigo.dispose();
                    
                    if (hiloServidor != null) {
                        hiloServidor.enviarMensajeATodos("Enemigo:" + enemigo.getID()+":Desaparecer");
                    }
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
    
    public void moverJugador(int id, boolean derecha) {
    	if(id == 1) {
    		if(derecha) this.jugador1.moverIzquierda();
    		else this.jugador1.moverDerecha();
    	} else if(derecha) this.jugador2.moverDerecha();
		else this.jugador2.moverIzquierda();
    }
    
    public void saltar(int idJugador) {
    	if(idJugador == 1) this.jugador1.saltar();
    	else this.jugador2.saltar();
    }
    
    public void atacar(int idJugador) {
    	if(idJugador == 1) this.jugador1.atacar(this.mundo, this.friendlyFire);
    	else this.jugador2.atacar(this.mundo, this.friendlyFire);
    }
}
