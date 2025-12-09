package niveles.entorno;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import Red.HiloServidor;
import niveles.NivelBase;

public abstract class ElementoActivador extends ElementoEntorno{
	protected boolean activado = false;
	protected Texture textura;
	protected HiloServidor hiloServidor;
	
	protected ElementoActivador(World mundo, float x, float y, int id) {
		super(mundo, x, y, id);
	}
	
	protected void crearYPosicionarCuerpo(float ancho, float alto) {
		super.ancho = ancho;
		super.alto = alto;
		FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.isSensor = true;
        super.setFixtureDef(fixtureDef);
        super.crearYPosicionarCuerpo();
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textura, getX(), getY(), getWidth(), getHeight());
    }
}