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

import Red.HiloCliente;
import interfaces.IdManager;
import niveles.NivelBase;
import niveles.entorno.BarraInventario;
import niveles.entorno.BarraVida;
import personajes.movimientos.Estado;

public class Jugador extends Actor implements IdManager{

    // =========================================================================
    // I. PROPIEDADES BASE
    // =========================================================================
    private int id;
    private String nombre;
    private MejoraTemporal mejoras;
    private HiloCliente hiloCliente;

    // Elementos de UI
    private BarraVida barraVida;
    private BarraInventario barraInventario;

    // =========================================================================
    // II. PROPIEDADES FÍSICAS Y ESTADO (Box2D)
    // =========================================================================
    private Body cuerpo;
    private float anchoHitbox = 0.4f; // En metros
    private float altoHitbox = 0.7f;  // En metros
    private float velocidadBase = 5f; 
    private float fuerzaSaltoBase = 7f;
    private float velocidadX = 0f; // Velocidad aplicada al cuerpo
    private boolean enElAire = false;

    // =========================================================================
    // III. PROPIEDADES DE VIDA, DAÑO Y SONIDO
    // =========================================================================
    private int vida = 100;
    private int vidaMaxima = 100;
    private int dañoBase = 20;
    private Sound sonidoDaño = Gdx.audio.newSound(Gdx.files.internal("Daño.mp3"));
    private long tiempoUltimoDaño = 0L;
    private boolean sonidoReproduciéndose = false;

    // =========================================================================
    // IV. PROPIEDADES DE ANIMACIÓN Y ATAQUE
    // =========================================================================
    private Animation<TextureRegion> animacionActual;
    private Estado estado = Estado.QUIETO;
    private float tiempoEstado = 0f; // Tiempo transcurrido para la animación
    private boolean mirandoDerecha = true;
    private boolean mirandoIzquierda = false;
    
    private float alcanceAtaque = 0.5f;
    private float tiempoTranscurridoAtaque = 0f;
    private float duracionAnimacionAtaque = 0.4f;
    private float cooldownAtaque = 0.5f;
    private boolean puedeAtacar = true;
    private boolean teclaPresionada = false;
    private boolean atacando = false;
    private boolean muerto = false;


    // =========================================================================
    // V. CONSTRUCTOR Y MÉTODOS DE INICIALIZACIÓN
    // =========================================================================
    public Jugador(World mundo, String nombre, int coordenadaXAparicion, int coordenadaYAparicion, int id, MejoraTemporal mejoras, HiloCliente hiloCliente) {
        this.id = id;
        this.nombre = nombre;
        this.mejoras = mejoras;
        this.barraVida = new BarraVida(this, true);
        this.hiloCliente = hiloCliente;
        
        aplicarMejoras();
        
        TextureRegion primerFrame = (TextureRegion) estado.QUIETO.crearAnimacion().getKeyFrame(0.0F);
        
        float anchoPersonajePx = primerFrame.getRegionWidth(); 
        float altoPersonajePx = primerFrame.getRegionHeight();
        
        this.crearCuerpo(mundo, anchoHitbox, altoHitbox, coordenadaXAparicion, coordenadaYAparicion); 
        this.setSize(anchoPersonajePx, altoPersonajePx); // Establece el tamaño del Actor (sprite)
        this.animacionActual = estado.QUIETO.crearAnimacion();
    }
    
    private void aplicarMejoras() {
        this.vidaMaxima = 100 + (int) mejoras.getBonusVida();
        this.vida = this.vidaMaxima;
    }
    
    private void crearCuerpo(World mundo, float anchoHitbox, float altoHitbox, int coordenadaXAparicion, int coordenadaYAparicion) { 
        BodyDef bodyDef = new BodyDef(); 
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(coordenadaXAparicion * NivelBase.PIXELES_A_METROS, coordenadaYAparicion * NivelBase.PIXELES_A_METROS); 
        bodyDef.fixedRotation = true; 
        Body body = mundo.createBody(bodyDef);
        
        PolygonShape forma = new PolygonShape(); 
        forma.setAsBox(anchoHitbox / 2, altoHitbox / 2); // Usa las dimensiones en metros
        
        FixtureDef fixtureDef = new FixtureDef(); fixtureDef.shape = forma;
        fixtureDef.density = 3f;
        
        body.createFixture(fixtureDef); forma.dispose();
        
        this.cuerpo = body; 
        this.cuerpo.setUserData(Jugador.this); 
    }

    // =========================================================================
    // VI. CICLO DE VIDA (ACTUALIZACIÓN Y DIBUJADO)
    // =========================================================================
    @Override 
    public void act(float delta) { 
        tiempoEstado += delta;
        
        // 🚨 Lógica de movimiento, ataque y cooldown (A ser modificada para red)
        if (atacando) { 
            tiempoTranscurridoAtaque += delta;
            if (tiempoTranscurridoAtaque >= duracionAnimacionAtaque) { 
                atacando = false;
                tiempoTranscurridoAtaque = 0f; 
                estado = Estado.QUIETO; 
            } 
        }
        if (!puedeAtacar) { 
            tiempoTranscurridoAtaque += delta; 
            if (tiempoTranscurridoAtaque >= cooldownAtaque) { 
                puedeAtacar = true;
                tiempoTranscurridoAtaque = 0f; 
            } 
        }
        
        cuerpo.setLinearVelocity(velocidadX, cuerpo.getLinearVelocity().y);
        
        if (!atacando) { animacionActual = estado.crearAnimacion(); }
        
        if (this.sonidoReproduciéndose) { 
            long ahora = System.currentTimeMillis(); 
            if (ahora - this.tiempoUltimoDaño >= 1000L) { 
                this.sonidoDaño.stop();
                this.sonidoReproduciéndose = false; 
            } 
        }
        
        setPosition( 
            cuerpo.getPosition().x / NivelBase.PIXELES_A_METROS - getWidth() / 2,
            cuerpo.getPosition().y / NivelBase.PIXELES_A_METROS - getHeight() / 2 
        );
        
        if(enElAire) {
            this.hiloCliente.enviarMensaje("Jugador:Mover:"+this.id+":"+this.cuerpo.getPosition().x+":"+this.cuerpo.getPosition().y);  
        } 
    }
    
