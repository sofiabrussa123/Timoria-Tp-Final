package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public class Palanca extends ElementoActivador{
	private float ancho = 20;
	private float alto = 20;
	
	public Palanca(World mundo, float x, float y) {
		super(mundo, x, y);
		super.textura = new Texture(Gdx.files.internal("Palanca1.png"));
		super.crearYPosicionarCuerpo(this.ancho, this.alto);
	}
	
	public boolean getActivada() {
		return super.activado;
	}
	
	public void activar() {
		this.activado = !activado;
		
		if(activado) {
			super.textura = new Texture(Gdx.files.internal("Palanca2.png"));
		} else {
			super.textura = new Texture(Gdx.files.internal("Palanca1.png"));
		}
	}
}
