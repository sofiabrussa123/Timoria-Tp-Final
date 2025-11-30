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
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import globales.EsceneManager;
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
import personajes.Personaje;

public abstract class NivelBase extends EscenaBase {

    public static final float PIXELES_A_METROS = 1 / 100f;
    private static final float ALTO_VIEWPORT_INICIAL = 15f;
    protected final int anchoPantalla = 800;
    protected final int altoPantalla = 800;

    protected World mundo = new World(new Vector2(0.0F, -25.0F), true);
    protected Box2DDebugRenderer depuradorBox2D = new Box2DDebugRenderer();
    protected OrthographicCamera camaraBox2D = new OrthographicCamera();
    protected ExtendViewport viewport = new ExtendViewport(800.0F, 800.0F);
    protected float anchoViewport = 8.0F;
    protected float altoViewport = 8.0F;
    protected Body cuerpoPiso;
    private boolean juegoPausado = false;
    protected Screen pantallaRetorno;
    protected Personaje jugador1;
    protected Personaje jugador2;

    protected Personaje personaje; // ← personaje seguido por la cámara

    public NivelBase(Game juego, String fondo) {

        super(juego, fondo);
        this.mundo = new World(new Vector2(0, -25f), true);
        this.depuradorBox2D = new Box2DDebugRenderer();
        this.viewport = new ExtendViewport(anchoPantalla, altoPantalla);
        this.camaraBox2D = new OrthographicCamera();
        this.anchoViewport = anchoPantalla * PIXELES_A_METROS;
        this.altoViewport = altoPantalla * PIXELES_A_METROS;

        //Todos los tipos de contacto
        this.establecerContactos();
    }

    public void setPersonaje(Personaje personaje) {
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

                //Lógica puerta
                if ((a instanceof Personaje && b instanceof PuertaLlegada) ||
                    (b instanceof Personaje && a instanceof PuertaLlegada)) {

                    PuertaLlegada puerta = (a instanceof PuertaLlegada) ? (PuertaLlegada) a : (PuertaLlegada) b;
                    if (puerta.sePuedeCruzar()) {

                    	NivelBase.this.jugador1 = null;
                    	NivelBase.this.escena.getActors().removeValue(jugador1, true);
                    	NivelBase.this.jugador2 = null;
                    	NivelBase.this.escena.getActors().removeValue(jugador2, true);
                        cambiarEscena(new PantallaGanaste(juego));
                    }
                }

                //Lógica enemigo daña al jugador
                if ((a instanceof Personaje && b instanceof Enemigo) ||
                    (b instanceof Personaje && a instanceof Enemigo)) {

                    Personaje jugadorColisionado = (a instanceof Personaje) ? (Personaje) a : (Personaje) b;
                    Enemigo enemigoColisionado = (a instanceof Enemigo) ? (Enemigo) a : (Enemigo) b;

                    if(enemigoColisionado.getPuedeAtacar()) {
                    	enemigoColisionado.aplicarDañoJugador(jugadorColisionado);
                    	enemigoColisionado.iniciarCooldown();

                        if(jugadorColisionado.getVida() == 0) {
                        	cambiarEscena(new PantallaDeMuerte(juego));
                        }
                    }
                }

                //Lógica activar el botón
                if (a instanceof Personaje && b instanceof LlaveActivadora || b instanceof Personaje && a instanceof LlaveActivadora) {
                    LlaveActivadora llave = a instanceof LlaveActivadora ? (LlaveActivadora)a : (LlaveActivadora)b;
                    Personaje personaje = a instanceof Personaje ? (Personaje)a : (Personaje)b;
                    llave.activarConJugador(personaje);
                }
                
                //Logica jugador activar palanca
                if (a instanceof Personaje && b instanceof Palanca || b instanceof Personaje && a instanceof Palanca) {
                    Palanca palanca = a instanceof Palanca ? (Palanca)a : (Palanca)b;
                    palanca.activar();
                }

                //Lógica jugador apoyarse en plataforma
                if ((a instanceof Personaje && (b instanceof Plataforma || b instanceof PlataformaMovil)) ||
                    (b instanceof Personaje && (b instanceof Plataforma || b instanceof PlataformaMovil))) {

                    Personaje personaje = (a instanceof Personaje) ? (Personaje) a : (Personaje) b;
                    personaje.setEnElAire(false);
                }
            }

            @Override
            public void endContact(Contact contact) {}
            @Override public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
    }

    public Personaje getJugador1() {
    	return this.jugador1;
    }

    public Personaje getJugador2() {
    	return this.jugador2;
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(this.inputManager);
        BarraVida barra1 = new BarraVida(this.jugador1, true);
        BarraVida barra2 = new BarraVida(this.jugador2, false);
        this.jugador1.setBarraVida(barra1);
        this.jugador2.setBarraVida(barra2);
        this.escena.addActor(barra1);
        this.escena.addActor(barra2);
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

    }

    @Override
    public void render(float delta) {
    	//Cambiar al menú de pausa si es aprieta escape o p
        if (this.inputManager.getIsEscPressed() || this.inputManager.getIsPPressed()) {
            this.juegoPausado = !this.juegoPausado;
            if(this.juegoPausado) {
            	EsceneManager.setEscenaActual(this);
            	this.cambiarEscena(new MenuPausa(this.juego));
            }
        }

        this.jugador1.detener();
        this.jugador2.detener();

        if(this.jugador1.getVida() == 0 || this.jugador2.getVida() == 0) {
        	this.cambiarEscena(new PantallaDeMuerte(this.juego));
        }

        if(this.inputManager.getIsWPressed()) {
        	if(!this.jugador1.getEnElAire()) {
        		this.jugador1.saltar();
        	}
        }

        if(this.inputManager.getIsAPressed()) {
        	jugador1.moverIzquierda();
        }

        if(this.inputManager.getIsDPressed()) {
        	jugador1.moverDerecha();
        }

        if(this.inputManager.getIsUpPressed()) {
        	if(!this.jugador2.getEnElAire()) {
        		this.jugador2.saltar();
        	}
        }

        if(this.inputManager.getIsLeftPressed()) {
        	jugador2.moverIzquierda();
        }

        if(this.inputManager.getIsRightPressed()) {
        	jugador2.moverDerecha();
        }

	    super.render(delta);
	    actualizarCamara();
	    escena.getViewport().getCamera().combined.set(camaraBox2D.combined);
	    mundo.step(1 / 60f, 6, 2);
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
}
