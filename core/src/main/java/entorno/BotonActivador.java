package entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.timoria.Principal;

public class BotonActivador extends Actor {

    private Texture textura;
    private Body cuerpo;
    private PuertaLlegada puerta; // Referencia a la puerta que se va a desbloquear
    private boolean fueActivado = false;

    public BotonActivador(World mundo, float x, float y, float ancho, float alto, PuertaLlegada puerta) {
        this.puerta = puerta;
        textura = new Texture(Gdx.files.internal("Boton.png"));

        BodyDef defCuerpo = new BodyDef();
        defCuerpo.type = BodyDef.BodyType.StaticBody;
        defCuerpo.position.set((x + ancho / 2) / Principal.PPM, (y + alto / 2) / Principal.PPM);
        cuerpo = mundo.createBody(defCuerpo);

        PolygonShape forma = new PolygonShape();
        forma.setAsBox(ancho / 2 / Principal.PPM, alto / 2 / Principal.PPM);

        FixtureDef defFixture = new FixtureDef();
        defFixture.shape = forma;
        defFixture.isSensor = true;
        cuerpo.createFixture(defFixture);
        forma.dispose();

        setBounds(x, y, ancho, alto);
        cuerpo.setUserData(this);
    }

    public void activar() {
        if (!fueActivado) {
            puerta.desbloquear(); // desbloqueamos la puerta
            fueActivado = true;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!fueActivado) {
            batch.draw(textura, getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public void act(float delta) {
        Vector2 posicion = cuerpo.getPosition();
        setPosition(posicion.x * Principal.PPM - getWidth() / 2, posicion.y * Principal.PPM - getHeight() / 2);
    }

    public Body getCuerpo() {
        return cuerpo;
    }

    public void dispose() {
        textura.dispose();
    }
}
