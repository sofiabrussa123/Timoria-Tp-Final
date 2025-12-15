package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import niveles.NivelBase;

public class PlataformaMovil extends ElementoEntorno {

    private Palanca palancaActivadora;

    public PlataformaMovil(float x, float y, Palanca palanca, int id) {
        super(x, y, 150, 20, id);

        if(palanca != null) {
            super.textura = new Texture(Gdx.files.internal("PlataformaActivable.png"));
            this.palancaActivadora = palanca;
        } else {
            super.textura = new Texture(Gdx.files.internal("PlataformaMovil.png"));
        }
    }

    public PlataformaMovil(float x, float y, int id) {
        super(x, y, 150, 20, id);
        super.textura = new Texture(Gdx.files.internal("PlataformaMovil.png"));
    }

    @Override
    public void act(float delta) {
    }

    public void moverDesdeServidor(float posX, float posY) {

        float xPixeles = posX * 100f;
        float yPixeles = posY * 100f;

        float x = xPixeles - (ancho / 2f);
        float y = yPixeles - (alto / 2f);

        setPosition(x, y);
    }
}
