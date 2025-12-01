package interfaces;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.timoria.Principal;
import niveles.*;
import personajes.*;

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
    private Personaje jugador;
    private int idJugadorMuerto;
    private MejoraTemporal mejorasJugador1;
    private MejoraTemporal mejorasJugador2;

    private Principal principal;

    public PantallaDeMuerte(Game principal, Personaje jugadorMuerto, NivelBase nivelAnterior) {
        super(principal, "PantallaDeMuerte.png");
        this.jugador = jugadorMuerto;
        this.idJugadorMuerto = jugadorMuerto.getIdJugador();

        // Guardar las mejoras de ambos jugadores
        if (nivelAnterior.getJugador1() != null) {
            this.mejorasJugador1 = copiarMejoras(nivelAnterior.getJugador1().getMejoras());
        }
        if (nivelAnterior.getJugador2() != null) {
            this.mejorasJugador2 = copiarMejoras(nivelAnterior.getJugador2().getMejoras());
        }

        // Botones de mejoras
        TextButton btnMejorarVida = new TextButton("", super.fuenteTextos);
        TextButton btnMejorarVelocidad = new TextButton("", super.fuenteTextos);
        TextButton btnMejorarSalto = new TextButton("", super.fuenteTextos);

        actualizarBotones(btnMejorarVida, btnMejorarVelocidad, btnMejorarSalto);

        // Listeners de mejora
        btnMejorarVida.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (jugador.getMejoras().mejorarVida()) {
                    jugador.actualizarVidaConMejoras();
                    actualizarMejorasGuardadas();
                    actualizarBotones(btnMejorarVida, btnMejorarVelocidad, btnMejorarSalto);
                }
            }
        });

        btnMejorarVelocidad.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (jugador.getMejoras().mejorarVelocidad()) {
                    actualizarMejorasGuardadas();
                    actualizarBotones(btnMejorarVida, btnMejorarVelocidad, btnMejorarSalto);
                }
            }
        });

        btnMejorarSalto.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (jugador.getMejoras().mejorarSalto()) {
                    actualizarMejorasGuardadas();
                    actualizarBotones(btnMejorarVida, btnMejorarVelocidad, btnMejorarSalto);
                }
            }
        });

        super.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));

        this.fraseElegida = frases[new Random().nextInt(frases.length)];

        Label frase = new Label(fraseElegida, super.fuenteTextos);
        frase.setAlignment(Align.center);
        frase.setFontScale(1.2f);
        frase.setWrap(true);
        frase.setWidth(600);

        this.musicaMuerte = Gdx.audio.newMusic(Gdx.files.internal("Muerte.mp3"));
        this.musicaMuerte.setLooping(true);

        if (this.musicaMuerteActiva) this.musicaMuerte.play();

        // Botón volver
        TextButton botonVolver = new TextButton("Volver a jugar", super.fuenteTextos);
        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PantallaDeMuerte.this.musicaMuerte.pause();
                // Pasar las mejoras guardadas al nuevo nivel
                cambiarEscena(new Nivel1(juego, mejorasJugador1, mejorasJugador2));
            }
        });

        // Botón menú principal
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

        // Layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.padTop(50);

        table.add(frase).width(600).padBottom(40).row();
        table.add(botonVolver).width(200).padBottom(15).row();

        table.add(btnMejorarVida).width(260).padBottom(10).row();
        table.add(btnMejorarVelocidad).width(260).padBottom(10).row();
        table.add(btnMejorarSalto).width(260).padBottom(20).row();

        table.add(botonMusica).width(200).padBottom(15).row();
        table.add(botonMenu).width(200);

        super.escena.addActor(table);
    }

    // Actualizar las mejoras guardadas con las del jugador actual
    private void actualizarMejorasGuardadas() {
        // Actualizar solo las mejoras del jugador que murió
        if (idJugadorMuerto == 1) {
            mejorasJugador1 = copiarMejoras(jugador.getMejoras());
        } else if (idJugadorMuerto == 2) {
            mejorasJugador2 = copiarMejoras(jugador.getMejoras());
        }
    }

    // Método para copiar mejoras
    private MejoraTemporal copiarMejoras(MejoraTemporal origen) {
        MejoraTemporal copia = new MejoraTemporal();

        for (int i = 0; i < origen.getMejorasVida(); i++) {
            copia.mejorarVida();
        }
        for (int i = 0; i < origen.getMejorasVelocidad(); i++) {
            copia.mejorarVelocidad();
        }
        for (int i = 0; i < origen.getMejorasSalto(); i++) {
            copia.mejorarSalto();
        }

        return copia;
    }

    private void actualizarBotones(TextButton vida, TextButton velocidad, TextButton salto) {
        MejoraTemporal m = jugador.getMejoras();

        vida.setText("Mejorar Vida (" + m.getMejorasVida() + "/" + m.getMaxMejoras() + ")");
        velocidad.setText("Mejorar Velocidad (" + m.getMejorasVelocidad() + "/" + m.getMaxMejoras() + ")");
        salto.setText("Mejorar Salto (" + m.getMejorasSalto() + "/" + m.getMaxMejoras() + ")");

        vida.setDisabled(m.getMejorasVida() >= m.getMaxMejoras());
        velocidad.setDisabled(m.getMejorasVelocidad() >= m.getMaxMejoras());
        salto.setDisabled(m.getMejorasSalto() >= m.getMaxMejoras());
    }

    @Override
    public void show() {
    	Gdx.input.setInputProcessor(escena);
    }
}
