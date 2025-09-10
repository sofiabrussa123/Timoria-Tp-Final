package interfaces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;

import globales.EsceneManager;
import io.github.timoria.Principal;
import niveles.EscenaBase;

public class Instrucciones extends EscenaBase {

    private Principal juego;
    private Stage stage;

    public Instrucciones(Principal juego) {
    	super(juego, "instrucciones.png");
        this.juego = juego;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            cambiarEscena(EsceneManager.getEscenaActual());
        }
    }
}
