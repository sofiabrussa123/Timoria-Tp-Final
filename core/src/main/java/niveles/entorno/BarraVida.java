package niveles.entorno;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import personajes.Personaje;

public class BarraVida extends Actor {
    private final Texture relleno;
    private Personaje jugador;
    private Texture fondo;
    private Texture vidaLlena;
    private boolean posicionIzquierda;

    public BarraVida(Personaje jugador, boolean posicionIzquierda) {
        this.jugador = jugador;
        this.posicionIzquierda = posicionIzquierda;
        this.fondo = new Texture("barra_fondo.png");
        this.relleno = new Texture("barra_vida.png");
        this.setWidth(300.0F);
        this.setHeight(20.0F);
    }

    public void draw(Batch batch, float parentAlpha) {
        int vida = this.jugador.getVida();
        int vidaMax = this.jugador.getVidaMaxima();
        float porcentaje = (float)vida / (float)vidaMax;
        float y = (float)(this.jugador.getStage().getViewport().getScreenY() + this.jugador.getStage().getViewport().getScreenHeight() - 30);
        if (this.posicionIzquierda) {
            float x = (float)(this.jugador.getStage().getViewport().getScreenX() + 10);
            batch.draw(this.fondo, x, y, this.getWidth(), this.getHeight());
            batch.draw(this.relleno, x, y, this.getWidth() * porcentaje, this.getHeight());
        } else {
            float x = (float)(this.jugador.getStage().getViewport().getScreenX() + this.jugador.getStage().getViewport().getScreenWidth()) - this.getWidth() - 10.0F;
            batch.draw(this.fondo, x, y, this.getWidth(), this.getHeight());
            float rellenoX = x + this.getWidth() - this.getWidth() * porcentaje;
            batch.draw(this.relleno, rellenoX, y, this.getWidth() * porcentaje, this.getHeight());
        }

    }

    public void dispose() {
        this.fondo.dispose();
        this.vidaLlena.dispose();
    }
}
