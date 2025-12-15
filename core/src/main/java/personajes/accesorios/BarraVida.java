package personajes.accesorios;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;

import personajes.Jugador;

public class BarraVida extends Actor {

    private final Texture relleno;
    private Jugador jugador;
    private Texture fondo;
    private boolean posicionIzquierda;
    private BitmapFont fuente;

    public BarraVida(Jugador jugador, boolean posicionIzquierda) {
        this.jugador = jugador;
        this.posicionIzquierda = posicionIzquierda;
        this.fondo = new Texture("barra_fondo.png");
        this.relleno = new Texture("barra_vida.png");
        this.fuente = new BitmapFont();
        this.fuente.setColor(Color.WHITE);
        this.setWidth(300f);
        this.setHeight(20f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (jugador == null) return;

        int vida = jugador.getVida();
        int vidaMax = jugador.getVidaMaxima();
        float porcentaje = (float) vida / (float) vidaMax;

        float screenWidth = getStage().getViewport().getScreenWidth();
        float screenHeight = getStage().getViewport().getScreenHeight();

        float y = screenHeight - 30;
        float x;

        if (posicionIzquierda) {
            x = 10;
            batch.draw(fondo, x, y, getWidth(), getHeight());
            batch.draw(relleno, x, y, getWidth() * porcentaje, getHeight());
        } else {
            x = screenWidth - getWidth() - 10;
            batch.draw(fondo, x, y, getWidth(), getHeight());

            float rellenoX = x + getWidth() - (getWidth() * porcentaje);
            batch.draw(relleno, rellenoX, y, getWidth() * porcentaje, getHeight());
        }

        String textoVida = vida + " / " + vidaMax;
        float textoX = x + getWidth() / 2f - 20;
        float textoY = y + getHeight() / 2f + 5;
        fuente.draw(batch, textoVida, textoX, textoY);
    }

    public void dispose() {
        fondo.dispose();
        relleno.dispose();
        fuente.dispose();
    }

    public void actualizarVida() {
        this.invalidate();
    }

    private void invalidate() {

    }
}
