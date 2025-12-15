package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.World;

public class PuertaLlegada extends ElementoEntorno {

    private boolean estaBloqueada = true;

    public PuertaLlegada(float x, float y, int id) {
        super(x, y, 50, 95, id);
        textura = new Texture(Gdx.files.internal("puerta.png"));
    }

    public boolean sePuedeCruzar() {
        return !estaBloqueada;
    }

    public void desbloquear() {
        estaBloqueada = false;
<<<<<<< HEAD

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if (textura != null) {
                    textura.dispose();
                }
                textura = new Texture(Gdx.files.internal("puertaAbierta.png"));
            }
        });
=======
        textura = new Texture(Gdx.files.internal("puertaAbierta.png"));
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
    }
}
