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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class EscenaBase implements Screen{
	
    protected Principal juego;
    protected Stage escena;
    protected Skin fuenteTextos;
    protected Texture fondo;
    protected SpriteBatch batch;
	
	public EscenaBase(Principal juego, String fondo) {
		this.juego = juego;
		this.escena = new Stage(new ScreenViewport());
		this.batch = new SpriteBatch();
		this.fondo = new Texture(fondo);
		
		
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
    public void pause() {
        // No es necesario implementar
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
