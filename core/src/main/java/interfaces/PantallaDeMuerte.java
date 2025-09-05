package menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.timoria.Principal;
import io.github.timoria.Nivel1;

import java.util.Random;

public class PantallaDeMuerte implements Screen {

    private Stage stage;
    private Skin skin;
    private Texture fondoMuerte;
    private SpriteBatch batch;
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
        this.principal = principal;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);

        fondoMuerte = new Texture(Gdx.files.internal("PantallaDeMuerte.png"));

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        fraseElegida = frases[new Random().nextInt(frases.length)];

        Label frase = new Label(fraseElegida, skin);
        frase.setAlignment(Align.center);
        frase.setFontScale(1.2f);
        frase.setWrap(true);
        frase.setWidth(600);

        musicaMuerte = Gdx.audio.newMusic(Gdx.files.internal("Muerte.mp3"));
        musicaMuerte.setLooping(true);

        if (musicaMuerteActiva) musicaMuerte.play();

        TextButton botonVolver = new TextButton("Volver a jugar", skin);
        TextButton botonMusica = new TextButton("Silenciar música", skin);
        TextButton botonMenu = new TextButton("Menú principal", skin);

        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                principal.setScreen(new Nivel1(principal));
                musicaMuerte.pause();
            }
        });

        botonMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                principal.setScreen(new Menu(principal));
                musicaMuerte.pause();
            }
        });

        botonMusica.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                musicaMuerteActiva = !musicaMuerteActiva;
                if (musicaMuerteActiva) {
                    musicaMuerte.play();
                    botonMusica.setText("Silenciar música");
                } else {
                    musicaMuerte.pause();
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

        stage.addActor(table);
    }

    @Override public void show() {}

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(
            fondoMuerte,
            0,
            0,
            stage.getViewport().getWorldWidth(),
            stage.getViewport().getWorldHeight()
        );

        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
        public void resize(int width, int height) {
            stage.getViewport().update(width, height, true);
        }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        batch.dispose();
        fondoMuerte.dispose();
        skin.dispose();
    }
}
