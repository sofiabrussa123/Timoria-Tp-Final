package io.github.timoria;

import com.badlogic.gdx.Game;
<<<<<<< HEAD
import pantallas.Menu;

public class Principal extends Game {

    public static final float PPM = 100;
    private Menu menu;

    @Override
    public void create() {
        menu = new Menu(this);
        setScreen(menu);
    }

    @Override
    public void dispose(){
        if(menu.getControlador() != null) menu.getControlador().getHiloCliente().enviarMensaje("Desconectar");
    }
}

=======

import interfaces.Menu;

public class Principal extends Game {
	
	public static final float PPM = 100;
    @Override
    public void create() {
    	
    	Menu menu = new Menu(this);
        setScreen(menu);
    }

}
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
