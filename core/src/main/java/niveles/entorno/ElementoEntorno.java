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

import io.github.timoria.Principal;
import niveles.NivelBase;

public abstract class ElementoEntorno extends Actor {

    protected World mundo;
    protected float x;
    protected float y;
    protected Texture textura;
    protected Body cuerpo;
    protected float ancho;
    protected float alto;
    protected BodyDef.BodyType tipoCuerpo = BodyDef.BodyType.StaticBody;
    protected FixtureDef fixtureDef = new FixtureDef();
    protected final int ID;

    protected ElementoEntorno(World mundo, float x, float y, float ancho, float alto, int id) {
    	this.ID = id;
        this.mundo = mundo;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }
    
    protected ElementoEntorno(World mundo, float x, float y, int id) {
    	this.ID = id;
        this.mundo = mundo;
        this.x = x;
        this.y = y;
    }
    
    protected void crearYPosicionarCuerpo() {
    	// Definir el cuerpo estático
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set((x + this.ancho / 2) / Principal.PPM, (y + this.alto / 2) / Principal.PPM);
        bodyDef.type = this.tipoCuerpo;
        cuerpo = mundo.createBody(bodyDef);
        cuerpo.setUserData(this);

        // Definir la forma
        PolygonShape forma = new PolygonShape();
        forma.setAsBox(
        	ancho / 2 / Principal.PPM,
        	(alto / 2 - 2) / Principal.PPM
        );

        // Definir la fixture
        fixtureDef.shape = forma;
        fixtureDef.friction = 0.5f;
        this.cuerpo.createFixture(fixtureDef);
        forma.dispose();

        setBounds(x, y, this.ancho, this.alto);
        cuerpo.setUserData(this);
    }
    
    public Body getCuerpo() {
    	return this.cuerpo;
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
        setPosition(posicion.x * Principal.PPM - this.ancho / 2, posicion.y * Principal.PPM - this.alto / 2);
    }

    public void dispose() {
        textura.dispose();
    }
}