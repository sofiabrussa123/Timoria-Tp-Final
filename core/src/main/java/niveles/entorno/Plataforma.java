package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.timoria.Principal;

public class Plataforma extends Actor {

    private Texture textura;
    private Body cuerpo;

    private float ancho;
    private float alto;

    public Plataforma(World mundo, float x, float y, float ancho, float alto) {
        this.ancho = ancho;
        this.alto = alto;

        textura = new Texture(Gdx.files.internal("plataforma.jpg"));

        // Definir el cuerpo estático
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set((x + ancho / 2) / Principal.PPM, (y + alto / 2) / Principal.PPM);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        cuerpo = mundo.createBody(bodyDef);

        // Definir la forma
        PolygonShape forma = new PolygonShape();
        forma.setAsBox(
            ancho / 2 / Principal.PPM,
            (alto - 10) / 2 / Principal.PPM,
            new Vector2(0, 5 / Principal.PPM), // desplaza la hitbox hacia arriba
            0
        );


        // Definir la fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = forma;
        fixtureDef.friction = 0.5f;
        cuerpo.createFixture(fixtureDef);
        forma.dispose();

        setBounds(x, y, ancho, alto);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textura, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        Vector2 posicion = cuerpo.getPosition();
        setPosition(posicion.x * Principal.PPM - ancho / 2, posicion.y * Principal.PPM - alto / 2);
    }

    public void dispose() {
        textura.dispose();
    }
}
