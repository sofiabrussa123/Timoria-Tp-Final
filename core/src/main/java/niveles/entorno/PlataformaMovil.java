package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import Red.HiloServidor;
import io.github.timoria.Principal;

// Plataforma que se mueve automáticamente o al activar una palanca
public class PlataformaMovil extends ElementoEntorno {

    private int direccionMovimiento;
    private int distanciaMovimiento;
    private Palanca palancaActivadora;
    private float velocidadMovimiento = 1.5f;
    private float posicionInicial;
    private float dx = 0;
    private float dy = 0;
    private HiloServidor hiloServidor;

    // Constructor con palanca activadora (plataforma requiere activación)
    public PlataformaMovil(World mundo, float x, float y, int direccionMovimiento, 
                          int distanciaMovimiento, Palanca palanca, int id) {
        super(mundo, x, y, 150, 20, id);
        super.textura = new Texture(Gdx.files.internal("PlataformaActivable.png"));
        this.direccionMovimiento = direccionMovimiento;
        this.distanciaMovimiento = (int) (distanciaMovimiento / Principal.PPM);
        this.palancaActivadora = palanca;
        
        inicializarMovimiento();
    }

    // Constructor sin palanca (plataforma siempre activa)
    public PlataformaMovil(World mundo, float x, float y, int direccionMovimiento, 
                          int distanciaMovimiento, int id) {
        super(mundo, x, y, 150, 20, id);
        super.textura = new Texture(Gdx.files.internal("PlataformaMovil.png"));
        this.direccionMovimiento = direccionMovimiento;
        this.distanciaMovimiento = (int) (distanciaMovimiento / Principal.PPM);
        
        inicializarMovimiento();
    }

    // Setter para el servidor (se debe llamar después de la construcción)
    public void setHiloServidor(HiloServidor hiloServidor) {
        this.hiloServidor = hiloServidor;
    }

    // Inicializa la dirección del movimiento y crea el cuerpo físico
    private void inicializarMovimiento() {
        switch (this.direccionMovimiento) {
            case 1: dy = 1; break; // Vertical
            case 2: dx = 1; break; // Horizontal
        }

        setTipoCuerpo(BodyDef.BodyType.KinematicBody);
        super.crearYPosicionarCuerpo();

        this.posicionInicial = (dx != 0) ? cuerpo.getPosition().x : cuerpo.getPosition().y;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Si hay palanca y no está activada, detener movimiento
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

        // Sincronizar posición en red si el servidor está disponible
        if (hiloServidor != null) {
            hiloServidor.enviarMensajeATodos(
                "PlataformaMovil:" + this.ID + ":Mover:" + 
                this.cuerpo.getPosition().x + ":" + 
                this.cuerpo.getPosition().y
            );
        }
    }

    // Invierte la dirección del movimiento al llegar a los límites
    private void cambiarSentidoMovimiento(float posicion, float posicionInicial, int distanciaMovimiento) {
        if (posicion >= posicionInicial + distanciaMovimiento || posicion <= posicionInicial) {
            this.velocidadMovimiento = -this.velocidadMovimiento;
        }
    }
}
