package io.github.timoria;

import com.badlogic.gdx.Game;
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
