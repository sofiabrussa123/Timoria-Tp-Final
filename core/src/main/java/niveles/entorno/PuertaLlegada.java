package entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.timoria.Principal;

public class PuertaLlegada extends Actor {

    private Texture textura;
    private Body cuerpo;

    public PuertaLlegada(World mundo, float x, float y, float ancho, float alto) {
        textura = new Texture(Gdx.files.internal("puerta.png")); 

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

    private boolean estaBloqueada = true;

    public boolean sePuedeCruzar() {
        return !estaBloqueada;
    }

    public void desbloquear() {
        estaBloqueada = false;
        textura = new Texture(Gdx.files.internal("puertaAbierta.png"));
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textura, getX(), getY(), getWidth(), getHeight());
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
