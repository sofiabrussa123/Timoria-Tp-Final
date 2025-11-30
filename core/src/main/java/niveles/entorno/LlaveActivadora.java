package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import personajes.Personaje;

public class LlaveActivadora extends ElementoActivador {

    private PuertaLlegada puerta; // Referencia a la puerta que se va a desbloquear
    private float ancho = 30;
    private float alto = 45;

    public LlaveActivadora(World mundo, float x, float y, PuertaLlegada puerta) {
    	super(mundo, x, y);
        this.puerta = puerta;
        super.textura = new Texture(Gdx.files.internal("boton.png"));
        super.crearYPosicionarCuerpo(this.ancho, this.alto);
    }

    public void activarConJugador(Personaje personaje) {
        if (!this.activado) {
            int slotLibre = personaje.getBarraInventario().getPrimeraCasillaLibre();
            if (slotLibre != -1) {
                Texture texturaLlave = new Texture(Gdx.files.internal("boton.png"));
                personaje.getBarraInventario().setIcono(slotLibre, texturaLlave);
                this.puerta.desbloquear();
                this.activado = true;
                this.remove();
            } else {
                System.out.println("Inventario lleno. No se puede recoger la llave.");
            }
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!activado) {
            super.draw(batch, parentAlpha);
        }
    }
}
