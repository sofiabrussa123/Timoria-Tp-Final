package niveles.entorno;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;

import personajes.Jugador;

/**
 * Barra visual que muestra la vida del jugador.
 * Se posiciona en la parte superior de la pantalla con el texto de vida superpuesto.
 */
public class BarraVida extends Actor {

    private final Texture relleno;
    private Jugador jugador;
    private Texture fondo;
    private Texture vidaLlena;
    private boolean posicionIzquierda;
    private BitmapFont fuente;

    /**
     * Constructor de la barra de vida.
     * @param jugador El jugador cuya vida se mostrará
     * @param posicionIzquierda true para posicionar en la izquierda, false para la derecha
     */
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
        int vida = jugador.getVida();
        int vidaMax = jugador.getVidaMaxima();
        float porcentaje = (float) vida / (float) vidaMax;

        float y = jugador.getStage().getViewport().getScreenY()
            + jugador.getStage().getViewport().getScreenHeight() - 30;

        float x;

        if (posicionIzquierda) {
            // Barra de vida desde la izquierda (Jugador 1)
            x = jugador.getStage().getViewport().getScreenX() + 10;
            batch.draw(fondo, x, y, getWidth(), getHeight());
            batch.draw(relleno, x, y, getWidth() * porcentaje, getHeight());
        } else {
            // Barra de vida desde la derecha (Jugador 2)
            x = jugador.getStage().getViewport().getScreenX()
                + jugador.getStage().getViewport().getScreenWidth()
                - getWidth() - 10;

            batch.draw(fondo, x, y, getWidth(), getHeight());

            // Calcular posición del relleno para que crezca desde la derecha
            float rellenoX = x + getWidth() - (getWidth() * porcentaje);
            batch.draw(relleno, rellenoX, y, getWidth() * porcentaje, getHeight());
        }

        // Dibujar texto de vida encima de la barra
        String textoVida = vida + " / " + vidaMax;
        float textoX = x + getWidth() / 2f - 20; // Centrado aproximado
        float textoY = y + getHeight() / 2f + 5; // Centrado vertical
        fuente.draw(batch, textoVida, textoX, textoY);
    }

    /**
     * Libera los recursos de texturas y fuentes utilizados.
     */
    public void dispose() {
        fondo.dispose();
        relleno.dispose();
        if (vidaLlena != null) {
            vidaLlena.dispose();
        }
        fuente.dispose();
    }
}
