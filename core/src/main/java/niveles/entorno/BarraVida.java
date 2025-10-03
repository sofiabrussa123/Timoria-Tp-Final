package niveles.entorno;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import personajes.Personaje;

public class BarraVida extends Actor{

    private Personaje jugador;
    private Texture fondo;
    private Texture relleno;

    public BarraVida(Personaje jugador) {
        this.jugador = jugador;
        this.fondo = new Texture("barra_fondo.png");
        this.relleno = new Texture("barra_vida.png");
        setWidth(300);
        setHeight(20);
    }

    public void draw(Batch batch, float parentAlpha) {
        int vida = jugador.getVida();
        int vidaMax = jugador.getVidaMaxima();
        float porcentaje = (float) vida / vidaMax;
        float x = jugador.getStage().getViewport().getScreenX() + 10;
        float y = jugador.getStage().getViewport().getScreenY() + jugador.getStage().getViewport().getScreenHeight() - 30;

        // Fondo gris
        batch.draw(fondo, x, y, getWidth(), getHeight());

        // Barra roja
        batch.draw(relleno, x, y, getWidth() * porcentaje, getHeight());

        // Restaurar color a blanco para no afectar otros draws
        batch.setColor(Color.WHITE);
    }
}
