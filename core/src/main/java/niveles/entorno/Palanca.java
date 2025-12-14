package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public class Palanca extends ElementoActivador {

    private float ancho = 20f;
    private float alto = 20f;
    private Texture texturaDesactivada;
    private Texture texturaActivada;

    public Palanca(World mundo, float x, float y, int id) {
        super(mundo, x, y, id);

        // ✅ Cargar ambas texturas
        this.texturaDesactivada = new Texture(Gdx.files.internal("Palanca1.png"));
        this.texturaActivada = new Texture(Gdx.files.internal("Palanca2.png"));

        super.textura = texturaDesactivada; // Empezar desactivada

        super.crearYPosicionarCuerpo(this.ancho, this.alto);
    }

    public boolean getActivada() {
        return super.activado;
    }

    // ✅ Alternar el estado de la palanca (llamado desde contactos O desde red)
    public void activar() {
        this.activado = !activado;

        if (activado) {
            super.textura = texturaActivada;
        } else {
            super.textura = texturaDesactivada;
        }

        System.out.println("🔧 Palanca " + this.getId() + " ahora está: " + (activado ? "ACTIVADA" : "DESACTIVADA"));
    }
}
