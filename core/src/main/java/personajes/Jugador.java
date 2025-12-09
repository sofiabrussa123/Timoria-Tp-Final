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

import Red.HiloServidor;
import niveles.NivelBase;
import niveles.entorno.BarraInventario;
import niveles.entorno.BarraVida;
import personajes.accesorios.Estado;
import personajes.accesorios.MejoraTemporal;

public class Jugador extends Actor {

    private String nombre;
    private BarraVida barraVida;
    private int idJugador; // 1 o 2
    private BarraInventario barraInventario;
    private Body cuerpo;
    private Animation<TextureRegion> animacionActual;
    private Estado estado = Estado.QUIETO;
    private float tiempoEstado = 0f;
    private boolean mirandoDerecha = true;
    private boolean mirandoIzquierda = false;
    private boolean enElAire = false;
    private int vida = 100;
    private int vidaMaxima = 100;
    private Sound sonidoDaño = Gdx.audio.newSound(Gdx.files.internal("Daño.mp3"));;
    private long tiempoUltimoDaño = 0l;
    private boolean sonidoReproduciéndose = false;
    private float velocidadX = 0f;
    private float alcanceAtaque = 0.5f;
    private int dañoAtaque = 20;
    private float tiempoAtacando = 0f;
    private boolean atacando = false;
    private float cargaAtaque = 0.2f;
    private float cooldown = 0.4f;
    private float duracionAtaque = 0.015f;
    private boolean enCooldown = false;
    private MejoraTemporal mejoras;
    private HiloServidor hiloServidor;

	public Jugador(World mundo, String nombre, int coordenadaXAparicion, int coordenadaYAparicion, int idJugador) {

		this.mejoras = new MejoraTemporal();
		this.idJugador = idJugador;
        this.velocidadX = 5f + mejoras.getBonusVelocidad();
        this.velocidadX = -5f - mejoras.getBonusVelocidad();
        this.velocidadX = 0.0F;
        this.nombre = nombre;
        this.barraVida = new BarraVida(this, true);

        TextureRegion primerFrame = (TextureRegion)estado.QUIETO.crearAnimacion().getKeyFrame(0.0F);

        float anchoPersonaje = (float)primerFrame.getRegionWidth();
        float altoPersonaje = (float)primerFrame.getRegionHeight();
        float anchoHitbox = 0.39999998F;
        float altoHitbox = 0.7F;

        this.crearCuerpo(mundo, anchoHitbox, altoHitbox, coordenadaXAparicion, coordenadaYAparicion);
        this.setSize(anchoPersonaje, altoPersonaje);
        this.animacionActual = estado.QUIETO.crearAnimacion();
	}

	@Override
	public void act(float delta) {

		tiempoEstado += delta;
		this.hiloServidor.enviarMensajeATodos("CambiarFrame:"+this.idJugador+":"+tiempoEstado);
		
		this.tiempoAtacando += delta;

        cuerpo.setLinearVelocity(velocidadX, cuerpo.getLinearVelocity().y);

        animacionActual = estado.crearAnimacion();

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
        
        this.hiloServidor.enviarMensajeATodos("CambiarPosicion:Jugador:"+this.cuerpo.getPosition().x+":"+this.cuerpo.getPosition().y);
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
		this.cuerpo.setUserData(Jugador.this);
	}

