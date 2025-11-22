package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.timoria.Principal;
import personajes.Personaje;

public class BotonActivador extends Actor {

    private Texture textura;
    private Body cuerpo;
    private PuertaLlegada puerta; // Referencia a la puerta que se va a desbloquear
    private boolean fueActivado = false;

    public BotonActivador(World mundo, float x, float y, float ancho, float alto, PuertaLlegada puerta) {
        this.puerta = puerta;
        this.textura = new Texture(Gdx.files.internal("boton.png"));
        BodyDef defCuerpo = new BodyDef();
        defCuerpo.type = BodyType.StaticBody;
        defCuerpo.position.set((x + ancho / 2.0F) / 100.0F, (y + alto / 2.0F) / 100.0F);
        this.cuerpo = mundo.createBody(defCuerpo);
        PolygonShape forma = new PolygonShape();
        forma.setAsBox(ancho / 2.0F / 100.0F, alto / 2.0F / 100.0F);
        FixtureDef defFixture = new FixtureDef();
        defFixture.shape = forma;
        defFixture.isSensor = true;
        this.cuerpo.createFixture(defFixture);
        forma.dispose();
        this.setBounds(x, y, ancho, alto);
        this.cuerpo.setUserData(this);
    }

    public void activar() {
        if (!fueActivado) {
            puerta.desbloquear(); // desbloqueamos la puerta
            fueActivado = true;
        }
    }

    public void activarConJugador(Personaje personaje) {
        if (!this.fueActivado) {
            int slotLibre = personaje.getBarraInventario().getPrimeraCasillaLibre();
            if (slotLibre != -1) {
                Texture texturaLlave = new Texture(Gdx.files.internal("boton.png"));
                personaje.getBarraInventario().setIcono(slotLibre, texturaLlave);
                this.puerta.desbloquear();
                this.fueActivado = true;
                this.remove();
            } else {
                System.out.println("Inventario lleno. No se puede recoger la llave.");
            }
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!fueActivado) {
            batch.draw(textura, getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public void act(float delta) {
        Vector2 posicion = cuerpo.getPosition();
        setPosition(posicion.x * Principal.PPM - getWidth() / 2, posicion.y * Principal.PPM - getHeight() / 2);
    }

    public Body getCuerpo() {
        return cuerpo;
    }

    public void dispose() {
        textura.dispose();
    }
}
