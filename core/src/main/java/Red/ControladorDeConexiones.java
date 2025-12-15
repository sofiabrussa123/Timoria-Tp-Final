package Red;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.badlogic.gdx.utils.Timer;
import interfaces.ConectionManager;
import niveles.EscenaBase;
import niveles.Nivel1;
import pantallas.Menu;
import pantallas.PantallaGanaste;
import pantallas.SecuenciaImagenes;

public class ControladorDeConexiones extends EscenaBase implements ConectionManager {

    private HiloCliente hiloCliente;
    private Label lblEstado;
    private Label lblTuId;
    private Label lblMensaje;
    private int miId = -1;
    private float cooldown = 0.5f;
    private float tiempoTranscurrido = 0f;
    private Nivel1 primerNivel;

    public ControladorDeConexiones(Game juego) {
        super(juego, "FondoTransparente.png");
        this.hiloCliente = new HiloCliente(this, juego);
        this.hiloCliente.start();
        primerNivel = new Nivel1(juego, this.hiloCliente);
        ControladorDeConexiones.this.hiloCliente.setGameController(primerNivel);

        super.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));

        lblEstado = new Label("Conectando con el servidor...", super.fuenteTextos);
        lblEstado.setFontScale(1f);
        lblEstado.setColor(Color.WHITE);

        lblTuId = new Label("Esperando ID...", super.fuenteTextos);
        lblTuId.setFontScale(1f);
        lblTuId.setColor(Color.LIGHT_GRAY);

        lblMensaje = new Label("Esperando...", super.fuenteTextos);
        lblMensaje.setFontScale(1.2f);
        lblMensaje.setColor(Color.LIGHT_GRAY);

        this.hiloCliente.enviarMensaje("Conectar");

        Label titulo = new Label("ESPERANDO A UN SEGUNDO JUGADOR", super.fuenteTextos);
        titulo.setFontScale(2f);
        titulo.setColor(Color.YELLOW);

        TextButton btnCancelar = new TextButton("Cancelar", super.fuenteTextos);
        btnCancelar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (hiloCliente != null) {
                    hiloCliente.terminar();
                }
                cambiarEscena(new Menu(juego));
            }
        });

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();

        tabla.add(titulo).padBottom(40).row();
        tabla.add(lblEstado).padBottom(20).row();
        tabla.add(lblTuId).padBottom(30).row();
        tabla.add(lblMensaje).padBottom(30).row();
        tabla.add(btnCancelar).width(200).height(50);

        super.escena.addActor(tabla);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        tiempoTranscurrido += delta;

        if(miId != -1) {
            lblTuId.setText("Eres el Jugador: " + miId);
            lblTuId.setColor(miId == 1 ? Color.CYAN : Color.ORANGE);

            lblEstado.setText("Conectado. Esperando al Jugador " + (miId == 1 ? 2 : 1) + "...");
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(escena);
    }

    @Override
    public void asignarId(int id) {
        this.miId = id;
        primerNivel.conectar(miId);
    }

    @Override
    public void empezarHistoria() {
        String[] imagenesIntro = {
            "1.png",
            "2.png",
            "3.png",
            "4.png",
            "5.png"
        };

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    SecuenciaImagenes secuenciaInicio = new SecuenciaImagenes(juego, imagenesIntro);
                    secuenciaInicio.setHiloCliente(hiloCliente);
                    cambiarEscena(secuenciaInicio);
                } catch (Exception e) {
                    System.err.println("❌ Error al iniciar SecuenciaImagenes: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void empezarJuego() {

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    cambiarEscena(primerNivel);
                } catch (Exception e) {
                    System.err.println("❌ Error al iniciar el nivel: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void reiniciarNivel() {

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Nivel1 nuevoNivel = new Nivel1(juego, hiloCliente);
                    nuevoNivel.conectar(miId);
                    juego.setScreen(nuevoNivel);
                } catch (Exception e) {
                    System.err.println("❌ Error al reiniciar nivel: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void servidorDesconectado() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                lblEstado.setText("❌ SERVIDOR DESCONECTADO");
                lblEstado.setColor(Color.RED);
                lblMensaje.setText("El servidor se ha cerrado. Volviendo al menú...");

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        if (hiloCliente != null) {
                            hiloCliente.terminar();
                        }
                        cambiarEscena(new Menu(juego));
                    }
                }, 3);
            }
        });
    }

    public void cambiarAPantallaGanaste() {
        juego.setScreen(new PantallaGanaste(juego));
    }

    public HiloCliente getHiloCliente() {
        return hiloCliente;
    }
}
