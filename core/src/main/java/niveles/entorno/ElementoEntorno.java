package niveles.entorno;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import interfaces.IdManager;
import io.github.timoria.Principal;

/**
 * Clase base abstracta para todos los elementos del entorno del juego
 */
public abstract class ElementoEntorno extends Actor implements IdManager{

    protected float x;
    protected float y;
    protected Texture textura;
    protected float ancho;
    protected float alto;
    protected int id;

    // Constructor con dimensiones e ID
    protected ElementoEntorno(float x, float y, float ancho, float alto, int id) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;

        setBounds(x, y, ancho, alto);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(textura != null){
            batch.draw(textura, getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public void act(float delta) {}

    public void dispose() {
        textura.dispose();
    }

    @Override
    public int getId() {
        return this.id;
    }
}
