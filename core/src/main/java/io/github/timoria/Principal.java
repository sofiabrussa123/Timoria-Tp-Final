package io.github.timoria;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import interfaces.Menu;

public class Principal extends Game {
	
	public static final float PPM = 100;
    @Override
    public void create() {
    	
    	Menu menu = new Menu(this);
        setScreen(menu);
        Gdx.input.setInputProcessor(menu.getStage());
    }

}

