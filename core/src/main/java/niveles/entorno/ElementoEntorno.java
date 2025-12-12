package niveles.entorno;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import interfaces.IdManager;
import io.github.timoria.Principal;

public abstract class ElementoEntorno extends Actor implements IdManager{

    protected World mundo;
    protected float x;
    protected float y;
    protected Texture textura;
    protected Body cuerpo;
    protected float ancho;
    protected float alto;
    protected BodyDef.BodyType tipoCuerpo = BodyDef.BodyType.StaticBody;
    protected FixtureDef fixtureDef = new FixtureDef();
    protected int id;

    protected ElementoEntorno(World mundo, float x, float y, float ancho, float alto, int id) {
    	this.id = id;
        this.mundo = mundo;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    protected ElementoEntorno(World mundo, float x, float y, int id) {
    	this.id = id;
        this.mundo = mundo;
        this.x = x;
        this.y = y;
    }

    protected void crearYPosicionarCuerpo() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(
            (x + ancho / 2) / Principal.PPM,
            (y + alto / 2) / Principal.PPM
        );
        bodyDef.type = tipoCuerpo;

        cuerpo = mundo.createBody(bodyDef);
        cuerpo.setUserData(this);

        PolygonShape forma = new PolygonShape();
        forma.setAsBox(
            ancho / 2 / Principal.PPM,
            (alto / 2 - 2) / Principal.PPM
        );

        fixtureDef.shape = forma;
        fixtureDef.friction = 0.5f;
        cuerpo.createFixture(fixtureDef);
        forma.dispose();

        setBounds(x, y, ancho, alto);
        cuerpo.setUserData(this);
    }

    public Body getCuerpo() {
        return cuerpo;
    }

    protected void setTipoCuerpo(BodyDef.BodyType tipo) {
        this.tipoCuerpo = tipo;
    }

    protected void setFixtureDef(FixtureDef fixtureDef) {
        this.fixtureDef = fixtureDef;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textura, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void act(float delta) {
        Vector2 posicion = cuerpo.getPosition();
        setPosition(
            posicion.x * Principal.PPM - ancho / 2,
            posicion.y * Principal.PPM - alto / 2
        );
    }

    public void dispose() {
        textura.dispose();
    }
    
    @Override
    public int getId() {
    	return this.id;
    }
}
