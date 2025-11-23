package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import io.github.timoria.Principal;

public class BotonActivador extends ElementoEntorno {

    private PuertaLlegada puerta; // Referencia a la puerta que se va a desbloquear
    private boolean fueActivado = false;

    public BotonActivador(World mundo, float x, float y, float ancho, float alto, PuertaLlegada puerta) {
    	super(mundo, x, y, ancho, alto);
        this.puerta = puerta;
        textura = new Texture(Gdx.files.internal("boton.png"));
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        super.setFixtureDef(fixtureDef);
        super.crearYPosicionarCuerpo();
    }

    public void activar() {
        if (!fueActivado) {
            puerta.desbloquear(); // desbloqueamos la puerta
            fueActivado = true;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!fueActivado) {
            super.draw(batch, parentAlpha);
        }
    }
}
