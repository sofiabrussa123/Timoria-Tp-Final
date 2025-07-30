package io.github.timoria;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;

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

    public NivelBase(Principal juego, String fondo) {
        super(juego, fondo);

        this.mundo = new World(new Vector2(0, -25f), true);
        this.depuradorBox2D = new Box2DDebugRenderer();

        this.viewport = new ExtendViewport(anchoPantalla, altoPantalla);
        this.camaraBox2D = new OrthographicCamera();

        this.anchoViewport = anchoPantalla * PIXELES_A_METROS;
        this.altoViewport = altoPantalla * PIXELES_A_METROS;

        crearPiso();

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        mundo.step(1 / 60f, 6, 2);

        depuradorBox2D.render(mundo, escena.getCamera().combined.scl(1 / PIXELES_A_METROS));
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
        crearPiso();
    }

    private void crearPiso() {
        if (cuerpoPiso != null) {
            mundo.destroyBody(cuerpoPiso);
        }

        BodyDef definicionCuerpo = new BodyDef();
        definicionCuerpo.type = BodyDef.BodyType.StaticBody;
        definicionCuerpo.position.set(anchoViewport / 2, 0.5f);

        cuerpoPiso = mundo.createBody(definicionCuerpo);

        PolygonShape forma = new PolygonShape();
        forma.setAsBox(anchoViewport / 2, 0.5f); // Mitad del ancho y alto

        FixtureDef definicionFixture = new FixtureDef();
        definicionFixture.shape = forma;
        definicionFixture.density = 1.0f;

        cuerpoPiso.createFixture(definicionFixture);
        forma.dispose();

        actualizarCamara();
    }

    private void actualizarCamara() {
        camaraBox2D.setToOrtho(false, anchoViewport, altoViewport);
        camaraBox2D.position.set(anchoViewport / 2, altoViewport / 2, 0);
        camaraBox2D.update();
    }
}
