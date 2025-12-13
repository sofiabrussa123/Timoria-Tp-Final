package interfaces;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import globales.EsceneManager;
import niveles.EscenaBase;
import niveles.Nivel1;

public class Menu extends EscenaBase {

    // Valores fijos o con estado inicial conocido
    private boolean musicaActiva = true;

    // Objetos dependientes del contexto → se inicializan en el constructor
    private Music musica;

    public Menu(Game juego) {
        super(juego, "Fondo.jpeg");

        // Fuente / Skin
        super.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));

        // Música de fondo
        this.musica = Gdx.audio.newMusic(Gdx.files.internal("musica_fondo.mp3"));
        this.musica.setLooping(true);
        this.musica.setVolume(0.5f);
        this.musica.play();

        // Tabla principal de layout
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

                String[] imagenesIntro = {
                    "1.png",
                    "2.png",
                    "3.png",
                    "4.png",
                    "5.png"
                };

                // Pasamos Nivel1.class en lugar de new Nivel1(juego)
                cambiarEscena(new SecuenciaImagenes(juego, imagenesIntro, Nivel1.class));
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

        table.add(botonJugar).width(350).height(40).padBottom(15);
        table.row();

        table.add(botonMusicaMenu).width(350).height(40).padBottom(15);
        table.row();

        table.add(btnInstrucciones).width(350).height(40);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.escena);
    }
}
