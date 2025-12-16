package pantallas;

import Red.ControladorDeConexiones;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import Red.HiloCliente;
import globales.EsceneManager;
import niveles.EscenaBase;

public class Menu extends EscenaBase {

    private boolean musicaActiva = true;
    private HiloCliente hiloCliente;
    private ControladorDeConexiones controlador;

    private Music musica;

    public Menu(Game juego) {
        super(juego, "Fondo.jpeg");

        super.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));

        this.musica = Gdx.audio.newMusic(Gdx.files.internal("musica_fondo.mp3"));
        this.musica.setLooping(true);
        this.musica.setVolume(0.5f);
        this.musica.play();

        Table table = new Table();
        table.setFillParent(true);
        super.escena.addActor(table);

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

                controlador = new ControladorDeConexiones(juego);
                cambiarEscena(controlador);
            }
        });

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

    public ControladorDeConexiones getControlador() {
        return controlador;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.escena);
    }

    @Override
    public void dispose(){
        this.musica.dispose();
    }
}
