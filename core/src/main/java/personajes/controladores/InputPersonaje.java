package personajes.controladores;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import personajes.Personaje;

public class InputPersonaje extends InputAdapter {

    private Personaje personaje;

    public InputPersonaje(Personaje personaje) {
        this.personaje = personaje;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.A) {
            personaje.setMoverIzquierda(true);
        }
        if (keycode == Input.Keys.D) {
            personaje.setMoverDerecha(true);
        }
        if (keycode == Input.Keys.W) {
            personaje.setSaltar(true);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.A) {
            personaje.setMoverIzquierda(false);
        }
        if (keycode == Input.Keys.D) {
            personaje.setMoverDerecha(false);
        }
        return true;
    }
} 
