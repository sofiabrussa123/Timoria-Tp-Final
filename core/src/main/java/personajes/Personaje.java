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

import io.github.timoria.Principal;
import niveles.NivelBase;
import niveles.entorno.BarraVida;
import personajes.movimientos.Animaciones;
import personajes.movimientos.Estado;

public class Personaje extends Actor {
	
	private String nombre;
	private BarraVida barraVida;
	private Body cuerpo;
	private Animaciones animaciones = new Animaciones();
	private Animation<TextureRegion> animacionActual;
	private Estado estado = Estado.QUIETO;
	private float tiempoEstado = 0;
	private boolean mirandoDerecha = true;
	private boolean mirandoIzquierda = false;
    private boolean moverIzquierda = false;
    private boolean moverDerecha = false;
    private boolean saltar = false;
    private boolean enElAire = false;
    private int vida = 100;
    private int vidaMaxima = 100;
    private Sound sonidoDaño = Gdx.audio.newSound(Gdx.files.internal("Daño.mp3"));
    private long tiempoUltimoDaño = 0;
    private boolean sonidoReproduciéndose = false;
    private final long DURACION_SONIDO_DAÑO = 1000;
	
	public Personaje(World mundo, String nombre, int coordenadaXAparicion, int coordenadaYAparicion) {
		
		this.nombre = nombre;
		this.barraVida = new BarraVida(this);
        
        TextureRegion primerFrame = animaciones.getAnimacionQuieto().getKeyFrame(0);
        
        float anchoPersonaje = primerFrame.getRegionWidth();
        float altoPersonaje = primerFrame.getRegionHeight();
        
        float anchoHitbox = 40 * NivelBase.PIXELES_A_METROS;
        float altoHitbox = 70 * NivelBase.PIXELES_A_METROS;

        crearCuerpo(mundo, anchoHitbox, altoHitbox, coordenadaXAparicion, coordenadaYAparicion);

        setSize(anchoPersonaje, altoPersonaje);
        
		this.animacionActual = animaciones.getAnimacionQuieto();
	}
	
	@Override
	public void act(float delta) {
		
		tiempoEstado += delta;
		
		float velocidadX = 0;

		if (moverIzquierda) {
            velocidadX = -5f;
            mirandoIzquierda = true;
            mirandoDerecha = false;
            estado = estado.CORRIENDO;
        } else if (moverDerecha) {
            velocidadX = 5f;
            mirandoIzquierda = false;
            mirandoDerecha = true;
            estado = estado.CORRIENDO;
        } else {
            estado = estado.QUIETO;
        }
            
		if (saltar && !enElAire) {
        	cuerpo.applyLinearImpulse(new Vector2(0, 7f), cuerpo.getWorldCenter(), true);
            enElAire = true;
            estado = estado.SALTANDO;
        }
            

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
        
        if (sonidoReproduciéndose) {
            long ahora = System.currentTimeMillis();
            if (ahora - tiempoUltimoDaño >= DURACION_SONIDO_DAÑO) {
                sonidoDaño.stop();
                sonidoReproduciéndose = false;
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
        
        barraVida.setPosition(10, 760);
        barraVida.draw(batch, parentAlpha);
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
	
	public void setMoverIzquierda(boolean valor) {
        this.moverIzquierda = valor;
    }
	
	public void setMoverDerecha(boolean valor) {
        this.moverDerecha = valor;
    }
	
	public void setSaltar(boolean valor) {
        this.saltar = valor;
    }
}