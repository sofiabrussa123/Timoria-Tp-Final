package niveles.entorno;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import Red.HiloServidor;
import io.github.timoria.Principal;

public class PlataformaMovil extends ElementoEntorno {

    private int direccionMovimiento;
    private int distanciaMovimiento;
    private Palanca palancaActivadora;
    private float velocidadMovimiento = 1.5f;
    private float posicionInicial;
    private float dx = 0;
    private float dy = 0;
    private HiloServidor hiloServidor;

    // ✅ Control de sincronización con delta compression
    private float tiempoDesdeUltimaSincronizacion = 0f;
    private static final float INTERVALO_SINCRONIZACION = 0.033f; // 30Hz

    private float posXAnterior = 0f;
    private float posYAnterior = 0f;
    private static final float UMBRAL_MOVIMIENTO = 0.05f; // 5cm

    // Constructor con palanca
    public PlataformaMovil(World mundo, float x, float y, int direccionMovimiento,
                           int distanciaMovimiento, Palanca palanca, int id) {
        super(mundo, x, y, 150, 20, id);
        this.direccionMovimiento = direccionMovimiento;
        this.distanciaMovimiento = (int) (distanciaMovimiento / Principal.PPM);
        this.palancaActivadora = palanca;

        inicializarMovimiento();
    }

    // Constructor sin palanca
    public PlataformaMovil(World mundo, float x, float y, int direccionMovimiento,
                           int distanciaMovimiento, int id) {
        super(mundo, x, y, 150, 20, id);
        this.direccionMovimiento = direccionMovimiento;
        this.distanciaMovimiento = (int) (distanciaMovimiento / Principal.PPM);

        inicializarMovimiento();
    }

    public void setHiloServidor(HiloServidor hiloServidor) {
        this.hiloServidor = hiloServidor;
    }

    private void inicializarMovimiento() {
        switch (this.direccionMovimiento) {
            case 1: dy = 1; break; // Vertical
            case 2: dx = 1; break; // Horizontal
        }

        setTipoCuerpo(BodyDef.BodyType.KinematicBody);
        super.crearYPosicionarCuerpo();

        this.posicionInicial = (dx != 0) ? cuerpo.getPosition().x : cuerpo.getPosition().y;
        this.posXAnterior = cuerpo.getPosition().x;
        this.posYAnterior = cuerpo.getPosition().y;
    }

    public void act(float delta) {
        tiempoDesdeUltimaSincronizacion += delta;

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

        // ✅ Sincronizar con delta compression
        if (hiloServidor != null && tiempoDesdeUltimaSincronizacion >= INTERVALO_SINCRONIZACION) {
            float posX = this.cuerpo.getPosition().x;
            float posY = this.cuerpo.getPosition().y;

            // Calcular distancia desde última sincronización
            float distancia = (float) Math.sqrt(
                Math.pow(posX - posXAnterior, 2) + Math.pow(posY - posYAnterior, 2)
            );

            // Solo enviar si se movió significativamente
            if (distancia > UMBRAL_MOVIMIENTO) {
                String mensaje = String.format(java.util.Locale.US, "PlataformaMovil:%d:%.2f:%.2f",
                    this.ID, posX, posY);

                hiloServidor.enviarMensajeATodos(mensaje);

                posXAnterior = posX;
                posYAnterior = posY;
            }

            tiempoDesdeUltimaSincronizacion = 0f;
        }
    }

    private void cambiarSentidoMovimiento(float posicion, float posicionInicial, int distanciaMovimiento) {
        if (posicion >= posicionInicial + distanciaMovimiento || posicion <= posicionInicial) {
            this.velocidadMovimiento = -this.velocidadMovimiento;
        }
    }
}
