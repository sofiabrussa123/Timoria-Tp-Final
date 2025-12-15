package niveles.entorno;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
<<<<<<< HEAD
import com.badlogic.gdx.scenes.scene2d.Actor;

import interfaces.IdManager;

public abstract class ElementoEntorno extends Actor implements IdManager {
=======
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import interfaces.IdManager;
import io.github.timoria.Principal;

/**
 * Clase base abstracta para todos los elementos del entorno del juego
 */
public abstract class ElementoEntorno extends Actor implements IdManager{
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f

    protected float x;
    protected float y;
    protected Texture textura;
    protected float ancho;
    protected float alto;
    protected int id;

<<<<<<< HEAD
=======
    // Constructor con dimensiones e ID
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
    protected ElementoEntorno(float x, float y, float ancho, float alto, int id) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;

<<<<<<< HEAD
        setBounds(x + 37.5f, y, ancho, alto);
    }

    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        super.setPosition(x, y);
    }

    @Override
    public float getX() {
        return super.getX();
    }

    @Override
    public float getY() {
        return super.getY();
=======
        setBounds(x, y, ancho, alto);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
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
<<<<<<< HEAD
        if (textura != null) {
            textura.dispose();
        }
=======
        textura.dispose();
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
    }

    @Override
    public int getId() {
        return this.id;
    }
}
