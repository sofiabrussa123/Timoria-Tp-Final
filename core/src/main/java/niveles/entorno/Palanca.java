package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public class Palanca extends ElementoEntorno {

    private Texture texturaDesactivada;
    private Texture texturaActivada;
    private boolean activado = false;

    public Palanca(float x, float y, int id) {
        super(x, y, 20f, 20f, id);

        // ✅ Cargar ambas texturas
        this.texturaDesactivada = new Texture(Gdx.files.internal("Palanca1.png"));
        this.texturaActivada = new Texture(Gdx.files.internal("Palanca2.png"));

        super.textura = texturaDesactivada; // Empezar desactivada
    }

    public boolean getActivada() {
        return this.activado;
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
