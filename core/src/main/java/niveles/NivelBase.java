package niveles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import interfaces.Menu;
import interfaces.Instrucciones;
import interfaces.PantallaGanaste;
import personajes.Enemigo;
import personajes.Personaje;
import niveles.entorno.PuertaLlegada;
import io.github.timoria.Principal;
import niveles.entorno.BotonActivador;
import niveles.entorno.Plataforma;

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
    protected Screen pantallaRetorno;

    protected Personaje personaje; // ← personaje seguido por la cámara
    protected SpriteBatch batch;   // ← batch local para renderizado

    public NivelBase(Principal juego, String fondo, Screen pantallaRetorno) {
        super(juego, fondo);

        this.mundo = new World(new Vector2(0, -25f), true);
        this.depuradorBox2D = new Box2DDebugRenderer();
        this.viewport = new ExtendViewport(anchoPantalla, altoPantalla);
        this.juego = juego;
        this.pantallaRetorno = pantallaRetorno;
        this.camaraBox2D = new OrthographicCamera();
        this.batch = new SpriteBatch(); // ← inicializado aquí

        this.anchoViewport = anchoPantalla * PIXELES_A_METROS;
        this.altoViewport = altoPantalla * PIXELES_A_METROS;

        mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object a = contact.getFixtureA().getBody().getUserData();
                Object b = contact.getFixtureB().getBody().getUserData();

                if ((a instanceof Personaje && b instanceof PuertaLlegada) ||
                    (b instanceof Personaje && a instanceof PuertaLlegada)) {

                    PuertaLlegada puerta = (a instanceof PuertaLlegada) ? (PuertaLlegada) a : (PuertaLlegada) b;
                    if (puerta.sePuedeCruzar()) {
                        juego.setScreen(new PantallaGanaste(juego));
                    }
                }

                if ((a instanceof Personaje && b instanceof Enemigo) ||
                    (b instanceof Personaje && a instanceof Enemigo)) {

                    Personaje jugador = (a instanceof Personaje) ? (Personaje) a : (Personaje) b;
                    Enemigo enemigo = (a instanceof Enemigo) ? (Enemigo) a : (Enemigo) b;

                    enemigo.aplicarDañoJugador(jugador);
                }

                if ((a instanceof Personaje && b instanceof BotonActivador) ||
                    (b instanceof Personaje && a instanceof BotonActivador)) {

                    BotonActivador boton = (a instanceof BotonActivador)
                        ? (BotonActivador) a
                        : (BotonActivador) b;

                    boton.activar();
                }

                if ((a instanceof Personaje && b instanceof Plataforma) ||
                    (b instanceof Personaje && a instanceof Plataforma)) {

                    Personaje personaje = (a instanceof Personaje) ? (Personaje) a : (Personaje) b;
                    personaje.setEnElAire(false);
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
        btnSeguir.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                juegoPausado = false;
                Gdx.input.setInputProcessor(crearMultiplexer());
            }
        });

        TextButton btnMenu = new TextButton("Menú", skinPausa);
        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new Menu(juego));
            }
        });

        // Boton con instrucciones basicas
        TextButton btnInstrucciones = new TextButton("Instrucciones", skinPausa);
        btnInstrucciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new Instrucciones(juego, NivelBase.this));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(btnSeguir).pad(10);
        table.row();
        table.add(btnMenu).pad(10);
        table.row();
        table.add(btnInstrucciones).pad(10);

        escenaPausa.addActor(table);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            juegoPausado = !juegoPausado;
            Gdx.input.setInputProcessor(juegoPausado ? escenaPausa : crearMultiplexer());
        }

        if (juegoPausado) {
            if (Gdx.input.getInputProcessor() != escenaPausa) {
                Gdx.input.setInputProcessor(escenaPausa);
            }

            escenaPausa.act(delta);
            escenaPausa.draw();
        } else {
            super.render(delta);
            actualizarCamara();
            batch.setProjectionMatrix(camaraBox2D.combined); // ← usa el batch propio
            batch.begin();
            batch.end();
            mundo.step(1 / 60f, 6, 2);

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
        batch.dispose(); // ← importante liberar memoria
    }
}
