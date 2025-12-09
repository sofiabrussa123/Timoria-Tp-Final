package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import personajes.Jugador;

public class LlaveActivadora extends ElementoActivador {

    private PuertaLlegada puerta; // Referencia a la puerta que se va a desbloquear
    private float ancho = 30;
    private float alto = 45;

    public LlaveActivadora(World mundo, float x, float y, PuertaLlegada puerta, int id) {
    	super(mundo, x, y, id);
        this.puerta = puerta;
        super.textura = new Texture(Gdx.files.internal("boton.png"));
        super.crearYPosicionarCuerpo(this.ancho, this.alto);
    }

    public void activarConJugador(Jugador jugador) {
        if (!this.activado) {
            int slotLibre = jugador.getBarraInventario().getPrimeraCasillaLibre();
            if (slotLibre != -1) {
            	super.hiloServidor.enviarMensajeATodos("Desaparecer:LlaveActivadora");
            	super.hiloServidor.enviarMensajeATodos("ActualizarInventario:LlaveActivadora:"+jugador.getIdJugador()+":"+slotLibre);
                Texture texturaLlave = new Texture(Gdx.files.internal("boton.png"));
                jugador.getBarraInventario().setIcono(slotLibre, texturaLlave);
                this.puerta.desbloquear(super.hiloServidor);
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
