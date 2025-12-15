package personajes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import Red.HiloServidor;
import niveles.NivelBase;
import personajes.accesorios.BarraInventario;
import personajes.accesorios.MejoraTemporal;

public class Jugador {

    private int idJugador;
    private String nombre;
    private HiloServidor hiloServidor;

    private Body cuerpo;
    private float anchoHitbox = 0.4f;
    private float altoHitbox = 0.7f;
    private float velocidadBase = 5f;
    private float fuerzaSaltoBase = 7f;
    private float velocidadX = 0f;
    private boolean enElAire = false;
    private boolean mirandoDerecha = true;

    private int vida = 100;
    private int vidaMaxima = 100;
    private int dañoBase = 20;

    private float alcanceAtaque = 0.5f;
    private float tiempoTranscurridoAtaque = 0f;
    private float duracionAnimacionAtaque = 0.4f;
    private float cooldownAtaque = 0.5f;
    private boolean puedeAtacar = true;
    private boolean atacando = false;

    private MejoraTemporal mejoras;
    private BarraInventario barraInventario;

    public Jugador(World mundo, String nombre, int coordenadaXAparicion, int coordenadaYAparicion, int idJugador) {
        this.idJugador = idJugador;
        this.nombre = nombre;
        this.mejoras = new MejoraTemporal();

        aplicarMejoras();
        this.crearCuerpo(mundo, anchoHitbox, altoHitbox, coordenadaXAparicion, coordenadaYAparicion);
    }

    private void aplicarMejoras() {
        this.vidaMaxima = 100 + (int) mejoras.getBonusVida();
        this.vida = this.vidaMaxima;
    }

    public boolean getMirandoDerecha() {
        return this.mirandoDerecha;
    }

    public void setHiloServidor(HiloServidor hiloServidor) {
        this.hiloServidor = hiloServidor;
    }

    public void act(float delta) {

        if (!puedeAtacar) {
            tiempoTranscurridoAtaque += delta;
            if (tiempoTranscurridoAtaque >= cooldownAtaque) {
                puedeAtacar = true;
                tiempoTranscurridoAtaque = 0f;
            }
        }

        if (atacando) {
            tiempoTranscurridoAtaque += delta;
            if (tiempoTranscurridoAtaque >= duracionAnimacionAtaque) {
                atacando = false;
                tiempoTranscurridoAtaque = 0f;
            }
        }

        if (cuerpo != null) {
            cuerpo.setLinearVelocity(velocidadX, cuerpo.getLinearVelocity().y);
        }

        if (cuerpo != null) {
            Vector2 vel = cuerpo.getLinearVelocity();
            float maxVelX = 6f;
            float maxVelY = 15f;

            if (Math.abs(vel.x) > maxVelX) {
                vel.x = Math.signum(vel.x) * maxVelX;
            }
            if (Math.abs(vel.y) > maxVelY) {
                vel.y = Math.signum(vel.y) * maxVelY;
            }

            cuerpo.setLinearVelocity(vel);
        }
    }

