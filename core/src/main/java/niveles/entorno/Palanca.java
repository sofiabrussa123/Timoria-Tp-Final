package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
<<<<<<< HEAD
=======
import com.badlogic.gdx.physics.box2d.World;
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f

public class Palanca extends ElementoEntorno {

    private Texture texturaDesactivada;
    private Texture texturaActivada;
    private boolean activado = false;

    public Palanca(float x, float y, int id) {
        super(x, y, 20f, 20f, id);

<<<<<<< HEAD
        this.texturaDesactivada = new Texture(Gdx.files.internal("Palanca1.png"));
        this.texturaActivada = new Texture(Gdx.files.internal("Palanca2.png"));

        super.textura = texturaDesactivada;
=======
        // ✅ Cargar ambas texturas
        this.texturaDesactivada = new Texture(Gdx.files.internal("Palanca1.png"));
        this.texturaActivada = new Texture(Gdx.files.internal("Palanca2.png"));

        super.textura = texturaDesactivada; // Empezar desactivada
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
    }

    public boolean getActivada() {
        return this.activado;
    }

<<<<<<< HEAD
=======
    // ✅ Alternar el estado de la palanca (llamado desde contactos O desde red)
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
    public void activar() {
        this.activado = !activado;

        if (activado) {
            super.textura = texturaActivada;
        } else {
            super.textura = texturaDesactivada;
        }
<<<<<<< HEAD
=======

        System.out.println("🔧 Palanca " + this.getId() + " ahora está: " + (activado ? "ACTIVADA" : "DESACTIVADA"));
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
    }
}
