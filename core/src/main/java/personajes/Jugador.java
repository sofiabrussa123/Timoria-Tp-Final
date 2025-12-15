package personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import Red.HiloCliente;
import interfaces.IdManager;
import niveles.NivelBase;
import niveles.entorno.BarraInventario;
import niveles.entorno.BarraVida;
import personajes.movimientos.Estado;

public class Jugador extends Actor implements IdManager{

    private int id;
    private String nombre;
    private MejoraTemporal mejoras;
    private HiloCliente hiloCliente;

    private BarraVida barraVida;
    private BarraInventario barraInventario;

    private float anchoHitbox = 0.4f;
    private float altoHitbox = 0.7f;
    private float velocidadBase = 5f;
    private float fuerzaSaltoBase = 7f;
    private float velocidadX = 0f;
    private boolean enElAire = false;

    private int vida = 100;
    private int vidaMaxima = 100;
    private int dañoBase = 20;
    private Sound sonidoDaño = Gdx.audio.newSound(Gdx.files.internal("Daño.mp3"));
    private long tiempoUltimoDaño = 0L;
    private boolean sonidoReproduciéndose = false;

    private boolean esMiJugador = false;

    private Animation<TextureRegion> animacionActual;
    private Estado estado = Estado.QUIETO;
    private float tiempoEstado = 0f;
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

    public Jugador(String nombre, int coordenadaXAparicion, int coordenadaYAparicion, int id, MejoraTemporal mejoras, HiloCliente hiloCliente) {
        this.nombre = nombre;
        this.id = id;
        this.nombre = nombre;
        this.mejoras = mejoras;
        this.barraVida = new BarraVida(this, true);
        this.hiloCliente = hiloCliente;

        aplicarMejoras();

        TextureRegion primerFrame = (TextureRegion) estado.QUIETO.crearAnimacion().getKeyFrame(0.0F);

        float anchoPersonajePx = primerFrame.getRegionWidth();
        float altoPersonajePx = primerFrame.getRegionHeight();

        this.setSize(anchoPersonajePx, altoPersonajePx);

        setPosition(coordenadaXAparicion, coordenadaYAparicion);

        try {
            this.sonidoDaño = Gdx.audio.newSound(Gdx.files.internal("Daño.mp3"));
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo cargar Daño.mp3: " + e.getMessage());
        }

        this.animacionActual = estado.QUIETO.crearAnimacion();
    }

    private void aplicarMejoras() {
        this.vidaMaxima = 100 + (int) mejoras.getBonusVida();
        this.vida = this.vidaMaxima;
    }

    public void actualizarPosicion(float x, float y) {
        setPosition(x, y);
    }

