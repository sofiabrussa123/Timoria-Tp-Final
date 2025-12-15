package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

import Red.HiloServidor;
import personajes.Jugador;

public class LlaveActivadora extends ElementoActivador {

    private PuertaLlegada puerta;
    private float ancho = 30f;
    private float alto = 45f;
    private HiloServidor hiloServidor;

    public LlaveActivadora(World mundo, float x, float y, PuertaLlegada puerta, int id) {
        super(mundo, x, y, id);
        this.puerta = puerta;

        if (Gdx.graphics != null) {
            super.textura = new Texture(Gdx.files.internal("boton.png"));
        }

        super.crearYPosicionarCuerpo(this.ancho, this.alto);
    }

    public void setHiloServidor(HiloServidor hiloServidor) {
        this.hiloServidor = hiloServidor;
    }

    public void activarConJugador(Jugador personaje) {
        if (!this.activado) {
            this.puerta.desbloquear();
            this.activado = true;
        }
    }

    @Override
    public void dispose() {
        if (textura != null) {
            textura.dispose();
        }
    }
}
