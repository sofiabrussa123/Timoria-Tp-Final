package io.github.timoria;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import menus.Menu;
import menus.PantallaGanaste;
import personajes.Enemigo;
import personajes.Personaje;

public class NivelBase extends EscenaBase {

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
    protected Stage escenaPausa;
    protected Skin skinPausa;


    public NivelBase(Principal juego, String fondo) {
        super(juego, fondo);

        this.mundo = new World(new Vector2(0, -25f), true);
        this.depuradorBox2D = new Box2DDebugRenderer();

        this.viewport = new ExtendViewport(anchoPantalla, altoPantalla);
        this.camaraBox2D = new OrthographicCamera();

        this.anchoViewport = anchoPantalla * PIXELES_A_METROS;
        this.altoViewport = altoPantalla * PIXELES_A_METROS;
        
        mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object a = contact.getFixtureA().getBody().getUserData();
                Object b = contact.getFixtureB().getBody().getUserData();

                if ((a instanceof personajes.Personaje && b instanceof entorno.PuertaLlegada) ||
                    (b instanceof personajes.Personaje && a instanceof entorno.PuertaLlegada)) {
                    juego.setScreen(new PantallaGanaste(juego));
                }

                if ((a instanceof Personaje && b instanceof Enemigo) || (b instanceof Personaje && a instanceof Enemigo)) {
                    Personaje jugador = (a instanceof Personaje) ? (Personaje) a : (Personaje) b;
                    Enemigo enemigo = (a instanceof Enemigo) ? (Enemigo) a : (Enemigo) b;

                    enemigo.aplicarDañoJugador(jugador);
                }
            }

            @Override public void endContact(Contact contact) {}
            @Override public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
        });

        crearMenuPausa();
    }
    
    private void crearMenuPausa() {
        skinPausa = new Skin(Gdx.files.internal("uiskin.json"));
        escenaPausa = new Stage(new ScreenViewport());

        TextButton btnSeguir = new TextButton("Seguir", skinPausa);
        TextButton btnMenu = new TextButton("Menú", skinPausa);

        btnSeguir.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                juegoPausado = false;
                Gdx.input.setInputProcessor(crearMultiplexer());
            }
        });

        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new Menu(juego));
            }
        });

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();
        tabla.add(btnSeguir).pad(10);
        tabla.row();
        tabla.add(btnMenu).pad(10);

        escenaPausa.addActor(tabla);
    }


    @Override
    public void render(float delta) {
    	if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
    	    juegoPausado = !juegoPausado;
    	    Gdx.input.setInputProcessor(juegoPausado ? escenaPausa : crearMultiplexer());
    	}

    	if (juegoPausado) {
    	    escenaPausa.act(delta);
    	    escenaPausa.draw();
    	} else {
    	    super.render(delta); // ← solo se actualiza escena si no está pausado
    	    mundo.step(1 / 60f, 6, 2);
    	    depuradorBox2D.render(mundo, escena.getCamera().combined.scl(1 / PIXELES_A_METROS));
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
    
    @Override
    public void dispose() {
    	escenaPausa.dispose();
    	skinPausa.dispose();
    }
}
