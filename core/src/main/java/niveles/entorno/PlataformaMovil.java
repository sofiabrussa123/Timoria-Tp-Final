package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
<<<<<<< HEAD
=======
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f

import niveles.NivelBase;

public class PlataformaMovil extends ElementoEntorno {

    private Palanca palancaActivadora;

<<<<<<< HEAD
=======
    // Constructor con palanca activadora
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
    public PlataformaMovil(float x, float y, Palanca palanca, int id) {
        super(x, y, 150, 20, id);

        if(palanca != null) {
            super.textura = new Texture(Gdx.files.internal("PlataformaActivable.png"));
            this.palancaActivadora = palanca;
        } else {
            super.textura = new Texture(Gdx.files.internal("PlataformaMovil.png"));
        }
    }

<<<<<<< HEAD
=======
    // Constructor sin palanca
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
    public PlataformaMovil(float x, float y, int id) {
        super(x, y, 150, 20, id);
        super.textura = new Texture(Gdx.files.internal("PlataformaMovil.png"));
    }

    @Override
    public void act(float delta) {
<<<<<<< HEAD
    }

    public void moverDesdeServidor(float posX, float posY) {

        float xPixeles = posX * 100f;
        float yPixeles = posY * 100f;

        float x = xPixeles - (ancho / 2f);
        float y = yPixeles - (alto / 2f);

        setPosition(x, y);
=======
        // ✅ NO llamar a super.act() - queremos posicionamiento MANUAL
        // super.act(delta) sincroniza desde el Body, pero nosotros queremos lo contrario

        // NO hacer nada - la posición viene del servidor via moverDesdeServidor()
    }

    // ✅ MÉTODO LLAMADO DESDE LA RED
    public void moverDesdeServidor(float posX, float posY) {

            // ✅ CRÍTICO: También actualizar la posición VISUAL del Actor
            setPosition(
                posX / NivelBase.PIXELES_A_METROS - super.ancho / 2,
                posY / NivelBase.PIXELES_A_METROS - super.alto / 2
            );
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
    }
}
