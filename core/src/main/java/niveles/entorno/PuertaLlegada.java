package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

import Red.HiloServidor;

public class PuertaLlegada extends ElementoEntorno {

    private boolean estaBloqueada = true;
    private HiloServidor hiloServidor;

    public PuertaLlegada(World mundo, float x, float y, int id) {
        super(mundo, x, y, 50, 95, id);
        textura = new Texture(Gdx.files.internal("puerta.png"));
        super.crearYPosicionarCuerpo();
    }

    public void setHiloServidor(HiloServidor hiloServidor) {
        this.hiloServidor = hiloServidor;
    }

    public boolean sePuedeCruzar() {
        return !estaBloqueada;
    }

    public void desbloquear() {
        estaBloqueada = false;
        textura = new Texture(Gdx.files.internal("puertaAbierta.png"));

        if (hiloServidor != null) {
            hiloServidor.enviarMensajeATodos("Puerta:" + this.ID+":Desbloquear");
        }
    }
}
