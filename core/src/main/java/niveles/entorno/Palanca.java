package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

import Red.HiloServidor;

// Palanca activable que alterna entre dos estados
public class Palanca extends ElementoActivador {

    private float ancho = 20f;
    private float alto = 20f;
    private HiloServidor hiloServidor;

    public Palanca(World mundo, float x, float y, int id) {
        super(mundo, x, y, id);
        super.textura = new Texture(Gdx.files.internal("Palanca1.png"));
        super.crearYPosicionarCuerpo(this.ancho, this.alto);
    }

    // Setter para el servidor (se debe llamar después de la construcción)
    public void setHiloServidor(HiloServidor hiloServidor) {
        this.hiloServidor = hiloServidor;
    }

    public boolean getActivada() {
        return super.activado;
    }

    // Alterna el estado de la palanca entre activado y desactivado
    public void activar() {
        this.activado = !activado;

        if (activado) {
            super.textura = new Texture(Gdx.files.internal("Palanca2.png"));
            
            // Enviar mensaje de activación si el servidor está disponible
            if (hiloServidor != null) {
                hiloServidor.enviarMensajeATodos(
                    "ActivarPalanca:" + this.ID + ":" + 
                    this.cuerpo.getPosition().x + ":" + 
                    this.cuerpo.getPosition().y
                );
            }
        } else {
            super.textura = new Texture(Gdx.files.internal("Palanca1.png"));
        }
    }
}
