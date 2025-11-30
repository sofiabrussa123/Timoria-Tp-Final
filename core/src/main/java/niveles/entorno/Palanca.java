package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Palanca extends ElementoEntorno{
	private boolean activada = false;
	
	public Palanca(World mundo, float x, float y) {
		super(mundo, x, y);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		super.setFixtureDef(fixtureDef);
		super.ancho = 20;
		super.alto = 20;
		super.textura = new Texture(Gdx.files.internal("Palanca1.png"));
		super.crearYPosicionarCuerpo();
	}
	
	public boolean getActivada() {
		return this.activada;
	}
	
	public void activar() {
		this.activada = !activada;
		
		if(activada) {
			super.textura = new Texture(Gdx.files.internal("Palanca2.png"));
		} else {
			super.textura = new Texture(Gdx.files.internal("Palanca1.png"));
		}
	}
}
