package interfaces;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import niveles.*;
import personajes.MejoraTemporal;

public class PantallaDeMuerte extends EscenaBase {

    private Music musicaMuerte;
    private boolean musicaMuerteActiva = true;

    private final String[] frases = {
        "Las sombras te han tomado. ¿Renacerás?",
        "El ciclo se repite, mientras tu alma vaga sin paz.",
        "El velo entre mundos se cierra… vuelve a intentarlo.",
        "Caíste, pero el vacío aún te llama.",
        "Tu viaje ha terminado, pero tu alma aún busca redención.",
        "Has cruzado el umbral… pero no estabas listo.",
        "Un susurro apagó tu llama."
    };

    private String fraseElegida;
    private int idJugadorMuerto;
    private MejoraTemporal mejorasJugador;

    public PantallaDeMuerte(Game principal, int idJugadorMuerto) {
        super(principal, "PantallaDeMuerte.png");

        super.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));

        this.fraseElegida = frases[new Random().nextInt(frases.length)];
        this.idJugadorMuerto = idJugadorMuerto;

        if (idJugadorMuerto == 1) {
            this.mejorasJugador = NivelBase.getMejorasJugador1();
        } else {
            this.mejorasJugador = NivelBase.getMejorasJugador2();
        }

        Label frase = new Label(fraseElegida, super.fuenteTextos);
        frase.setAlignment(Align.center);
        frase.setFontScale(1.2f);
        frase.setWrap(true);
        frase.setWidth(600);

        this.musicaMuerte = Gdx.audio.newMusic(Gdx.files.internal("Muerte.mp3"));
        this.musicaMuerte.setLooping(true);

        if (this.musicaMuerteActiva) this.musicaMuerte.play();

        Label tituloJugador = new Label("=== JUGADOR " + idJugadorMuerto + " - ELIGE UNA MEJORA ===", super.fuenteTextos);
        tituloJugador.setAlignment(Align.center);
        tituloJugador.setColor(Color.YELLOW);
        tituloJugador.setFontScale(1.1f);

        TextButton botonMejorarVida = crearBotonMejora("Mejorar Vida (+20)",
            mejorasJugador.getMejorasVida(), mejorasJugador.getMaxMejoras());

        TextButton botonMejorarVelocidad = crearBotonMejora("Mejorar Velocidad (+1)",
            mejorasJugador.getMejorasVelocidad(), mejorasJugador.getMaxMejoras());

        TextButton botonMejorarSalto = crearBotonMejora("Mejorar Salto (+1.5)",
            mejorasJugador.getMejorasSalto(), mejorasJugador.getMaxMejoras());

        TextButton botonMejorarDaño = crearBotonMejora("Mejorar Daño (+10)",
            mejorasJugador.getMejorasDaño(), mejorasJugador.getMaxMejoras());

        botonMejorarVida.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!botonMejorarVida.isDisabled() && mejorasJugador.mejorarVida()) {
                    actualizarBotonMejora(botonMejorarVida, "Mejorar Vida (+20)",
                        mejorasJugador.getMejorasVida(), mejorasJugador.getMaxMejoras());
                }
            }
        });

        botonMejorarVelocidad.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!botonMejorarVelocidad.isDisabled() && mejorasJugador.mejorarVelocidad()) {
                    actualizarBotonMejora(botonMejorarVelocidad, "Mejorar Velocidad (+1)",
                        mejorasJugador.getMejorasVelocidad(), mejorasJugador.getMaxMejoras());
                }
            }
        });

        botonMejorarSalto.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!botonMejorarSalto.isDisabled() && mejorasJugador.mejorarSalto()) {
                    actualizarBotonMejora(botonMejorarSalto, "Mejorar Salto (+1.5)",
                        mejorasJugador.getMejorasSalto(), mejorasJugador.getMaxMejoras());
                }
            }
        });

        botonMejorarDaño.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!botonMejorarDaño.isDisabled() && mejorasJugador.mejorarDaño()) {
                    actualizarBotonMejora(botonMejorarDaño, "Mejorar Daño (+10)",
                        mejorasJugador.getMejorasDaño(), mejorasJugador.getMaxMejoras());
                }
            }
        });

        TextButton botonVolver = new TextButton("Volver a jugar", super.fuenteTextos);
        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PantallaDeMuerte.this.musicaMuerte.pause();
                cambiarEscena(new Nivel1(juego));
            }
        });

        TextButton botonMenu = new TextButton("Menú principal", super.fuenteTextos);
        botonMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PantallaDeMuerte.this.musicaMuerte.pause();
                cambiarEscena(new Menu(juego));
            }
        });

        // Botón música
        TextButton botonMusica = new TextButton("Silenciar música", super.fuenteTextos);
        botonMusica.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PantallaDeMuerte.this.musicaMuerteActiva = !PantallaDeMuerte.this.musicaMuerteActiva;
                if (PantallaDeMuerte.this.musicaMuerteActiva) {
                    PantallaDeMuerte.this.musicaMuerte.play();
                    botonMusica.setText("Silenciar música");
                } else {
                    PantallaDeMuerte.this.musicaMuerte.pause();
                    botonMusica.setText("Reactivar música");
                }
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.padTop(30);

        table.add(tituloJugador).padBottom(15).row();
        table.add(botonMejorarVida).width(250).padBottom(8).row();
        table.add(botonMejorarVelocidad).width(250).padBottom(8).row();
        table.add(botonMejorarSalto).width(250).padBottom(8).row();
        table.add(botonMejorarDaño).width(250).padBottom(20).row();
        table.add(frase).width(600).padBottom(25).row();
        table.add(botonVolver).width(200).padBottom(10).row();
        table.add(botonMusica).width(200).padBottom(10).row();
        table.add(botonMenu).width(200);

        super.escena.addActor(table);
    }
    private TextButton crearBotonMejora(String nombre, int nivel, int max) {
        String texto = String.format("%s [%d/%d]", nombre, nivel, max);
        TextButton boton = new TextButton(texto, super.fuenteTextos);

        if (nivel >= max) {
            boton.setDisabled(true);
            boton.setColor(Color.GRAY);
        }

        return boton;
    }

    private void actualizarBotonMejora(TextButton boton, String nombre, int nivel, int max) {
        boton.setText(String.format("%s [%d/%d]", nombre, nivel, max));
        if (nivel >= max) {
            boton.setDisabled(true);
            boton.setColor(Color.GRAY);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(escena);
    }
}
