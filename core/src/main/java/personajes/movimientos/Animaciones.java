package personajes.movimientos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Animaciones {

    protected Animation<TextureRegion> animacionQuieto;
    protected Animation<TextureRegion> animacionCorrer;

    Texture hojaQuieto = new Texture("animaciones/idle.png");
    Texture hojaCorrer = new Texture("animaciones/run.png");

    public Animaciones() {
        animacionQuieto = crearAnimacionDesdeHoja(hojaQuieto, 0.1f, 128, 128);
        animacionCorrer = crearAnimacionDesdeHoja(hojaCorrer, 0.1f, 128, 128);
    }

    protected Animation<TextureRegion> crearAnimacionDesdeHoja(Texture hoja, float duracionFrame, int ancho, int alto) {
        TextureRegion[][] temporal = TextureRegion.split(hoja, ancho, alto);
        Array<TextureRegion> cuadros = new Array<>();

        for (TextureRegion[] fila : temporal) {
            for (TextureRegion cuadro : fila) {
                cuadros.add(cuadro);
            }
        }

        if (cuadros.size == 0) {
            throw new IllegalArgumentException("La hoja de sprites no contiene cuadros o no se ha cargado correctamente: " + hoja);
        }

        return new Animation<TextureRegion>(duracionFrame, cuadros, Animation.PlayMode.LOOP);
    }

    public Animation<TextureRegion> getAnimacionCorrer() {
        return this.animacionCorrer;
    }

    public Animation<TextureRegion> getAnimacionQuieto() {
        return this.animacionQuieto;
    }

    public void setAnimacionQuieto(Animation<TextureRegion> animacionQuieto) {
        this.animacionQuieto = animacionQuieto;
    }
} 
