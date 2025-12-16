package niveles;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import Red.HiloCliente;
import globales.EsceneManager;
import interfaces.GameController;
import interfaces.IdManager;
import pantallas.Menu;
import pantallas.MenuPausa;
import pantallas.PantallaDeMuerte;
import pantallas.PantallaGanaste;
import personajes.accesorios.BarraInventario;
import personajes.accesorios.BarraVida;
import niveles.entorno.LlaveActivadora;
import niveles.entorno.Palanca;
import niveles.entorno.Plataforma;
import niveles.entorno.PlataformaMovil;
import niveles.entorno.PuertaLlegada;
import personajes.Enemigo;
import personajes.Jugador;
import personajes.accesorios.MejoraTemporal;
import personajes.accesorios.Estado;

public abstract class NivelBase extends EscenaBase implements GameController{

    public static final float PIXELES_A_METROS = 1 / 100f;

    protected final int anchoPantalla = 800;
    protected final int altoPantalla = 800;

    protected static MejoraTemporal mejorasJugador1 = new MejoraTemporal();
    protected static MejoraTemporal mejorasJugador2 = new MejoraTemporal();

    protected ExtendViewport viewport;
    protected float anchoViewport;
    protected float altoViewport;
    private boolean juegoPausado = false;
    protected Screen pantallaRetorno;
    protected Jugador jugador1;
    protected Jugador jugador2;
    protected Jugador personaje;
    protected Map<Integer, Actor> entidades = new HashMap<>();
    protected HiloCliente hiloCliente;
    protected int cantEntidades = 0;
    protected int idJugadorActivo;

    private Queue<Runnable> accionesBox2DPendientes = new Queue<Runnable>();

    private String estadoAnteriorJ1 = "";
    private String estadoAnteriorJ2 = "";
    private boolean teclaWSaltoPrevioJ1 = false;
    private boolean teclaWSaltoPrevioJ2 = false;
    private boolean teclaUpSaltoPrevioJ2 = false;

    public NivelBase(Game juego, String fondo) {
        super(juego, fondo);

        this.viewport = new ExtendViewport(anchoPantalla, altoPantalla);
        this.anchoViewport = anchoPantalla * PIXELES_A_METROS;
        this.altoViewport = altoPantalla * PIXELES_A_METROS;
    }

    public void encolarAccionBox2D(Runnable accion) {
        accionesBox2DPendientes.addLast(accion);
    }

    private void procesarAccionesBox2DPendientes() {
        while (accionesBox2DPendientes.size > 0) {
            try {
                accionesBox2DPendientes.removeFirst().run();
            } catch (Exception e) {
                System.err.println("❌ Error Box2D: " + e.getMessage());
            }
        }
    }

    public static MejoraTemporal getMejorasJugador1() {
        return mejorasJugador1;
    }

    public static MejoraTemporal getMejorasJugador2() {
        return mejorasJugador2;
    }

    public Jugador getJugador1() {
        return this.jugador1;
    }

    public Jugador getJugador2() {
        return this.jugador2;
    }

    public void setPersonaje(Jugador personaje) {
        this.personaje = personaje;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.inputManager);

        if (this.jugador1 != null) {
            BarraVida barra1 = new BarraVida(this.jugador1, true);
            this.jugador1.setBarraVida(barra1);
            this.escena.addActor(barra1);

            BarraInventario inventario1 = new BarraInventario(this.jugador1, true);
            this.jugador1.setBarraInventario(inventario1);
            this.escena.addActor(inventario1);
        }

