package personajes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.github.timoria.NivelBase;

public class Enemigo extends Actor {

    private Texture textura;
    private Body cuerpo;
    private float ancho;
    private float alto;
    private float daño;
    private Personaje objetivo;

    public Enemigo(World mundo, float x, float y, float daño, Personaje jugador) {
        this.textura = new Texture("enemigo.png");
        this.objetivo = jugador;
        this.daño = daño;

        this.ancho = 48;
        this.alto = 48;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x * NivelBase.PIXELES_A_METROS, y * NivelBase.PIXELES_A_METROS);
        def.fixedRotation = true;

        this.cuerpo = mundo.createBody(def);

        PolygonShape forma = new PolygonShape();
        forma.setAsBox(ancho / 2 * NivelBase.PIXELES_A_METROS, alto / 2 * NivelBase.PIXELES_A_METROS);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = forma;
        fixture.density = 1f;
        fixture.friction = 0.5f;
        cuerpo.createFixture(fixture);
        forma.dispose();

        setSize(ancho, alto);
        cuerpo.setUserData(this);
    }

    public void aplicarDañoJugador(Personaje jugador) {
        jugador.recibirDaño((int) daño);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Movimiento hacia el jugador
        Vector2 posicionJugador = objetivo.getCuerpo().getPosition();
        Vector2 posicionEnemigo = cuerpo.getPosition();

        Vector2 direccion = posicionJugador.cpy().sub(posicionEnemigo).nor().scl(1.5f);
        cuerpo.setLinearVelocity(direccion.x, cuerpo.getLinearVelocity().y);

        // Actualizar posición del actor
        setPosition(
            cuerpo.getPosition().x / NivelBase.PIXELES_A_METROS - ancho / 2,
            cuerpo.getPosition().y / NivelBase.PIXELES_A_METROS - alto / 2
        );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textura, getX(), getY(), getWidth(), getHeight());
    }

    public float getDaño() {
        return daño;
    }

    public void dispose() {
        textura.dispose();
    }
}
