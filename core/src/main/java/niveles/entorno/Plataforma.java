package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public class Plataforma extends ElementoEntorno {

    public Plataforma(World mundo, float x, float y, float ancho, float alto) {
    	super(mundo, x, y, ancho, alto);

        textura = new Texture(Gdx.files.internal("plataforma.jpg"));

        super.crearYPosicionarCuerpo();
    }
}