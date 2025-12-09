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

import Red.HiloServidor;
import niveles.NivelBase;

public class Enemigo extends Actor {

	private int vida = 50;
    private Texture textura;
    private Body cuerpo;
    private float anchoHitbox;
    private float altoHitbox;
    private int daño = 10;
    private int cooldown = 1;
    private float tiempoTranscurrido = 0;
    private NivelBase nivel;
    private float alcanceAtaque = 0.3f;
    private boolean enCooldown = false;
    private boolean encontroJugador;
    private World mundo;
    private boolean atacandoVisualmente = false;
    private float tiempoAtaqueVisual = 0.1f; 
    private float contadorAtaqueVisual = 0f;
    private boolean muerto = false;
    private HiloServidor hiloServidor;
    private final int ID;

    public Enemigo(World mundo, float x, float y, NivelBase nivel, int id) {
    	this.ID = id;
        this.textura = new Texture("enemigo.png");
        this.nivel = nivel;
        this.anchoHitbox = 48;
        this.altoHitbox = 48;
        this.mundo = mundo;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(x * NivelBase.PIXELES_A_METROS, y * NivelBase.PIXELES_A_METROS);
        def.fixedRotation = true;

        this.cuerpo = this.mundo.createBody(def);

        PolygonShape forma = new PolygonShape();
        forma.setAsBox(anchoHitbox / 2 * NivelBase.PIXELES_A_METROS, altoHitbox / 2 * NivelBase.PIXELES_A_METROS);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = forma;
        fixture.density = 1f;
        fixture.friction = 0.5f;
        cuerpo.createFixture(fixture);
        forma.dispose();

        setSize(anchoHitbox, altoHitbox);
        cuerpo.setUserData(this);
    }

    public void aplicarDañoJugador(Jugador jugador) {
        jugador.recibirDaño((int) daño);
    }
    
    public void recibirDaño(int dañoAtaque) {
    	if (dañoAtaque <= 0 || vida <= 0) return;
    	
    	if(vida - dañoAtaque < 0) {
    		this.vida = 0;
    	} 
    	else {
    		this.vida -= dañoAtaque;
    	}
    	
    	if(vida == 0) muerto = true;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        
        determinarDireccionMovimiento(calcularJugadorObjetivo());
        
        this.hiloServidor.enviarMensajeATodos("CambiarPosicion:Enemigo:"+getPosicionX()+":"+getPosicionY());
        
        this.tiempoTranscurrido += delta;
        
        if (atacandoVisualmente) {
            contadorAtaqueVisual += delta;
            if (contadorAtaqueVisual >= tiempoAtaqueVisual) {
                atacandoVisualmente = false;
                contadorAtaqueVisual = 0f;
            }
        }
        
        if(this.tiempoTranscurrido >= cooldown && enCooldown) {
        	enCooldown = false;
        } 
        
        if(!enCooldown) {
        	encontroJugador = false;
        	
        	Vector2 posicionEnemigo = this.cuerpo.getPosition();
    	    
    	    float centroXAreaAtaque = posicionEnemigo.x;
    	    float centroYAreaAtaque = posicionEnemigo.y;
    	    
    	    float lowerX = centroXAreaAtaque - (anchoHitbox*NivelBase.PIXELES_A_METROS/2) - alcanceAtaque;
    	    float upperX = centroXAreaAtaque + (anchoHitbox*NivelBase.PIXELES_A_METROS/2) + alcanceAtaque;
    	    float lowerY = centroYAreaAtaque - (altoHitbox*NivelBase.PIXELES_A_METROS/2) - alcanceAtaque; 
    	    float upperY = centroYAreaAtaque + (altoHitbox*NivelBase.PIXELES_A_METROS/2) + alcanceAtaque; 
    	    
    				this.mundo.QueryAABB(fixture -> {
    			        Object userData = fixture.getBody().getUserData();
    			        
    			        if (userData instanceof Jugador) {
    			            Jugador jugador = (Jugador) userData;
    			            
    			            jugador.recibirDaño(this.daño);
    			            this.encontroJugador = true;
    			            
    			            return true; 
    			        }
    			        return true; 
    			    }, lowerX, lowerY, upperX, upperY);
    				
    				if(encontroJugador) {
    					this.enCooldown = true;
						this.tiempoTranscurrido = 0f;
						this.atacandoVisualmente = true;
    				}
        }
    }

	private void determinarDireccionMovimiento(Jugador objetivo) {
		// Movimiento hacia el jugador
        Vector2 posicionJugador = objetivo.getCuerpo().getPosition();
        Vector2 posicionEnemigo = cuerpo.getPosition();

        Vector2 direccion = posicionJugador.cpy().sub(posicionEnemigo).nor().scl(1.5f);
        cuerpo.setLinearVelocity(direccion.x, cuerpo.getLinearVelocity().y);

        // Actualizar posición del actor
        setPosition(
            cuerpo.getPosition().x / NivelBase.PIXELES_A_METROS - anchoHitbox / 2,
            cuerpo.getPosition().y / NivelBase.PIXELES_A_METROS - altoHitbox / 2
        );
        
        this.hiloServidor.enviarMensajeATodos("CambiarPosicion:Enemigo:"+this.cuerpo.getPosition().x);
	}
    
    private Jugador calcularJugadorObjetivo() {
    	
    	float posicionAbsolutaJugador1 = Math.abs((this.nivel.getJugador1().getX()));
    	float posicionAbsolutaJugador2 = Math.abs((this.nivel.getJugador2().getX()));
    	float posicionAbsolutaEnemigo = Math.abs(this.getX());
    	
    	if(Math.abs(posicionAbsolutaJugador1 - posicionAbsolutaEnemigo) > Math.abs(posicionAbsolutaJugador2 - posicionAbsolutaEnemigo)) {
    		 return this.nivel.getJugador2();
    	} else return this.nivel.getJugador1();
    }
    
    public void eliminar() {
        if (this.cuerpo != null && this.mundo != null) {
            this.mundo.destroyBody(this.cuerpo);
            this.cuerpo = null;
        }
        this.remove();
    }
    
    public boolean getMuerto() {
    	return this.muerto;
    }
    
    public int getID() {
    	return this.ID;
    }
    
    public float getPosicionX() {
    	return this.cuerpo.getPosition().x;
    }
    
    public float getPosicionY() {
    	return this.cuerpo.getPosition().y;
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

    public void dispose() {
        textura.dispose();
    }
}
