package interfaces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import globales.EsceneManager;
import io.github.timoria.Principal;
import niveles.EscenaBase;
import niveles.Nivel1;
import niveles.NivelBase;

public class Menu extends EscenaBase {

    private Music musica;
    private boolean musicaActiva = true;

    public Menu(Principal juego) {
        super(juego, "Fondo.jpeg");
        super.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));

        // Música de fondo
        this.musica = Gdx.audio.newMusic(Gdx.files.internal("musica_fondo.mp3"));
        this.musica.setLooping(true);
        this.musica.setVolume(0.5f);
        this.musica.play();

        // Crear una tabla para organizar los elementos
        Table table = new Table();
        table.setFillParent(true);
        super.escena.addActor(table);

        // Botón Jugar
        TextButton botonJugar = new TextButton("Jugar", super.fuenteTextos);
        botonJugar.addListener(new ClickListener() {
            @Override	
            public void clicked(InputEvent event, float x, float y) {
                Menu.this.musica.stop();
                Menu.this.musica.dispose();
                EsceneManager.setEscenaActual(new Nivel1(juego));
                cambiarEscena((NivelBase)EsceneManager.getEscenaActual());
            }
        });

        // Botón de Música (activar/desactivar)
        TextButton botonMusicaMenu = new TextButton("Silenciar Música", super.fuenteTextos);
        botonMusicaMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Menu.this.musicaActiva = !Menu.this.musicaActiva;
                if (Menu.this.musicaActiva) {
                    Menu.this.musica.play();
                    botonMusicaMenu.setText("Silenciar Música");
                } else {
                    Menu.this.musica.pause();
                    botonMusicaMenu.setText("Activar Música");
                }
            }
        });

        // Botón con instrucciones básicas
        TextButton btnInstrucciones = new TextButton("Instrucciones", super.fuenteTextos);
        btnInstrucciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	EsceneManager.setEscenaActual(Menu.this);
                cambiarEscena(new Instrucciones(juego));
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
