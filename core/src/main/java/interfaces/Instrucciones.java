package interfaces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.timoria.Principal;

public class Instrucciones implements Screen {

    private Principal juego;
    private Texture imagen;
    private SpriteBatch batch;
    private Stage stage;
    private Screen pantallaRetorno; // pantalla desde donde se llamó

    public Instrucciones(Principal juego, Screen pantallaRetorno) {
        this.juego = juego;
        this.pantallaRetorno = pantallaRetorno;
        imagen = new Texture(Gdx.files.internal("instrucciones.png"));
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport()); // aunque no haya UI, sirve para captar input
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); // activar input handling
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();
        batch.draw(imagen, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE) && pantallaRetorno != null) {
            juego.setScreen(pantallaRetorno);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        imagen.dispose();
        batch.dispose();
        stage.dispose();
    }
}
