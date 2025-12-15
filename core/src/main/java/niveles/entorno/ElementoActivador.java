package niveles.entorno;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Clase base abstracta para elementos del entorno que pueden ser activados.
 * Estos elementos funcionan como sensores en el mundo físico (no colisionan sólidamente).
 */
public abstract class ElementoActivador extends ElementoEntorno {

    protected boolean activado = false;
    protected Texture textura;

    // Constructor base para elementos activadores
    protected ElementoActivador(float x, float y, int id) {
        super(x, y, 1, 2, id);
    }

    // Crea y posiciona el cuerpo físico del elemento como sensor
    // Los sensores detectan colisiones pero no generan respuesta física
    protected void crearYPosicionarCuerpo(float ancho, float alto) {
        super.ancho = ancho;
        super.alto = alto;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textura, getX(), getY(), getWidth(), getHeight());
    }
}
