package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import io.github.timoria.Principal;

public class PlataformaMovil extends ElementoEntorno {

    private int direccionMovimiento;
    private int distanciaMovimiento;
    private Palanca palancaActivadora;
    private float velocidadMovimiento = 1.5f;
    private float posicionInicial;
    private float dx = 0;
    private float dy = 0;

    public PlataformaMovil(World mundo, float x, float y, int direccionMovimiento, int distanciaMovimiento, Palanca palanca) {
        super(mundo, x, y, 150, 20);
        super.textura = new Texture(Gdx.files.internal("PlataformaActivable.png"));
        this.direccionMovimiento = direccionMovimiento;
        this.distanciaMovimiento = (int) (distanciaMovimiento / Principal.PPM);
        this.palancaActivadora = palanca;

        switch (this.direccionMovimiento) {
            case 1: dy = 1; break;
            case 2: dx = 1; break;
        }

        setTipoCuerpo(BodyDef.BodyType.KinematicBody);
        super.crearYPosicionarCuerpo();

        this.posicionInicial = (dx != 0) ? cuerpo.getPosition().x : cuerpo.getPosition().y;
    }

    public PlataformaMovil(World mundo, float x, float y, int direccionMovimiento, int distanciaMovimiento) {
        super(mundo, x, y, 150, 20);
        super.textura = new Texture(Gdx.files.internal("PlataformaMovil.png"));
        this.direccionMovimiento = direccionMovimiento;
        this.distanciaMovimiento = (int) (distanciaMovimiento / Principal.PPM);

        switch (this.direccionMovimiento) {
            case 1: dy = 1; break;
            case 2: dx = 1; break;
        }

        setTipoCuerpo(BodyDef.BodyType.KinematicBody);
        super.crearYPosicionarCuerpo();

        this.posicionInicial = (dx != 0) ? cuerpo.getPosition().x : cuerpo.getPosition().y;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (palancaActivadora != null && !palancaActivadora.getActivada()) {
            cuerpo.setLinearVelocity(0, 0);
            return;
        }

        float posicion = (dx != 0) ? cuerpo.getPosition().x : cuerpo.getPosition().y;

        if (velocidadMovimiento > 0) {
            if (posicion < posicionInicial + distanciaMovimiento) {
                cuerpo.setLinearVelocity(velocidadMovimiento * dx, velocidadMovimiento * dy);
            }
            cambiarSentidoMovimiento(posicion, posicionInicial, distanciaMovimiento);
        } else {
            if (posicion > posicionInicial) {
                cuerpo.setLinearVelocity(velocidadMovimiento * dx, velocidadMovimiento * dy);
            }
            cambiarSentidoMovimiento(posicion, posicionInicial, distanciaMovimiento);
        }
    }

    private void cambiarSentidoMovimiento(float posicion, float posicionInicial, int distanciaMovimiento) {
        if (posicion >= posicionInicial + distanciaMovimiento || posicion <= posicionInicial) {
            this.velocidadMovimiento = -this.velocidadMovimiento;
        }
    }
}
