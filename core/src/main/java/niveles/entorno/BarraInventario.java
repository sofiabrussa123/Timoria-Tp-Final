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

        for(int i = 0; i < this.iconos.length; ++i) {
            this.iconos[i] = null;
        }

        this.setWidth(300.0F);
        this.setHeight(50.0F);
    }

    public Texture getIcono(int i) {
        return i >= 0 && i < this.iconos.length ? this.iconos[i] : null;
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
        for(int i = 0; i < this.iconos.length; ++i) {
            if (this.iconos[i] == null) {
                return i;
            }
        }

        return -1;
    }

    public void draw(Batch batch, float parentAlpha) {
        float y = (float)(this.jugador.getStage().getViewport().getScreenY() + this.jugador.getStage().getViewport().getScreenHeight() - 90);
        if (this.posicionIzquierda) {
            float baseX = (float)(this.jugador.getStage().getViewport().getScreenX() + 10);

            for(int i = 0; i < 5; ++i) {
                float x = baseX + (float)(i * 52);
                batch.draw(this.slotVacio, x, y, 50.0F, 50.0F);
                if (this.iconos[i] != null) {
                    batch.draw(this.iconos[i], x, y, 50.0F, 50.0F);
                }
            }
        } else {
            float baseX = (float)(this.jugador.getStage().getViewport().getScreenX() + this.jugador.getStage().getViewport().getScreenWidth() - 10 - 50);

            for(int i = 0; i < 5; ++i) {
                float x = baseX - (float)(i * 52);
                batch.draw(this.slotVacio, x, y, 50.0F, 50.0F);
                if (this.iconos[i] != null) {
                    batch.draw(this.iconos[i], x, y, 50.0F, 50.0F);
                }
            }
        }

    }

    public void dispose() {
        this.slotVacio.dispose();

        for(Texture icono : this.iconos) {
            if (icono != null) {
                icono.dispose();
            }
        }

    }
}
