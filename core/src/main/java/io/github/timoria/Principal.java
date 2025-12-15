package io.github.timoria;

import Red.ServidorManager;
import com.badlogic.gdx.Game;
import pantallas.Menu;

public class Principal extends Game {

    public static final float PPM = 100;

    @Override
    public void create() {
        Menu menu = new Menu(this);
        setScreen(menu);
    }

    @Override
    public void dispose(){
        super.dispose();
        if(ServidorManager.obtenerServidor(this) != null){
            ServidorManager.obtenerServidor(this).enviarMensajeATodos("Desconectar");
            ServidorManager.obtenerServidor(this).terminar();
        }
    }
}
