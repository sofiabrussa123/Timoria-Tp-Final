package interfaces;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import globales.EsceneManager;
import niveles.EscenaBase;

public class Instrucciones extends EscenaBase {

    public Instrucciones(Game juego) {
    	super(juego, "instrucciones.png");
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            cambiarEscena(EsceneManager.getEscenaActual());
        }
    }
}
