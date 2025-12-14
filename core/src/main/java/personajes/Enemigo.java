package personajes;

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
import niveles.NivelBase;

public class Enemigo extends Actor implements IdManager{

	private int vida = 50;
    private Texture textura;
    private Body cuerpo;
    private float anchoHitbox;
    private float altoHitbox;
    private int daño = 10;
    private int cooldown = 1;
    private float tiempoTranscurrido = 0;
    private float alcanceAtaque = 0.15f;
    private boolean enCooldown = false;
    private World mundo;
    private boolean atacandoVisualmente = false;
    private float tiempoAtaqueVisual = 0.1f;
    private float contadorAtaqueVisual = 0f;
    private boolean muerto = false;
    private int id;

    public Enemigo(World mundo, float x, float y, int id) {
    	this.id = id;
        this.textura = new Texture("enemigo.png");
        this.anchoHitbox = 48;
        this.altoHitbox = 48;
        this.mundo = mundo;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x * NivelBase.PIXELES_A_METROS, y * NivelBase.PIXELES_A_METROS);
        def.fixedRotation = true;

        this.cuerpo = this.mundo.createBody(def);

        PolygonShape forma = new PolygonShape();
        forma.setAsBox(
            anchoHitbox / 2 * NivelBase.PIXELES_A_METROS,
            altoHitbox / 2 * NivelBase.PIXELES_A_METROS
        );

        FixtureDef fixture = new FixtureDef();
        fixture.shape = forma;
        fixture.density = 1f;
        fixture.friction = 0.5f;
        cuerpo.createFixture(fixture);
        forma.dispose();

        setSize(anchoHitbox, altoHitbox);
        cuerpo.setUserData(this);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        this.tiempoTranscurrido += delta;

        if (atacandoVisualmente) {
            contadorAtaqueVisual += delta;
            if (contadorAtaqueVisual >= tiempoAtaqueVisual) {
                atacandoVisualmente = false;
                contadorAtaqueVisual = 0f;
            }
        }

        if (this.tiempoTranscurrido >= cooldown && enCooldown) {
            enCooldown = false;
        }

        setPosition(
            cuerpo.getPosition().x / NivelBase.PIXELES_A_METROS - anchoHitbox / 2,
            cuerpo.getPosition().y / NivelBase.PIXELES_A_METROS - altoHitbox / 2
        );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float xDraw = getX();
        float yDraw = getY();
        float anchoDraw = getWidth();
        float altoDraw = getHeight();

        if (atacandoVisualmente) {
            float alcancePixeles = alcanceAtaque / NivelBase.PIXELES_A_METROS;

            anchoDraw += 2 * alcancePixeles;
            altoDraw += 2 * alcancePixeles;

            xDraw -= alcancePixeles;
            yDraw -= alcancePixeles;
        }

        batch.draw(this.textura, xDraw, yDraw, anchoDraw, altoDraw);
    }

    public void aplicarDañoJugador(Jugador jugador) {
        jugador.recibirDaño(daño);
    }

    public void recibirDaño(int dañoAtaque) {
        if (dañoAtaque <= 0 || vida <= 0) return;

        if (vida - dañoAtaque < 0) {
            this.vida = 0;
        } else {
            this.vida -= dañoAtaque;
        }

        if (vida == 0) muerto = true;
    }

    public void eliminar() {
        if (this.cuerpo != null && this.mundo != null) {
            this.mundo.destroyBody(this.cuerpo);
            this.cuerpo = null;
        }
        this.remove();
    }
    
    public void mover(int posX, int posY) {
        if (this.cuerpo != null) {
            this.cuerpo.setTransform(
                posX * NivelBase.PIXELES_A_METROS, 
                posY * NivelBase.PIXELES_A_METROS, 
                this.cuerpo.getAngle()
            );
        }
    }

    public boolean getMuerto() {
        return this.muerto;
    }

    public void dispose() {
        textura.dispose();
    }
    
    @Override
    public int getId() {
    	return this.id;
    }
}
