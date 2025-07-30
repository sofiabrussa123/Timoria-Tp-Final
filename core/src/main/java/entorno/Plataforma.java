package personajes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.timoria.NivelBase;
import movimientos.MovimientoBase;

public class Personaje extends Actor {

    private World mundo;
    private String nombre;
    private Animaciones animaciones = new Animaciones();
    private MovimientoBase movimientoActual;

    private Body cuerpo;
    private boolean mirandoDerecha = true;

    private Animation<TextureRegion> animacionActual;
    private float tiempoEstado = 0;

    private float velocidadSaltoY = 0;
    private boolean enElAire = false;

    private final float gravedad = 800;
    private final float impulsoSalto = 200;
    private final float pisoY = 100;
    
    private boolean moverIzquierda = false;
    private boolean moverDerecha = false;
    private boolean saltar = false;

    public Personaje(World mundo, String nombre, float anchoPantalla) {
        this.mundo = mundo;
        this.nombre = nombre;
        this.animacionActual = animaciones.getAnimacionQuieto();

        BodyDef defCuerpo = new BodyDef();
        defCuerpo.type = BodyDef.BodyType.DynamicBody;
        defCuerpo.position.set(anchoPantalla / 2, 5);
        defCuerpo.fixedRotation = true;

        this.cuerpo = mundo.createBody(defCuerpo);

        TextureRegion primerFrame = animaciones.getAnimacionQuieto().getKeyFrame(0);
        float anchoSprite = primerFrame.getRegionWidth();
        float altoSprite = primerFrame.getRegionHeight();

        float anchoHitbox = anchoSprite * NivelBase.PIXELES_A_METROS;
        float altoHitbox = altoSprite * NivelBase.PIXELES_A_METROS;

        PolygonShape forma = new PolygonShape();
        forma.setAsBox(anchoHitbox / 2, altoHitbox / 2);

        FixtureDef defFixture = new FixtureDef();
        defFixture.shape = forma;
        defFixture.density = 1.0f;
        defFixture.friction = 0.4f;
        defFixture.restitution = 0.1f;

        cuerpo.createFixture(defFixture);
        forma.dispose();

        setSize(anchoSprite, altoSprite);
        cuerpo.setUserData(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frameActual = animacionActual.getKeyFrame(tiempoEstado, true);
        Color color = getColor();
        batch.setColor(color);

        float anchoFrame = frameActual.getRegionWidth();
        float altoFrame = frameActual.getRegionHeight();

        float posXPx = cuerpo.getPosition().x / NivelBase.PIXELES_A_METROS;
        float posYPx = cuerpo.getPosition().y / NivelBase.PIXELES_A_METROS;

        if (!mirandoDerecha) {
            frameActual.flip(true, false);
        }

        batch.draw(
            frameActual,
            posXPx - anchoFrame / 2,
            posYPx - altoFrame / 2,
            anchoFrame,
            altoFrame
        );

        if (!mirandoDerecha) {
            frameActual.flip(true, false);
        }

        batch.setColor(Color.WHITE);
    }

    @Override
    public void act(float delta) {
        tiempoEstado += delta;

        if (movimientoActual == null || movimientoActual.estaCompletado()) {
            float velocidadX = 0;

            if (moverIzquierda) {
                velocidadX = -5f;
                mirandoDerecha = false;
                animacionActual = animaciones.getAnimacionCorrer();
            } else if (moverDerecha) {
                velocidadX = 5f;
                mirandoDerecha = true;
                animacionActual = animaciones.getAnimacionCorrer();
            } else {
                animacionActual = animaciones.getAnimacionQuieto();
            }

            cuerpo.setLinearVelocity(velocidadX, cuerpo.getLinearVelocity().y);
        }

        setPosition(
            (cuerpo.getPosition().x / NivelBase.PIXELES_A_METROS) - getWidth() / 2,
            (cuerpo.getPosition().y / NivelBase.PIXELES_A_METROS) - getHeight() / 2
        );

        if (saltar && !enElAire) {
            cuerpo.setLinearVelocity(cuerpo.getLinearVelocity().x, 10f);
            enElAire = true;
            saltar = false;
        }

        if (enElAire) {
            velocidadSaltoY += gravedad * delta;
            float nuevaY = getY() - velocidadSaltoY * delta;

            if (nuevaY <= pisoY) {
                nuevaY = pisoY;
                velocidadSaltoY = 0;
                enElAire = false;
            }

            setY(nuevaY);
        }
    }

    public Body getCuerpo() {
        return cuerpo;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean estaMirandoDerecha() {
        return mirandoDerecha;
    }
    
    public void setMoverIzquierda(boolean moverIzquierda) {
        this.moverIzquierda = moverIzquierda;
    }

    public void setMoverDerecha(boolean moverDerecha) {
        this.moverDerecha = moverDerecha;
    }

    public void setSaltar(boolean saltar) {
        this.saltar = saltar;
    }
}
