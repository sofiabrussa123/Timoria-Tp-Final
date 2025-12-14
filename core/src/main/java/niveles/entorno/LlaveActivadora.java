package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;

import personajes.Jugador;

/**
 * Llave que puede ser recogida por el jugador para desbloquear una puerta.
 * En el servidor headless, solo maneja la lógica sin gráficos.
 */
public class LlaveActivadora extends ElementoActivador {

    private PuertaLlegada puerta;
    private float ancho = 30f;
    private float alto = 45f;

    public LlaveActivadora(World mundo, float x, float y, PuertaLlegada puerta, int id) {
        super(mundo, x, y, id);
        this.puerta = puerta;

        // ✅ Solo cargar textura si hay gráficos disponibles
        if (Gdx.graphics != null) {
            super.textura = new Texture(Gdx.files.internal("boton.png"));
        }

        super.crearYPosicionarCuerpo(this.ancho, this.alto);
    }

    /**
     * Intenta activar la llave cuando un jugador colisiona con ella.
     * @param personaje El jugador que intenta recoger la llave
     */
    public void activarConJugador(Jugador personaje) {
        if (!this.activado) {
            // ✅ Verificar si el jugador tiene inventario (solo en cliente con UI)
            if (personaje.getBarraInventario() != null) {
                int slotLibre = personaje.getBarraInventario().getPrimeraCasillaLibre();

                if (slotLibre != -1) {
                    // Agregar llave al inventario (solo en cliente)
                    Texture texturaLlave = new Texture(Gdx.files.internal("boton.png"));
                    personaje.getBarraInventario().setIcono(slotLibre, texturaLlave);
                    System.out.println("✅ Llave agregada al inventario en slot " + slotLibre);
                } else {
                    System.out.println("⚠️ Inventario lleno. No se puede recoger la llave.");
                }
            } else {
                // En servidor headless, solo registrar el evento
                System.out.println("🔑 Jugador " + personaje.getIdJugador() + " recogió la llave (servidor)");
            }

            // ✅ Desbloquear puerta (tanto en servidor como cliente)
            this.puerta.desbloquear();
            this.activado = true;

            // Remover del escenario si existe
            if (getStage() != null) {
                this.remove();
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Solo dibujar si no ha sido activada y hay Stage
        if (!activado && getStage() != null) {
            super.draw(batch, parentAlpha);
        }
    }

    @Override
    public void dispose() {
        // ✅ Disponer textura solo si fue creada
        if (textura != null) {
            textura.dispose();
        }
        super.dispose();
    }
}
