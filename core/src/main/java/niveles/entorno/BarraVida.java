package niveles.entorno;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;

import personajes.Jugador;

public class BarraVida extends Actor {

    private final Texture relleno;
    private Jugador jugador;
    private Texture fondo;
    private Texture vidaLlena;
    private boolean posicionIzquierda;
    private BitmapFont fuente;

    public BarraVida(Jugador jugador, boolean posicionIzquierda) {
        this.jugador = jugador;
        this.posicionIzquierda = posicionIzquierda;
        this.fondo = new Texture("barra_fondo.png");
        this.relleno = new Texture("barra_vida.png");
        this.fuente = new BitmapFont();      // texto por defecto
        this.fuente.setColor(Color.WHITE);   // color visible
        this.setWidth(300f);
        this.setHeight(20f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        int vida = jugador.getVida();
        int vidaMax = jugador.getVidaMaxima();
        float porcentaje = (float) vida / (float) vidaMax;

        float y = jugador.getStage().getViewport().getScreenY()
            + jugador.getStage().getViewport().getScreenHeight() - 30;

        float x;

        if (posicionIzquierda) {
            x = jugador.getStage().getViewport().getScreenX() + 10;
            batch.draw(fondo, x, y, getWidth(), getHeight());
            batch.draw(relleno, x, y, getWidth() * porcentaje, getHeight());
        } else {
            x = jugador.getStage().getViewport().getScreenX()
                + jugador.getStage().getViewport().getScreenWidth()
                - getWidth() - 10;

            batch.draw(fondo, x, y, getWidth(), getHeight());

            float rellenoX = x + getWidth() - (getWidth() * porcentaje);
            batch.draw(relleno, rellenoX, y, getWidth() * porcentaje, getHeight());
        }

        // ===== DIBUJAR VIDA ENCIMA DE LA BARRA =====
        String textoVida = vida + " / " + vidaMax;

        float textoX = x + getWidth() / 2f - 20; // ajuste visual
        float textoY = y + getHeight() / 2f + 5;

        fuente.draw(batch, textoVida, textoX, textoY);
    }

    public void dispose() {
        fondo.dispose();
        if (vidaLlena != null) vidaLlena.dispose();
        fuente.dispose();
    }
}
