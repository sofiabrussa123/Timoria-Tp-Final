package io.github.timoria;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class EscenaBase implements Screen {

    protected Principal juego;
    protected Stage escena;
    protected Skin fuenteTextos;
    protected Texture fondo;
    protected SpriteBatch batch;
    protected boolean juegoPausado = false;
    protected Stage escenaPausa;
    protected Skin skinPausa;

    public EscenaBase(Principal juego, String fondo) {
        this.juego = juego;
        this.escena = new Stage(new ScreenViewport());
        this.batch = new SpriteBatch();
        this.fondo = new Texture(fondo);
        skinPausa = new Skin(Gdx.files.internal("uiskin.json"));

        escenaPausa = new Stage(new ScreenViewport());

        TextButton btnSeguir = new TextButton("Seguir", skinPausa);
        TextButton btnMenu = new TextButton("Menú", skinPausa);

        btnSeguir.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                juegoPausado = false;
                Gdx.input.setInputProcessor(crearMultiplexer());
            }
        });

        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new Menu(juego));
            }
        });

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();
        tabla.add(btnSeguir).pad(10);
        tabla.row();
        tabla.add(btnMenu).pad(10);

        escenaPausa.addActor(tabla);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(fondo, 0, 0, escena.getViewport().getWorldWidth(), escena.getViewport().getWorldHeight());
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            juegoPausado = !juegoPausado;
            Gdx.input.setInputProcessor(juegoPausado ? escenaPausa : crearMultiplexer());
        }

        if (juegoPausado) {
            escenaPausa.act(delta);
            escenaPausa.draw();
        } else {
            escena.act(delta);
            escena.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        escena.getViewport().update(width, height, true);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void dispose() {
        escena.dispose();
        fuenteTextos.dispose();
        fondo.dispose();
        batch.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(crearMultiplexer());
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void hide() {
    }

    // 🔧 Este método puede ser sobreescrito en cada nivel
    protected InputMultiplexer crearMultiplexer() {
        return new InputMultiplexer(escena);
    }
} 
