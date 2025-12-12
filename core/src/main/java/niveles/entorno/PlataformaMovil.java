package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import niveles.NivelBase;

public class PlataformaMovil extends ElementoEntorno {

    public PlataformaMovil(World mundo, float x, float y, Palanca palanca, int id) {
        super(mundo, x, y, 150, 20, id);
        if(palanca != null) {
        	super.textura = new Texture(Gdx.files.internal("PlataformaActivable.png"));
        } else super.textura = new Texture(Gdx.files.internal("PlataformaMovil.png"));

        setTipoCuerpo(BodyDef.BodyType.KinematicBody);
        super.crearYPosicionarCuerpo();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
    
    public void mover(int posX, int posY) {
        if (super.cuerpo != null) {
            super.cuerpo.setTransform(
                posX * NivelBase.PIXELES_A_METROS, 
                posY * NivelBase.PIXELES_A_METROS, 
                super.cuerpo.getAngle()
            );
            super.cuerpo.setLinearVelocity(0, 0); 
        }
    }
}