    private void crearCuerpo(World mundo, float anchoHitbox, float altoHitbox, int coordenadaXAparicion, int coordenadaYAparicion) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
            coordenadaXAparicion * NivelBase.PIXELES_A_METROS,
            coordenadaYAparicion * NivelBase.PIXELES_A_METROS
        );
        bodyDef.fixedRotation = true;

        bodyDef.bullet = true;

        Body body = mundo.createBody(bodyDef);

        PolygonShape forma = new PolygonShape();
        forma.setAsBox(anchoHitbox / 2, altoHitbox / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = forma;
        fixtureDef.density = 3f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        forma.dispose();

        this.cuerpo = body;
        this.cuerpo.setUserData(Jugador.this);
    }

    public void recibirDaño(int cantidad) {
        if (cantidad <= 0 || vida <= 0) return;

        this.vida -= cantidad;
        if (vida < 0) vida = 0;

        if (vida == 0) {
            if (hiloServidor != null) {
                hiloServidor.enviarMensajeATodos("Jugador:" + this.idJugador + ":Matar");
            }
        }

        if (hiloServidor != null) {
            hiloServidor.enviarMensajeATodos("Jugador:" + this.idJugador + ":Dañar:" + this.vida);
        }
    }

    public void actualizarVidaConMejoras() {
        vidaMaxima = 100 + (int) mejoras.getBonusVida();
        vida = vidaMaxima;

        if (hiloServidor != null) {
            hiloServidor.enviarMensajeATodos("Jugador:" + this.idJugador + ":ActualizarVida:" + this.vida + ":" + this.vidaMaxima);
        }
    }

    public void moverDerecha() {
        if (!atacando) {
            velocidadX = velocidadBase + mejoras.getBonusVelocidad();
            mirandoDerecha = true;
        }
    }

    public void moverIzquierda() {
        if (!atacando) {
            velocidadX = -(velocidadBase + mejoras.getBonusVelocidad());
            mirandoDerecha = false;
        }
    }

    public void saltar() {
        if (!enElAire) {
            float fuerzaSalto = fuerzaSaltoBase + mejoras.getBonusSalto();
            cuerpo.applyLinearImpulse(new Vector2(0, fuerzaSalto), cuerpo.getWorldCenter(), true);
            enElAire = true;
        }
    }

    public void detener() {
        if (!atacando) {
            velocidadX = 0;
        }
    }

    public void atacar(World mundo, boolean friendlyFire, HiloServidor hiloServidor) {
        if (puedeAtacar) {
            atacando = true;
            puedeAtacar = false;
            tiempoTranscurridoAtaque = 0f;

            Vector2 posicionJugador = this.cuerpo.getPosition();

            float anchoAreaAtaque = this.alcanceAtaque;
            float altoAreaAtaque = this.altoHitbox;

            float offsetX = (this.anchoHitbox / 2) + (anchoAreaAtaque / 2);
            float centroXAreaAtaque = posicionJugador.x + (offsetX * (mirandoDerecha ? 1 : -1));
            float centroYAreaAtaque = posicionJugador.y;

            float lowerX = centroXAreaAtaque - (anchoAreaAtaque / 2);
            float upperX = centroXAreaAtaque + (anchoAreaAtaque / 2);
            float lowerY = centroYAreaAtaque - (altoAreaAtaque / 2);
            float upperY = centroYAreaAtaque + (altoAreaAtaque / 2);

            int dañoActual = dañoBase + (int) mejoras.getBonusDaño();

            mundo.QueryAABB(fixture -> {
                Object userData = fixture.getBody().getUserData();

                if (userData instanceof Enemigo) {
                    Enemigo enemigo = (Enemigo) userData;
                    int vidaAntes = enemigo.getVida();
                    enemigo.recibirDaño(dañoActual);
                    int vidaDespues = enemigo.getVida();

                    if (hiloServidor != null) {
                        hiloServidor.enviarMensajeATodos("Enemigo:" + enemigo.getID() + ":RecibirDaño:" + dañoActual);
                    }
                    return false;
                }

                if (friendlyFire && userData instanceof Jugador && userData != this) {
                    Jugador otroJugador = (Jugador) userData;
                    int vidaAntes = otroJugador.getVida();
                    otroJugador.recibirDaño(dañoActual);
                    int vidaDespues = otroJugador.getVida();

                    if (hiloServidor != null) {
                        hiloServidor.enviarMensajeATodos("Jugador:" + otroJugador.getIdJugador() + ":Dañar:" + vidaDespues);
                    }
                    return false;
                }

                return true;
            }, lowerX, lowerY, upperX, upperY);
        }
    }

    // Getters
    public int getIdJugador() {
        return this.idJugador;
    }

    public MejoraTemporal getMejoras() {
        return this.mejoras;
    }

    public Body getCuerpo() {
        return this.cuerpo;
    }

    public int getVida() {
        return this.vida;
    }

    public int getDañoAtaque() {
        return dañoBase + (int) mejoras.getBonusDaño();
    }

    public int getVidaMaxima() {
        return this.vidaMaxima;
    }

    public boolean getEnElAire() {
        return this.enElAire;
    }

    public void setEnElAire(boolean valor) {
        enElAire = valor;
    }

    public BarraInventario getBarraInventario() {
        return this.barraInventario;
    }

    public float getAlto(){
        return this.altoHitbox;
    }

    public float getAncho(){
        return this.anchoHitbox;
    }

    public BarraInventario getInventario() {
        return barraInventario;
    }
}