	public void recibirDaño(int cantidad) {
        if (cantidad <= 0 || vida <= 0) return;

        this.vida -= cantidad;
        this.hiloServidor.enviarMensajeATodos("ActualizarVidaJugador:"+this.idJugador+":"+this.vida);
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
            this.hiloServidor.enviarMensajeATodos("EliminarEntidad:Jugador:"+this.idJugador);
        }
    }

    public void actualizarVidaConMejoras() {
        vidaMaxima = 100 + (int) mejoras.getBonusVida(); // Base fija + mejoras
        vida = vidaMaxima; // Restaurar vida completa
        this.hiloServidor.enviarMensajeATodos("ActualizarVidaJugador:"+this.idJugador+":"+this.vida);
    }

    public void moverDerecha() {
        velocidadX = 5f + mejoras.getBonusVelocidad();
        mirandoIzquierda = false;
        mirandoDerecha = true;
        estado = estado.CORRIENDO;
    }

    public void moverIzquierda() {
        velocidadX = -5f - mejoras.getBonusVelocidad();
        mirandoIzquierda = true;
        mirandoDerecha = false;
        estado = estado.CORRIENDO;
	}
	
	// Clase Jugador
	public void atacar(World mundo) { 
	    estado = estado.ATACANDO;

	    Vector2 posicionJugador = this.cuerpo.getPosition();
	    
	    float anchoAreaAtaque = this.alcanceAtaque; 
	    float altoAreaAtaque = 1.4f; 
	    
	    float centroXAreaAtaque = posicionJugador.x + (anchoAreaAtaque / 2 + (this.getWidth() * NivelBase.PIXELES_A_METROS) / 2) * (mirandoDerecha ? 1 : -1);
	    float centroYAreaAtaque = posicionJugador.y;
	    
	    float lowerX = centroXAreaAtaque - (anchoAreaAtaque / 2);
	    float upperX = centroXAreaAtaque + (anchoAreaAtaque / 2);
	    float lowerY = centroYAreaAtaque - (altoAreaAtaque / 2); 
	    float upperY = centroYAreaAtaque + (altoAreaAtaque / 2); 
	    
	    if(!enCooldown) {
			if(this.tiempoAtacando >= cargaAtaque && this.tiempoAtacando < this.cargaAtaque + this.duracionAtaque) {
				this.atacando = true;
				mundo.QueryAABB(fixture -> {
			        Object userData = fixture.getBody().getUserData();
			        
			        if (userData instanceof Enemigo) {
			            Enemigo enemigo = (Enemigo) userData;
			            
			            enemigo.recibirDaño(this.dañoAtaque);
			            
			            return false; 
			        }
			        return true; 
			    }, lowerX, lowerY, upperX, upperY);
			}
			else if (this.tiempoAtacando >= this.cargaAtaque + this.duracionAtaque){
				this.atacando = false;
				this.enCooldown = true;
				this.tiempoAtacando = 0f;
			}
		} else {
			if(this.tiempoAtacando >= cooldown) {
				this.enCooldown = false;
				this.tiempoAtacando = 0f;
			}
		}
	}
	
	public void resetearGolpe() {
		this.tiempoAtacando = 0;
		this.atacando = false;
		this.enCooldown = false;
	}
	
	public float getAlcanceAtaque() {
		return this.alcanceAtaque;
	}

    public void saltar() {
        float potenciaSalto = 7f + mejoras.getBonusSalto();
        cuerpo.applyLinearImpulse(new Vector2(0, potenciaSalto), cuerpo.getWorldCenter(), true);
        enElAire = true;
        /* estado = estado.SALTANDO; */
    }

    public void detener() {
        velocidadX = 0;
        estado = estado.QUIETO;
    }


    public Body getCuerpo() {

        return this.cuerpo;
    }

    public int getVida() {

        return this.vida;
    }

    public BarraInventario getBarraInventario() {

        return this.barraInventario;
    }

    public MejoraTemporal getMejoras() {

        return mejoras;
    }

    public int getVidaMaximaMejorada() {

        return vidaMaxima; // Ya incluye las mejoras aplicadas
    }

    public int getVidaMaxima() {

        return this.vidaMaxima;
    }

    public boolean getEnElAire() {

        return this.enElAire;
    }

    public int getIdJugador() {

        return this.idJugador;
    }

    public void setBarraInventario(BarraInventario barraInventario) {

        this.barraInventario = barraInventario;
    }

    public void setBarraVida(BarraVida barra1) {
    	this.barraVida = barra1;
    }

    public void setEnElAire(boolean valor) {

        enElAire = valor;
    }
}
