package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import personajes.Jugador;

public class LlaveActivadora extends ElementoActivador {

    private PuertaLlegada puerta;
    private float ancho = 30f;
    private float alto = 45f;

    public LlaveActivadora(World mundo, float x, float y, PuertaLlegada puerta, int id) {
        super(mundo, x, y, id);
        this.puerta = puerta;
        super.textura = new Texture(Gdx.files.internal("boton.png"));
        super.crearYPosicionarCuerpo(this.ancho, this.alto);
    }

    public void activarConJugador(Jugador personaje) {
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