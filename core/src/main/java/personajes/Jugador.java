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
import personajes.movimientos.Estado;

public class Jugador extends Actor {

    private int id;
    private String nombre;
    private BarraVida barraVida;
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
    private Sound sonidoDaño = Gdx.audio.newSound(Gdx.files.internal("Daño.mp3"));
    private long tiempoUltimoDaño = 0l;
    private boolean sonidoReproduciéndose = false;

    // Stats base
    private float velocidadBase = 5f;
    private float fuerzaSaltoBase = 7f;
    private int dañoBase = 20;

    private float velocidadX = 0f;
    private float alcanceAtaque = 0.5f;
    private float tiempoTranscurridoAtaque = 0f;
    private float duracionAnimacionAtaque = 0.4f;
    private float cooldownAtaque = 0.5f;
    private boolean puedeAtacar = true;
    private boolean teclaPresionada = false;
    private boolean atacando = false;
    private float anchoHitbox = 0.4f;
    private float altoHitbox = 0.7f;

    private MejoraTemporal mejoras;

    public Jugador(World mundo, String nombre, int coordenadaXAparicion, int coordenadaYAparicion, int id, MejoraTemporal mejoras) {
        this.id = id;
        this.nombre = nombre;
        this.mejoras = mejoras;
        this.barraVida = new BarraVida(this, true);

        // Aplicar mejoras a stats
        aplicarMejoras();

        TextureRegion primerFrame = (TextureRegion)estado.QUIETO.crearAnimacion().getKeyFrame(0.0F);

        float anchoPersonaje = (float)primerFrame.getRegionWidth();
        float altoPersonaje = (float)primerFrame.getRegionHeight();

        this.crearCuerpo(mundo, anchoHitbox, altoHitbox, coordenadaXAparicion, coordenadaYAparicion);
        this.setSize(anchoPersonaje, altoPersonaje);
        this.animacionActual = estado.QUIETO.crearAnimacion();
    }

    private void aplicarMejoras() {
        // Aplicar bonus de vida
        this.vidaMaxima = 100 + (int)mejoras.getBonusVida();
        this.vida = this.vidaMaxima;
    }

    public int getId() {
        return this.id;
    }

    public MejoraTemporal getMejoras() {
        return this.mejoras;
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

        // Gestión del ataque y cooldown
        if (atacando) {
            tiempoTranscurridoAtaque += delta;

            // Terminar animación de ataque
            if (tiempoTranscurridoAtaque >= duracionAnimacionAtaque) {
                atacando = false;
                tiempoTranscurridoAtaque = 0f;
                estado = Estado.QUIETO;
            }
        }

        // Cooldown independiente
        if (!puedeAtacar) {
            tiempoTranscurridoAtaque += delta;
            if (tiempoTranscurridoAtaque >= cooldownAtaque) {
                puedeAtacar = true;
                tiempoTranscurridoAtaque = 0f;
            }
        }

        cuerpo.setLinearVelocity(velocidadX, cuerpo.getLinearVelocity().y);

        // Actualizar animación solo si no está atacando
        if (!atacando) {
            animacionActual = estado.crearAnimacion();
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
        this.cuerpo.setUserData(Jugador.this);
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
        if (!atacando) {
            velocidadX = velocidadBase + mejoras.getBonusVelocidad();
            mirandoIzquierda = false;
            mirandoDerecha = true;
            estado = Estado.CORRIENDO;
        }
    }

    public void moverIzquierda() {
        if (!atacando) {
            velocidadX = -(velocidadBase + mejoras.getBonusVelocidad());
            mirandoIzquierda = true;
            mirandoDerecha = false;
            estado = Estado.CORRIENDO;
        }
    }

    public void saltar() {
        float fuerzaSalto = fuerzaSaltoBase + mejoras.getBonusSalto();
        cuerpo.applyLinearImpulse(new Vector2(0, fuerzaSalto), cuerpo.getWorldCenter(), true);
        enElAire = true;
    }

    public void detener() {
        if (!atacando) {
            velocidadX = 0;
            estado = Estado.QUIETO;
        }
    }

    public void atacar(World mundo, boolean friendlyFire) {
        // Solo atacar si puede atacar y la tecla no estaba presionada antes
        if (puedeAtacar && !teclaPresionada) {
            teclaPresionada = true;
            atacando = true;
            puedeAtacar = false;
            tiempoTranscurridoAtaque = 0f;

            // Cambiar a animación de ataque
            estado = Estado.ATACANDO;
            animacionActual = Estado.ATACANDO.crearAnimacion();
            tiempoEstado = 0f;

            Vector2 posicionJugador = this.cuerpo.getPosition();

            // Área de ataque
            float anchoAreaAtaque = this.alcanceAtaque;
            float altoAreaAtaque = this.altoHitbox;

            float offsetX = (this.anchoHitbox / 2) + (anchoAreaAtaque / 2);
            float centroXAreaAtaque = posicionJugador.x + (offsetX * (mirandoDerecha ? 1 : -1));
            float centroYAreaAtaque = posicionJugador.y;

            float lowerX = centroXAreaAtaque - (anchoAreaAtaque / 2);
            float upperX = centroXAreaAtaque + (anchoAreaAtaque / 2);
            float lowerY = centroYAreaAtaque - (altoAreaAtaque / 2);
            float upperY = centroYAreaAtaque + (altoAreaAtaque / 2);

            int dañoActual = dañoBase + (int)mejoras.getBonusDaño();

            mundo.QueryAABB(fixture -> {
                Object userData = fixture.getBody().getUserData();

                // Ataque normal a enemigos
                if (userData instanceof Enemigo) {
                    Enemigo enemigo = (Enemigo) userData;
                    enemigo.recibirDaño(dañoActual);
                    return false;
                }

                // Fuego amigo: daño a otros jugadores
                if (friendlyFire && userData instanceof Jugador && userData != this) {
                    Jugador otroJugador = (Jugador) userData;
                    otroJugador.recibirDaño(dañoActual);
                    return false;
                }

                return true;
            }, lowerX, lowerY, upperX, upperY);
        }
    }

    public void resetearTeclaAtaque() {
        teclaPresionada = false;
    }

    public void setEnElAire(boolean valor) {
        enElAire = valor;
    }

    public float getAlcanceAtaque() {
        return this.alcanceAtaque;
    }

    public Body getCuerpo() {
        return this.cuerpo;
    }

    public int getVida() {
        return this.vida;
    }

    public int getDañoAtaque() {
        return dañoBase + (int)mejoras.getBonusDaño();
    }

    public int getVidaMaxima() {
        return this.vidaMaxima;
    }

    public boolean getEnElAire() {
        return this.enElAire;
    }

    public void setBarraVida(BarraVida barra1) {
        this.barraVida = barra1;
    }
}
