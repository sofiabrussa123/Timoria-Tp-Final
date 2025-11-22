package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import niveles.NivelBase;
import niveles.entorno.BarraInventario;
import niveles.entorno.BarraVida;
import personajes.movimientos.Animaciones;
import personajes.movimientos.Estado;

public class Personaje extends Actor {

    private String nombre;
    private BarraVida barraVida;
    private BarraInventario barraInventario;
    private Body cuerpo;
    private Animaciones animaciones = new Animaciones();
    private Animation<TextureRegion> animacionActual;
    private Estado estado;
    private float tiempoEstado;
    private boolean mirandoDerecha;
    private boolean mirandoIzquierda;
    private boolean enElAire;
    private int vida;
    private int vidaMaxima;
    private Sound sonidoDaño;
    private long tiempoUltimoDaño;
    private boolean sonidoReproduciéndose;
    private final long DURACION_SONIDO_DAÑO;
    float velocidadX;

	public Personaje(World mundo, String nombre, int coordenadaXAparicion, int coordenadaYAparicion) {

        this.estado = Estado.QUIETO;
        this.tiempoEstado = 0.0F;
        this.mirandoDerecha = true;
        this.mirandoIzquierda = false;
        this.enElAire = false;
        this.vida = 100;
        this.vidaMaxima = 100;
        this.sonidoDaño = Gdx.audio.newSound(Gdx.files.internal("Daño.mp3"));
        this.tiempoUltimoDaño = 0L;
        this.sonidoReproduciéndose = false;
        this.DURACION_SONIDO_DAÑO = 1000L;
        this.velocidadX = 0.0F;
        this.nombre = nombre;
        this.barraVida = new BarraVida(this, true);

        TextureRegion primerFrame = (TextureRegion)this.animaciones.getAnimacionQuieto().getKeyFrame(0.0F);

        float anchoPersonaje = (float)primerFrame.getRegionWidth();
        float altoPersonaje = (float)primerFrame.getRegionHeight();
        float anchoHitbox = 0.39999998F;
        float altoHitbox = 0.7F;

        this.crearCuerpo(mundo, anchoHitbox, altoHitbox, coordenadaXAparicion, coordenadaYAparicion);
        this.setSize(anchoPersonaje, altoPersonaje);
        this.animacionActual = this.animaciones.getAnimacionQuieto();
	}

    public void setBarraInventario(BarraInventario barraInventario) {

        this.barraInventario = barraInventario;
    }

    public BarraInventario getBarraInventario() {

        return this.barraInventario;
    }

	@Override
	public void act(float delta) {

		tiempoEstado += delta;

        cuerpo.setLinearVelocity(velocidadX, cuerpo.getLinearVelocity().y);

        switch (estado) {
        case CORRIENDO:
            animacionActual = animaciones.getAnimacionCorrer();
            break;
        case QUIETO:
            animacionActual = animaciones.getAnimacionQuieto();
            break;
            /*
        case SALTANDO:
            animacionActual = animaciones.getAnimacionSaltar();
            break;*/
    }

        if (this.sonidoReproduciéndose) {
            long ahora = System.currentTimeMillis();
            if (ahora - this.tiempoUltimoDaño >= 1000L) {
                this.sonidoDaño.stop();
                this.sonidoReproduciéndose = false;
            }
        }

        setPosition(
                (cuerpo.getPosition().x / NivelBase.PIXELES_A_METROS) - getWidth() / 2,
                (cuerpo.getPosition().y / NivelBase.PIXELES_A_METROS) - getHeight() / 2
        );
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {

		TextureRegion frameActual = animacionActual.getKeyFrame(tiempoEstado, true);

		if (mirandoIzquierda && !frameActual.isFlipX()) {
	        frameActual.flip(true, false);
	    } else if (mirandoDerecha && frameActual.isFlipX()) {
	        frameActual.flip(true, false);
	    }

        float posXPx = cuerpo.getPosition().x / NivelBase.PIXELES_A_METROS;
        float posYPx = cuerpo.getPosition().y / NivelBase.PIXELES_A_METROS;

        batch.draw(
            frameActual,
            posXPx - getWidth() / 2,
            posYPx - getHeight() / 2 + 30f,
            getWidth(),
            getHeight()
        );

        this.barraVida.draw(batch, parentAlpha);
	}

	private void crearCuerpo(World mundo, float anchoHitbox, float altoHitbox, int coordenadaXAparicion, int coordenadaYAparicion) {

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(coordenadaXAparicion * NivelBase.PIXELES_A_METROS, coordenadaYAparicion * NivelBase.PIXELES_A_METROS);
		bodyDef.fixedRotation = true;
		Body body = mundo.createBody(bodyDef);

        PolygonShape forma = new PolygonShape();
        forma.setAsBox(anchoHitbox / 2, altoHitbox / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = forma;
        fixtureDef.density = 3f;

        body.createFixture(fixtureDef);
        forma.dispose();

		this.cuerpo = body;
		this.cuerpo.setUserData(Personaje.this);
	}

	public void recibirDaño(int cantidad) {
        if (cantidad <= 0 || vida <= 0) return;

        this.vida -= cantidad;
        if (vida < 0) vida = 0;

        if (sonidoDaño != null) {

            if (!sonidoReproduciéndose) {
                sonidoDaño.play();
                sonidoReproduciéndose = true;
                tiempoUltimoDaño = System.currentTimeMillis();
            }
        }

        if (vida == 0) {
            sonidoDaño.stop();
            sonidoReproduciéndose = false;
        }
    }

	public void moverDerecha() {
		velocidadX = 5f;
        mirandoIzquierda = false;
        mirandoDerecha = true;
        estado = estado.CORRIENDO;
	}

	public void moverIzquierda() {
		velocidadX = -5f;
        mirandoIzquierda = true;
        mirandoDerecha = false;
        estado = estado.CORRIENDO;
	}

	public void saltar() {
		cuerpo.applyLinearImpulse(new Vector2(0, 7f), cuerpo.getWorldCenter(), true);
        enElAire = true;
        estado = estado.SALTANDO;
	}

	public void detener() {
		velocidadX = 0;
		estado = estado.QUIETO;
	}

	public void setEnElAire(boolean valor) {
        enElAire = valor;
    }

	public Body getCuerpo() {

		return this.cuerpo;
	}

	public int getVida() {

        return this.vida;
	}

	public int getVidaMaxima() {

        return this.vidaMaxima;
	}

	public boolean getEnElAire() {

        return this.enElAire;
	}

    public void setBarraVida(BarraVida barra1) {
    }

}
