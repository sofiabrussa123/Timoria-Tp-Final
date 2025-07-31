package menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.timoria.Principal;

public class PantallaGanaste implements Screen {

    private Stage escena;
    private Skin skin;

    public PantallaGanaste(Principal juego) {

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        escena = new Stage(new ScreenViewport());

        Label mensaje = new Label("¡Ganaste!", skin);
        mensaje.setFontScale(2);

        TextButton btnMenu = new TextButton("Menú", skin);
        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new Menu(juego));
            }
        });

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();
        tabla.add(mensaje).pad(10);
        tabla.row();
        tabla.add(btnMenu).pad(10);

        escena.addActor(tabla);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(escena);
    }

    @Override
    public void render(float delta) {
        escena.act(delta);
        escena.draw();
    }

    @Override public void resize(int width, int height) {
        escena.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        escena.dispose();
        skin.dispose();
    }
}
