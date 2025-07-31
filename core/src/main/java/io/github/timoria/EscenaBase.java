package io.github.timoria;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class EscenaBase implements Screen {

    protected Principal juego;
    protected Stage escena;
    protected Texture fondo;
    protected SpriteBatch batch;
    protected Skin fuenteTextos;

    public EscenaBase(Principal juego, String fondo) {
        this.juego = juego;
        this.escena = new Stage(new ScreenViewport());
        this.batch = new SpriteBatch();
        this.fondo = new Texture(fondo);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(fondo, 0, 0, escena.getViewport().getWorldWidth(), escena.getViewport().getWorldHeight());
        batch.end();
        escena.act(delta);
        escena.draw();
    }

    @Override
    public void resize(int width, int height) {
        escena.getViewport().update(width, height, true);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void dispose() {
        escena.dispose();
        fondo.dispose();
        if (fuenteTextos != null) fuenteTextos.dispose();
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
