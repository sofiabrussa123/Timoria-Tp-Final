package niveles;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import globales.InputManager;

public abstract class EscenaBase implements Screen {

    protected Game juego;
    protected Stage escena;
    protected Texture fondo;
    protected SpriteBatch batch;
    protected Skin fuenteTextos;

    //Constructor, inicializar variables
    public EscenaBase(Game juego, String fondo) {
    	
        this.juego = juego;
        this.escena = new Stage(new ScreenViewport());
        this.batch = new SpriteBatch(); //PROBABLEMENTE NI LO NECESITES
        this.fondo = new Texture(fondo);
    }

    @Override
    public void render(float delta) {
    	
    	//Limpiar frame anterior, rellenar con negro
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Dibujar todo
        batch.begin(); // NO 
        batch.draw(this.fondo, 0, 0, escena.getViewport().getWorldWidth(), escena.getViewport().getWorldHeight());
        batch.end();
        escena.act(delta);
        escena.draw();
    }

    @Override
    public void resize(int width, int height) {
    	
        this.escena.getViewport().update(width, height, true);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    @Override
    public void dispose() {
    	
        this.escena.dispose();
        this.fondo.dispose();// NO
        if (this.fuenteTextos != null) this.fuenteTextos.dispose();
        batch.dispose();
    }
    
    @Override
    public void show() {
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
    
    //Establecer una nueva pantalla y asignar el stage de la nueva escena al InputProcessor
    protected void cambiarEscena(EscenaBase nuevaEscena) {
    	
    	this.juego.setScreen(nuevaEscena);
    	Gdx.input.setInputProcessor(nuevaEscena.escena);//-> Se hace en el constructor de la nueva pantalla
    }
    
    //Sobrecarga para asignar un Multiplexer al stage en caso de ser un nivel
    protected void cambiarEscena(NivelBase nuevoNivel) {
    	
    	this.juego.setScreen(nuevoNivel);
    }
    
    public Stage getStage() {
    	
    	return this.escena;
    }
} 