    @Override 
    public void draw(Batch batch, float parentAlpha) { 
        TextureRegion frameActual = animacionActual.getKeyFrame(tiempoEstado, true);
        
        if (mirandoIzquierda && !frameActual.isFlipX()) { frameActual.flip(true, false); } 
        else if (mirandoDerecha && frameActual.isFlipX()) { frameActual.flip(true, false); }
        
        float posXPx = cuerpo.getPosition().x / NivelBase.PIXELES_A_METROS; 
        float posYPx = cuerpo.getPosition().y / NivelBase.PIXELES_A_METROS;
        
        if(!muerto) {
        	batch.draw( 
                    frameActual, 
                    posXPx - getWidth() / 2, 
                    posYPx - getHeight() / 2 + 30f, 
                    getWidth(), getHeight() 
                );
        }
        
        this.barraVida.draw(batch, parentAlpha); 
    }

    // =========================================================================
    // VII. MÉTODOS DE ENTRADA Y CONTROL (Predicción del Cliente)
    // =========================================================================
    
    public void moverDerecha() { 
        if (!atacando) { 
            // PREDECIR: Mover localmente
            velocidadX = velocidadBase + mejoras.getBonusVelocidad(); 
            mirandoIzquierda = false; 
            mirandoDerecha = true;
            estado = Estado.CORRIENDO; 
            // COMANDAR: Enviar comando al servidor
            this.hiloCliente.enviarMensaje("Jugador:"+this.id+":true");
        } 
    }
    
    public void moverIzquierda() { 
        if (!atacando) { 
            velocidadX = -(velocidadBase + mejoras.getBonusVelocidad()); 
            mirandoIzquierda = true; 
            mirandoDerecha = false;
            estado = Estado.CORRIENDO; 
            this.hiloCliente.enviarMensaje("Jugador:"+this.id+":false");
        } 
    }
    
    public void saltar() {
        float fuerzaSalto = fuerzaSaltoBase + mejoras.getBonusSalto();
        cuerpo.applyLinearImpulse(new Vector2(0, fuerzaSalto), cuerpo.getWorldCenter(), true);
        enElAire = true;
        this.hiloCliente.enviarMensaje("Jugador:"+this.id+":Saltar");
    }
    
    public void detener() { 
        if (!atacando) { 
            velocidadX = 0; 
            estado = Estado.QUIETO; 
            this.hiloCliente.enviarMensaje("Jugador:"+this.id+":Detener");
        } 
    }
    
    public void atacar(World mundo, boolean friendlyFire) { 
        if (puedeAtacar && !teclaPresionada) { 
            teclaPresionada = true; 
            atacando = true; 
            puedeAtacar = false;
            tiempoTranscurridoAtaque = 0f;
            estado = Estado.ATACANDO; 
            animacionActual = Estado.ATACANDO.crearAnimacion();
            tiempoEstado = 0f;
            this.hiloCliente.enviarMensaje("Jugador:"+this.id+":Atacar");
        } 
    }
    
    public void resetearTeclaAtaque() { teclaPresionada = false; }
    
    // =========================================================================
    // VIII. MÉTODOS DE RED (Reconciliación y Sincronización)
    // =========================================================================
    
    public void moverCuerpo(int posX, int posY) {
        // Forzar el Body a la posición real dictada por el servidor.
        this.cuerpo.setTransform(
            posX * NivelBase.PIXELES_A_METROS, 
            posY * NivelBase.PIXELES_A_METROS, 
            this.cuerpo.getAngle()
        );
    }
    
    public void setFrameAnimacion(int numFrame, boolean mirandoDerecha) {
        this.mirandoIzquierda = !mirandoDerecha;
        
        // Si el servidor confirma un ataque, reinicia la animación de ataque (incluso si la predicción falló)
        if (this.estado == Estado.ATACANDO) {
             this.atacando = true;
             this.tiempoTranscurridoAtaque = 0f;
             this.animacionActual = Estado.ATACANDO.crearAnimacion();
             this.tiempoEstado = 0f;
        }
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
    
    public void morir() {
    	super.setVisible(false);
    }
    
    @Override public int getId() { return this.id; }
    
    public void setEnElAire(boolean valor) { enElAire = valor; }
    
    public Body getCuerpo() { return this.cuerpo; }
    
    public int getVida() { return this.vida; }
    
    public MejoraTemporal getMejoras() { return this.mejoras; }
    
    public BarraInventario getBarraInventario() { return this.barraInventario; }
    
    public float getAlcanceAtaque() { return this.alcanceAtaque; }
    
    public int getDañoAtaque() { return dañoBase + (int) mejoras.getBonusDaño(); }
    
    public BarraVida getBarraVida() { return this.barraVida; }
    
    public int getVidaMaxima() { return this.vidaMaxima; }
    
    public boolean getEnElAire() { return this.enElAire; }
    
    public void setBarraVida(BarraVida barra1) { this.barraVida = barra1; }
    
    public void setBarraInventario(BarraInventario barraInventario) { this.barraInventario = barraInventario; }
}