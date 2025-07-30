package io.github.timoria;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Menu extends EscenaBase {

    private Music musica;

    public Menu(Principal juego) {
        super(juego, "Fondo.jpeg");
        super.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));

        // Música de fondo
        musica = Gdx.audio.newMusic(Gdx.files.internal("musica_fondo.mp3"));
        musica.setLooping(true);
        musica.setVolume(0.5f);
        musica.play();

        // Crear una tabla para organizar los elementos
        Table table = new Table();
        table.setFillParent(true);
        escena.addActor(table);

        // Crear el cartel "Menu"
        Label titulo = new Label("Timoria", fuenteTextos);

        // Crear el botón "Jugar"
        TextButton botonJugar = new TextButton("Jugar", fuenteTextos);
        botonJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                musica.stop();
                musica.dispose();
                juego.setScreen(new Nivel1(juego, fuenteTextos));
            }
        });

        // Agregar elementos a la tabla
        table.add(titulo).padBottom(30);
        table.row();
        table.add(botonJugar).width(200).height(50);
    }

}
