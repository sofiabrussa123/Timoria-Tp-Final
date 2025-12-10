package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public class PuertaLlegada extends ElementoEntorno {

    private boolean estaBloqueada = true;

    public PuertaLlegada(World mundo, float x, float y, float ancho, float alto) {
        super(mundo, x, y, ancho, alto);
        textura = new Texture(Gdx.files.internal("puerta.png"));
        super.crearYPosicionarCuerpo();
    }

    public boolean sePuedeCruzar() {
        return !estaBloqueada;
    }

    public void desbloquear() {
        estaBloqueada = false;
        textura = new Texture(Gdx.files.internal("puertaAbierta.png"));
    }
}
