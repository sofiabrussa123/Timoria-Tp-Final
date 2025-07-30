package io.github.timoria;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class EscenaBase implements Screen{

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
        skinPausa = new Skin(Gdx.files.internal("uiskin.json")); // Asegurate que tenés este archivo

        escenaPausa = new Stage(new ScreenViewport());

        TextButton btnSeguir = new TextButton("Seguir", skinPausa);
        TextButton btnMenu = new TextButton("Menú", skinPausa);

        btnSeguir.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                juegoPausado = false;
                Gdx.input.setInputProcessor(escena); // Volver al juego
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
        // Limpiar la pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar el fondo
        batch.begin();
        batch.draw(fondo, 0, 0, escena.getViewport().getWorldWidth(), escena.getViewport().getWorldHeight());
        batch.end();

        // Dibujar la escena
        escena.act(delta);
        escena.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            juegoPausado = !juegoPausado;
            Gdx.input.setInputProcessor(juegoPausado ? escenaPausa : escena);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

// Fondo
        batch.begin();
        batch.draw(fondo, 0, 0, escena.getViewport().getWorldWidth(), escena.getViewport().getWorldHeight());
        batch.end();

// Dibujo
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
        // Actualizar el batch para que coincida con el nuevo viewport
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
        Gdx.input.setInputProcessor(escena); // para recibir input del mouse
    }

    @Override
    public void resume() {
        // No es necesario implementar
    }

    @Override
    public void hide() {
        // No es necesario implementar
    }

}
