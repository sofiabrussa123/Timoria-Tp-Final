package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import niveles.NivelBase;

public class PlataformaMovil extends ElementoEntorno {

    private Palanca palancaActivadora;

    // Constructor con palanca activadora
    public PlataformaMovil(World mundo, float x, float y, Palanca palanca, int id) {
        super(mundo, x, y, 150, 20, id);

        if(palanca != null) {
            super.textura = new Texture(Gdx.files.internal("PlataformaActivable.png"));
            this.palancaActivadora = palanca;
        } else {
            super.textura = new Texture(Gdx.files.internal("PlataformaMovil.png"));
        }

        // ✅ KINEMATIC para que NO tenga gravedad pero SÍ colisione
        setTipoCuerpo(BodyDef.BodyType.KinematicBody);
        super.crearYPosicionarCuerpo();

        // ✅ IMPORTANTE: Desactivar la velocidad inicial
        if (super.cuerpo != null) {
            super.cuerpo.setLinearVelocity(0, 0);
        }
    }

    // Constructor sin palanca
    public PlataformaMovil(World mundo, float x, float y, int id) {
        super(mundo, x, y, 150, 20, id);
        super.textura = new Texture(Gdx.files.internal("PlataformaMovil.png"));

        setTipoCuerpo(BodyDef.BodyType.KinematicBody);
        super.crearYPosicionarCuerpo();

        if (super.cuerpo != null) {
            super.cuerpo.setLinearVelocity(0, 0);
        }
    }

    @Override
    public void act(float delta) {
        // ✅ NO llamar a super.act() - queremos posicionamiento MANUAL
        // super.act(delta) sincroniza desde el Body, pero nosotros queremos lo contrario

        // NO hacer nada - la posición viene del servidor via moverDesdeServidor()
    }

    // ✅ MÉTODO LLAMADO DESDE LA RED
    public void moverDesdeServidor(float posX, float posY) {
        if (super.cuerpo != null) {
            // ✅ Actualizar DIRECTAMENTE sin interpolación (plataformas deben ser precisas)
            super.cuerpo.setTransform(posX, posY, 0);
            super.cuerpo.setLinearVelocity(0, 0);
            super.cuerpo.setAwake(true); // Mantener activo para colisiones

            // ✅ CRÍTICO: También actualizar la posición VISUAL del Actor
            setPosition(
                posX / NivelBase.PIXELES_A_METROS - super.ancho / 2,
                posY / NivelBase.PIXELES_A_METROS - super.alto / 2
            );

            //System.out.println("🔄 PlataformaMovil " + this.getId() + " → (" + posX + ", " + posY + ")");
        }
    }
}
