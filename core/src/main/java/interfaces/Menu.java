package interfaces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.timoria.Principal;
import niveles.EscenaBase;
import niveles.Nivel1;

public class Menu extends EscenaBase {

    private Music musica;
    private boolean musicaActiva = true;

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

        // Botón Jugar
        TextButton botonJugar = new TextButton("Jugar", fuenteTextos);
        botonJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                musica.stop();
                musica.dispose();
                juego.setScreen(new Nivel1(juego));
            }
        });

        // Botón de Música (activar/desactivar)
        TextButton botonMusicaMenu = new TextButton("Silenciar Música", fuenteTextos);
        botonMusicaMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                musicaActiva = !musicaActiva;
                if (musicaActiva) {
                    musica.play();
                    botonMusicaMenu.setText("Silenciar Música");
                } else {
                    musica.pause();
                    botonMusicaMenu.setText("Activar Música");
                }
            }
        });

        // Botón con instrucciones básicas
        TextButton btnInstrucciones = new TextButton("Instrucciones", fuenteTextos);
        btnInstrucciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new Instrucciones(juego, Menu.this)); // ← pasa esta pantalla como retorno
            }
        });

        // Agregar elementos a la tabla
        table.add(botonJugar).width(350).height(40).padBottom(15);
        table.row();
        table.add(botonMusicaMenu).width(350).height(40).padBottom(15);
        table.row();
        table.add(btnInstrucciones).width(350).height(40);
    }
}
