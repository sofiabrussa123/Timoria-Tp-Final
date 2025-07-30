package io.github.timoria;

import com.badlogic.gdx.Game;

public class Principal extends Game {
	public static final float PPM = 100;
    @Override
    public void create() {
        setScreen(new Menu(this));
    }

}
