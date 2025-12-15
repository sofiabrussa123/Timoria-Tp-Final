package niveles.entorno;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import interfaces.IdManager;

public abstract class ElementoEntorno extends Actor implements IdManager {

    protected float x;
    protected float y;
    protected Texture textura;
    protected float ancho;
    protected float alto;
    protected int id;

    protected ElementoEntorno(float x, float y, float ancho, float alto, int id) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;

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
        if (textura != null) {
            textura.dispose();
        }
    }

    @Override
    public int getId() {
        return this.id;
    }
}