    @Override
    public void act(float delta) {
        tiempoEstado += delta;

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

        if (!atacando) {
            animacionActual = estado.crearAnimacion();
        }

        if (this.sonidoReproduciéndose) {
            long ahora = System.currentTimeMillis();
            if (ahora - this.tiempoUltimoDaño >= 1000L) {
                if (sonidoDaño != null) {
                    sonidoDaño.stop();
                }
                this.sonidoReproduciéndose = false;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frameActual = animacionActual.getKeyFrame(tiempoEstado, true);

        if (mirandoIzquierda && !frameActual.isFlipX()) {
            frameActual.flip(true, false);
        }
        else if (mirandoDerecha && frameActual.isFlipX()) {
            frameActual.flip(true, false);
        }

        if(!muerto) {
            batch.draw(
                frameActual,
                getX(),
                getY(),
                getWidth(),
                getHeight()
            );
        }
    }

    // ✅ SOLO predicción local, SIN enviar mensajes de red
    public void moverDerecha() {
        if (!atacando) {
            mirandoIzquierda = false;
            mirandoDerecha = true;
            estado = Estado.CORRIENDO;
        }
    }

    public void moverIzquierda() {
        if (!atacando) {
            mirandoIzquierda = true;
            mirandoDerecha = false;
            estado = Estado.CORRIENDO;
        }
    }

    public void detener() {
        if (!atacando) {
            estado = Estado.QUIETO;
        }
    }

    public void atacar(boolean friendlyFire) {
        if (puedeAtacar && !teclaPresionada) {
            teclaPresionada = true;
            atacando = true;
            puedeAtacar = false;
            tiempoTranscurridoAtaque = 0f;
            estado = Estado.ATACANDO;
            animacionActual = Estado.ATACANDO.crearAnimacion();
            tiempoEstado = 0f;
        }
    }

    public void resetearTeclaAtaque() {
        teclaPresionada = false;
    }

    public void recibirDaño(int cantidad) {
        if (cantidad <= 0 || vida <= 0) return;

        this.vida -= cantidad;
        if (vida < 0) vida = 0;

        if (esMiJugador && sonidoDaño != null) {
            if (!sonidoReproduciéndose) {
                try {
                    sonidoDaño.play(0.5f);
                    sonidoReproduciéndose = true;
                    tiempoUltimoDaño = System.currentTimeMillis();
                } catch (Exception e) {
                    System.err.println("⚠️ Error al reproducir sonido: " + e.getMessage());
                }
            }
        }

        if (vida == 0) {
            if (esMiJugador && sonidoDaño != null) {
                sonidoDaño.stop();
                sonidoReproduciéndose = false;
            }
            muerto = true;
        }

        if (barraVida != null) {
            barraVida.actualizarVida();
        }
    }

    public void setEsMiJugador(boolean esMiJugador) {
        this.esMiJugador = esMiJugador;
        System.out.println("🎮 Jugador " + this.id + " - esMiJugador: " + esMiJugador);
    }

    public void actualizarVidaConMejoras() {
        vidaMaxima = 100 + (int) mejoras.getBonusVida();
        vida = vidaMaxima;

        if (barraVida != null) {
            barraVida.actualizarVida();
        }
    }

    public void setFrameAnimacion(int numFrame, boolean mirandoDerecha) {
        this.mirandoIzquierda = !mirandoDerecha;
        this.mirandoDerecha = mirandoDerecha;

        if (this.estado == Estado.ATACANDO) {
            this.atacando = true;
            this.tiempoTranscurridoAtaque = 0f;
            this.animacionActual = Estado.ATACANDO.crearAnimacion();
            this.tiempoEstado = 0f;
        }
    }

    public void reproducirSonidoDaño() {
        if (sonidoDaño != null && !sonidoReproduciéndose) {
            try {
                sonidoDaño.play(0.5f);
                sonidoReproduciéndose = true;
                tiempoUltimoDaño = System.currentTimeMillis();
            } catch (Exception e) {
                System.err.println("⚠️ Error al reproducir sonido: " + e.getMessage());
            }
        }
    }

    public void setVida(int nuevaVida) {
        this.vida = nuevaVida;

        if (vida <= 0) {
            vida = 0;
            muerto = true;

            if (sonidoDaño != null) {
                sonidoDaño.stop();
                sonidoReproduciéndose = false;
            }
        }

        if (barraVida != null) {
            barraVida.actualizarVida();
        }
    }

    public void morir() {
        super.setVisible(false);
        this.muerto = true;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setEnElAire(boolean valor) {
        enElAire = valor;
    }

    public int getVida() {
        return this.vida;
    }

    public MejoraTemporal getMejoras() {
        return this.mejoras;
    }

    public BarraInventario getBarraInventario() {
        return this.barraInventario;
    }

    public float getAlcanceAtaque() {
        return this.alcanceAtaque;
    }

    public int getDañoAtaque() {
        return dañoBase + (int) mejoras.getBonusDaño();
    }

    public BarraVida getBarraVida() {
        return this.barraVida;
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

    public void setBarraInventario(BarraInventario barraInventario) {
        this.barraInventario = barraInventario;
    }

    public boolean getMirandoDerecha() {
        return this.mirandoDerecha;
    }
}
