package interfaces;

import java.util.Random;

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
import niveles.EscenaBase;
import niveles.Nivel1;

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

    private Principal principal;

    public PantallaDeMuerte(Principal principal) {
        super(principal, "PantallaDeMuerte.png");
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
        table.padTop(50);
        table.add(frase).width(600).padBottom(40).row();
        table.add(botonVolver).width(200).padBottom(15).row();
        table.add(botonMusica).width(200).padBottom(15).row();
        table.add(botonMenu).width(200);

        super.escena.addActor(table);
    }
}