        if (this.jugador2 != null) {
            BarraVida barra2 = new BarraVida(this.jugador2, false);
            this.jugador2.setBarraVida(barra2);
            this.escena.addActor(barra2);

            BarraInventario inventario2 = new BarraInventario(this.jugador2, false);
            this.jugador2.setBarraInventario(inventario2);
            this.escena.addActor(inventario2);
        }
    }

    @Override
    public void render(float delta) {
        if (this.inputManager.getIsEscPressed() || this.inputManager.getIsPPressed()) {
            this.juegoPausado = !this.juegoPausado;
            if (this.juegoPausado) {
                EsceneManager.setEscenaActual(this);
                this.cambiarEscena(new MenuPausa(this.juego));
            }
        }

        aplicarControlesOptimizados();

        if (this.jugador1 != null && this.jugador1.getVida() == 0) {
            this.cambiarEscena(new PantallaDeMuerte(this.juego, 1, hiloCliente));
        }

        if (this.jugador2 != null && this.jugador2.getVida() == 0) {
            this.cambiarEscena(new PantallaDeMuerte(this.juego, 2, hiloCliente));
        }

        super.render(delta);

        procesarAccionesBox2DPendientes();
    }

    private void aplicarControlesOptimizados() {
        if (idJugadorActivo == 1 && this.jugador1 != null) {
            String estadoActual = "";

            if (this.inputManager.getIsAPressed()) {
                estadoActual = "MoverIzquierda";
                if (!estadoAnteriorJ1.equals(estadoActual)) {
                    jugador1.setEstadoAnimacion(Estado.CORRIENDO);
                    jugador1.setDireccion(false); // Mirando izquierda

                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:1:MoverIzquierda");
                    }
                }
            } else if (this.inputManager.getIsDPressed()) {
                estadoActual = "MoverDerecha";
                if (!estadoAnteriorJ1.equals(estadoActual)) {
                    jugador1.setEstadoAnimacion(Estado.CORRIENDO);
                    jugador1.setDireccion(true); // Mirando derecha

                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:1:MoverDerecha");
                    }
                }
            } else {
                estadoActual = "Detener";
                if (!estadoAnteriorJ1.equals(estadoActual)) {
                    jugador1.setEstadoAnimacion(Estado.QUIETO);

                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:1:Detener");
                    }
                }
            }

            estadoAnteriorJ1 = estadoActual;

            if (this.inputManager.getIsWPressed()) {
                if (!teclaWSaltoPrevioJ1 && !this.jugador1.getEnElAire()) {
                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:1:Saltar");
                    }
                }
                teclaWSaltoPrevioJ1 = true;
            } else {
                teclaWSaltoPrevioJ1 = false;
            }

            if (this.inputManager.getIsOPressed()) {
                if (hiloCliente != null) {
                    hiloCliente.enviarMensaje("Input:1:Atacar");
                }
            } else {
                jugador1.resetearTeclaAtaque();
            }

        } else if (idJugadorActivo == 2 && this.jugador2 != null) {
            String estadoActual = "";

            if (this.inputManager.getIsLeftPressed()) {
                estadoActual = "MoverIzquierda";
                if (!estadoAnteriorJ2.equals(estadoActual)) {
                    jugador2.setEstadoAnimacion(Estado.CORRIENDO);
                    jugador2.setDireccion(false);

                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:2:MoverIzquierda");
                    }
                }
            } else if (this.inputManager.getIsRightPressed()) {
                estadoActual = "MoverDerecha";
                if (!estadoAnteriorJ2.equals(estadoActual)) {
                    jugador2.setEstadoAnimacion(Estado.CORRIENDO);
                    jugador2.setDireccion(true);

                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:2:MoverDerecha");
                    }
                }
            } else {
                estadoActual = "Detener";
                if (!estadoAnteriorJ2.equals(estadoActual)) {
                    jugador2.setEstadoAnimacion(Estado.QUIETO);

                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:2:Detener");
                    }
                }
            }

            estadoAnteriorJ2 = estadoActual;

            if (this.inputManager.getIsUpPressed()) {
                if (!teclaUpSaltoPrevioJ2 && !this.jugador2.getEnElAire()) {
                    if (hiloCliente != null) {
                        hiloCliente.enviarMensaje("Input:2:Saltar");
                    }
                }
                teclaUpSaltoPrevioJ2 = true;
            } else {
                teclaUpSaltoPrevioJ2 = false;
            }

            if (this.inputManager.getIsEPressed()) {
                if (hiloCliente != null) {
                    hiloCliente.enviarMensaje("Input:2:Atacar");
                }
            } else {
                jugador2.resetearTeclaAtaque();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void draw(float delta) {
        super.render(delta);
    }

    public void despausar() {
        this.juegoPausado = false;
        this.inputManager.resetPauseKeys();
        Gdx.input.setInputProcessor(this.inputManager);
    }

    protected void añadirElemento(IdManager elemento) {
        this.escena.addActor((Actor)elemento);
        entidades.put(elemento.getId(), (Actor)elemento);
    }

    public void setHiloCliente(HiloCliente hiloCliente) {
        this.hiloCliente = hiloCliente;
    }

    @Override
    public void recogerItem(int idLlave, int idJugador) {
        Actor entidad = this.entidades.get(idLlave);
        if (entidad instanceof LlaveActivadora) {
            LlaveActivadora llave = (LlaveActivadora) entidad;
            Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;
            if (jugador != null) {
                llave.activarConJugador(jugador);
            }
        }
    }

    @Override
    public void moverPlataformaMovil(int id, float posX, float posY) {

        final Actor entidad = this.entidades.get(id);

        if (entidad instanceof PlataformaMovil) {

            encolarAccionBox2D(new Runnable() {
                @Override
                public void run() {
                    ((PlataformaMovil) entidad).moverDesdeServidor(posX, posY);
                }
            });
        } else {
            System.err.println("❌ [CLIENTE] PlataformaMovil ID " + id + " NO encontrada. Entidad: " + (entidad != null ? entidad.getClass().getName() : "null"));
            System.err.println("📋 [CLIENTE] IDs disponibles: " + entidades.keySet());
        }
    }

    @Override
    public void abrirPuerta(int id) {
        Actor entidad = this.entidades.get(id);
        if (entidad instanceof PuertaLlegada) {
            ((PuertaLlegada) entidad).desbloquear();
        }
    }

    @Override
    public void actualizarPosicionJugador(int id, float posX, float posY, boolean mirandoDerecha) {
        Jugador jugador = (id == 1) ? this.jugador1 : this.jugador2;
        if (jugador == null) return;

        encolarAccionBox2D(new Runnable() {
            @Override
            public void run() {
                jugador.actualizarPosicion(posX, posY, mirandoDerecha);
            }
        });
    }

    public void renderFondoPausado(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        escena.getViewport().apply();
        escena.draw();
    }

    @Override
    public void actualizarPosicionEnemigo(int id, float posX, float posY) {
        final Actor entidad = this.entidades.get(id);
        if (entidad instanceof Enemigo) {
            encolarAccionBox2D(new Runnable() {
                @Override
                public void run() {
                    Enemigo enemigo = (Enemigo) entidad;
                    enemigo.actualizarPosicion(posX, posY);
                }
            });
        }
    }

    @Override
    public void servidorDesconectado() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                volverAlMenu();
            }
        });
    }

    @Override
    public void matarJugador(int id) {
        Jugador jugador = (id == 1) ? this.jugador1 : this.jugador2;
        if (jugador != null) {
            jugador.morir();
        }
    }

    @Override
    public void dañarJugador(int idJugador, int nuevaVida) {
        Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;
        if (jugador != null) {
            int vidaAnterior = jugador.getVida();
            jugador.setVida(nuevaVida);

            if (idJugador == idJugadorActivo && nuevaVida < vidaAnterior) {
                jugador.reproducirSonidoDaño();
            }
        }
    }

    @Override
    public void mostrarAtaqueEnemigo(int idEnemigo) {
        final Actor entidad = this.entidades.get(idEnemigo);

        if (entidad instanceof Enemigo) {
            final Enemigo enemigo = (Enemigo) entidad;

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    enemigo.mostrarAnimacionAtaque();
                }
            });
        }
    }

    @Override
    public void mostrarDañoEnemigo(int idEnemigo) {
        final Actor entidad = this.entidades.get(idEnemigo);

        if (entidad instanceof Enemigo) {
            final Enemigo enemigo = (Enemigo) entidad;

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    enemigo.mostrarDañoRecibido();
                }
            });
        }
    }

    @Override
    public void procesarAccionesEnemigo(String[] mensaje, int idEnemigo) {
        // Manejado
    }

    @Override
    public void desaparecerEnemigo(int id) {
        final Actor entidad = this.entidades.get(id);

        if (entidad instanceof Enemigo) {
            final Enemigo enemigo = (Enemigo) entidad;

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    try {
                        enemigo.eliminar();
                        enemigo.dispose();
                        entidades.remove(id);
                    } catch (Exception e) {
                        System.err.println("❌ [CLIENTE] Error al eliminar enemigo: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void activarPalanca(int idPalanca) {
        Actor entidad = this.entidades.get(idPalanca);
        if (entidad instanceof Palanca) {
            ((Palanca) entidad).activar();
        } else {
            System.err.println("❌ [CLIENTE] No se encontró palanca con ID: " + idPalanca);
        }
    }

    @Override
    public void aplicarMejora(int idJugador, String tipoMejora) {
        Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;
        MejoraTemporal mejoras = (idJugador == 1) ? mejorasJugador1 : mejorasJugador2;

        if (jugador == null || mejoras == null) return;

        switch(tipoMejora) {
            case "Vida":
                mejoras.mejorarVida();
                jugador.actualizarVidaConMejoras();
                break;
            case "Velocidad":
                mejoras.mejorarVelocidad();
                break;
            case "Salto":
                mejoras.mejorarSalto();
                break;
            case "Daño":
                mejoras.mejorarDaño();
                break;
        }
    }

    @Override
    public void mostrarAtaqueJugador(int idJugador) {
        Jugador jugador = (idJugador == 1) ? this.jugador1 : this.jugador2;
        if (jugador != null) {
            jugador.setFrameAnimacion(0, jugador.getMirandoDerecha());
        }
    }

    @Override
    public void conectar(int idJugador) {
        this.idJugadorActivo = idJugador;

        if (jugador1 != null && jugador2 != null) {
            jugador1.setEsMiJugador(idJugador == 1);
            jugador2.setEsMiJugador(idJugador == 2);
        }
    }

    @Override
    public void volverAlMenu() {
        if (this.hiloCliente != null) {
            hiloCliente.enviarMensaje("VolverAlMenu");
        }
        cambiarEscena(new Menu(this.juego));
    }

    protected void setFriendlyFire(boolean b) {
    }
}
