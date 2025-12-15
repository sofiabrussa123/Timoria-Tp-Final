package niveles;

import com.badlogic.gdx.Game;

import Red.HiloCliente;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import niveles.entorno.LlaveActivadora;
import niveles.entorno.Palanca;
import niveles.entorno.Plataforma;
import niveles.entorno.PlataformaMovil;
import niveles.entorno.PuertaLlegada;
import personajes.Enemigo;
import personajes.Jugador;

public class Nivel1 extends NivelBase {

    private boolean inicializado = false;

    public Nivel1(Game juego, HiloCliente hiloCliente) {
        super(juego, "FondoNivel1.jpeg");

        this.hiloCliente = hiloCliente;

        this.setFriendlyFire(true);

        super.jugador1 = new Jugador("Jugador1", 100, 85, 1,
            NivelBase.getMejorasJugador1(), hiloCliente);
        super.jugador2 = new Jugador("Jugador2", 120, 85, 2,
            NivelBase.getMejorasJugador2(), hiloCliente);
    }

    @Override
    public void show() {

        if (this.hiloCliente != null) {
            this.hiloCliente.setGameController(this);
        }

        super.show();

        if (idJugadorActivo == 1) {
            super.jugador1.setEsMiJugador(true);
            super.jugador2.setEsMiJugador(false);
        } else if (idJugadorActivo == 2) {
            super.jugador1.setEsMiJugador(false);
            super.jugador2.setEsMiJugador(true);
        }

        if (!inicializado) {
            inicializarNivel();
            inicializado = true;
        }
    }

    private void inicializarNivel() {

        int idPiso = 1;
        int idPlat2 = 2;
        int idPlat3 = 3;
        int idPalanca = 4;
        int idPlatMovil = 5;
        int idPuerta = 6;
        int idLlave = 7;
        int idEnemigo = 8;


        Plataforma piso = new Plataforma(0, 10, 1000, 50, idPiso);
        Plataforma plataforma2 = new Plataforma(495, 200, 75, 20, idPlat2);
        Plataforma plataforma3 = new Plataforma(80, 250, 95, 20, idPlat3);

        Palanca palanca = new Palanca(170, 120, idPalanca);
        PlataformaMovil plataformaMovil = new PlataformaMovil(200, 130, palanca, idPlatMovil);

        PuertaLlegada puerta = new PuertaLlegada(590, 50, idPuerta);
        LlaveActivadora llave = new LlaveActivadora(532, 210, puerta, idLlave);

        Enemigo enemigo = new Enemigo(400, 150, idEnemigo);

        añadirElemento(super.jugador1);
        añadirElemento(super.jugador2);
        añadirElemento(piso);
        añadirElemento(plataforma2);
        añadirElemento(plataforma3);
        añadirElemento(palanca);
        añadirElemento(plataformaMovil);
        añadirElemento(puerta);
        añadirElemento(llave);
        añadirElemento(enemigo);
    }

    @Override
    public void mostrarAtaqueEnemigo(int idEnemigo) {

    }

    @Override
    public void recogerItem(int idLlave, int idJugador) {
        try {

            Object entidad = entidades.get(idLlave);

            if (entidad instanceof LlaveActivadora) {
                LlaveActivadora llaveEncontrada = (LlaveActivadora) entidad;

                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        llaveEncontrada.remove();
                        entidades.remove(idLlave);
                    }
                });
            } else {
                System.err.println("⚠️ [CLIENTE] No se encontró la llave con ID " + idLlave);
                System.err.println("⚠️ [CLIENTE] Tipo de entidad: " + (entidad != null ? entidad.getClass().getName() : "null"));
            }

            // Agregar al inventario del jugador correspondiente
            final Jugador jugador = (idJugador == 1) ? jugador1 : jugador2;

            if (jugador != null && jugador.getBarraInventario() != null) {
                int slotLibre = jugador.getBarraInventario().getPrimeraCasillaLibre();

                if (slotLibre != -1) {
                    try {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Texture texturaLlave = new Texture(Gdx.files.internal("boton.png"));
                                    jugador.getBarraInventario().setIcono(slotLibre, texturaLlave);
                                } catch (Exception e) {
                                    System.err.println("❌ [CLIENTE] Error al cargar textura de llave: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        System.err.println("❌ [CLIENTE] Error al procesar textura: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                System.err.println("❌ [CLIENTE] Jugador " + idJugador + " o su inventario es null");
            }
        } catch (Exception e) {
            System.err.println("💥💥💥 [CLIENTE] ERROR CRÍTICO EN recogerItem:");
            System.err.println("💥 idLlave: " + idLlave);
            System.err.println("💥 idJugador: " + idJugador);
            System.err.println("💥 Error: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
}
