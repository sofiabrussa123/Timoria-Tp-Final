package niveles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import io.github.timoria.Principal;
import niveles.entorno.BotonActivador;
import niveles.entorno.Plataforma;
import niveles.entorno.PuertaLlegada;
import personajes.Enemigo;
import personajes.Personaje;
import personajes.controladores.InputPersonaje;

public abstract class NivelBase extends EscenaBase {

    public static final float PIXELES_A_METROS = 1 / 100f;
    private static final float ALTO_VIEWPORT_INICIAL = 15f;
    protected final int anchoPantalla = 800;
    protected final int altoPantalla = 800;

    protected World mundo;
    protected Box2DDebugRenderer depuradorBox2D;
    protected OrthographicCamera camaraBox2D;
    protected ExtendViewport viewport;
    protected float anchoViewport;
    protected float altoViewport;
    protected Body cuerpoPiso;
    protected boolean juegoPausado = false;
    protected Screen pantallaRetorno;
    protected Personaje jugador;

    protected Personaje personaje; // ← personaje seguido por la cámara
    protected SpriteBatch batch;   // ← batch local para renderizado

    public NivelBase(Principal juego, String fondo) {
    	
        super(juego, fondo);
        this.mundo = new World(new Vector2(0, -25f), true);
        this.depuradorBox2D = new Box2DDebugRenderer();
        this.viewport = new ExtendViewport(anchoPantalla, altoPantalla);
        this.juego = juego;
        this.camaraBox2D = new OrthographicCamera();
        this.batch = new SpriteBatch(); // ← inicializado aquí
        this.anchoViewport = anchoPantalla * PIXELES_A_METROS;
        this.altoViewport = altoPantalla * PIXELES_A_METROS;

        //Todos los tipos de contacto
        mundo.setContactListener(new ContactListener() {
        	
            @Override
            public void beginContact(Contact contact) {
            
                Object a = contact.getFixtureA().getBody().getUserData();
                Object b = contact.getFixtureB().getBody().getUserData();

                //Lógica puerta
                if ((a instanceof Personaje && b instanceof PuertaLlegada) ||
                    (b instanceof Personaje && a instanceof PuertaLlegada)) {

                    PuertaLlegada puerta = (a instanceof PuertaLlegada) ? (PuertaLlegada) a : (PuertaLlegada) b;
                    if (puerta.sePuedeCruzar()) {
                    	
                        cambiarEscena(new PantallaGanaste(juego));
                    }
                }

                //Lógica enemigo daña al jugador
                if ((a instanceof Personaje && b instanceof Enemigo) ||
                    (b instanceof Personaje && a instanceof Enemigo)) {

                    Personaje jugadorColisionado = (a instanceof Personaje) ? (Personaje) a : (Personaje) b;
                    Enemigo enemigoColisionado = (a instanceof Enemigo) ? (Enemigo) a : (Enemigo) b;

                    enemigoColisionado.aplicarDañoJugador(jugadorColisionado);
                    
                    if(jugadorColisionado.getVida() == 0) {
                    	cambiarEscena(new PantallaDeMuerte(juego));
                    }
                }

                //Lógica activar el botón
                if ((a instanceof Personaje && b instanceof BotonActivador) ||
                    (b instanceof Personaje && a instanceof BotonActivador)) {

                    BotonActivador boton = (a instanceof BotonActivador)
                        ? (BotonActivador) a
                        : (BotonActivador) b;

                    boton.activar();
                }
                /* Tiene pinta que no hace nada, pero lo dejo por si acaso
                if ((a instanceof Personaje && b instanceof Plataforma) ||
                    (b instanceof Personaje && a instanceof Plataforma)) {

                    Personaje personaje = (a instanceof Personaje) ? (Personaje) a : (Personaje) b;
                    personaje.setEnElAire(false);
                }
                */
            }

            @Override public void endContact(Contact contact) {}
            @Override public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
        });
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

    @Override
    public void render(float delta) {
    	//Cambiar al menú de pausa si es aprieta escape o p
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            juegoPausado = !juegoPausado;
            if(juegoPausado) {
            	EsceneManager.setEscenaActual(this);
            	cambiarEscena(new MenuPausa(juego));
            } 
        }

	    super.render(delta);
	    actualizarCamara();
	    batch.setProjectionMatrix(camaraBox2D.combined); // ← usa el batch propio
	    batch.begin();
	    batch.end();
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
        actualizarCamara();
        batch.setProjectionMatrix(camaraBox2D.combined);
        batch.begin();
	    batch.end();
    }
    
    //Añadir los inputs de jugador y escena
    protected InputMultiplexer crearMultiplexer() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputPersonaje(this.jugador));
        multiplexer.addProcessor(escena);
        return multiplexer;
    }
}
