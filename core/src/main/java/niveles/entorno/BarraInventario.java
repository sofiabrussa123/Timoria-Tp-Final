package niveles.entorno;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import personajes.Jugador;

public class BarraInventario extends Actor {
	
    private Jugador jugador;
    private Texture slotVacio;
    private Texture[] iconos;
    private boolean posicionIzquierda;

    public BarraInventario(Jugador jugador, boolean posicionIzquierda) {
        this.jugador = jugador;
        this.posicionIzquierda = posicionIzquierda;
        this.slotVacio = new Texture("barraInventario.png");
        this.iconos = new Texture[5];

        for (int i = 0; i < this.iconos.length; i++) {
            this.iconos[i] = null;
        }

        this.setWidth(300f);
        this.setHeight(50f);
    }

    public Texture getIcono(int i) {
        return (i >= 0 && i < this.iconos.length) ? this.iconos[i] : null;
    }

    public void setIcono(int i, Texture textura) {
        if (i >= 0 && i < this.iconos.length) {
            this.iconos[i] = textura;
        }
        
    }

    public void limpiarIcono(int i) {
        if (i >= 0 && i < this.iconos.length) {
            this.iconos[i] = null;
        }
    }

    public int getPrimeraCasillaLibre() {
        for (int i = 0; i < this.iconos.length; i++) {
            if (this.iconos[i] == null) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float y = jugador.getStage().getViewport().getScreenY()
            + jugador.getStage().getViewport().getScreenHeight() - 90;

        if (posicionIzquierda) {
            float baseX = jugador.getStage().getViewport().getScreenX() + 10;

            for (int i = 0; i < 5; i++) {
                float x = baseX + (i * 52);
                batch.draw(slotVacio, x, y, 50f, 50f);
                if (iconos[i] != null) {
                    batch.draw(iconos[i], x, y, 50f, 50f);
                }
            }
        } else {
            float baseX = jugador.getStage().getViewport().getScreenX()
                + jugador.getStage().getViewport().getScreenWidth() - 10 - 50;

            for (int i = 0; i < 5; i++) {
                float x = baseX - (i * 52);
                batch.draw(slotVacio, x, y, 50f, 50f);
                if (iconos[i] != null) {
                    batch.draw(iconos[i], x, y, 50f, 50f);
                }
            }
        }
    }

    public void dispose() {
        slotVacio.dispose();

        for (Texture icono : iconos) {
            if (icono != null) {
                icono.dispose();
            }
        }
    }
}
