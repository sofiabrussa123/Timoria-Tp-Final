package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
<<<<<<< HEAD

=======
import com.badlogic.gdx.physics.box2d.World;
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
import personajes.Jugador;

public class LlaveActivadora extends ElementoEntorno {

    private PuertaLlegada puerta;
    private boolean activado = false;

    public LlaveActivadora(float x, float y, PuertaLlegada puerta, int id) {
        super(x, y, 30f, 45f, id);
        this.puerta = puerta;
        super.textura = new Texture(Gdx.files.internal("boton.png"));
    }

    public void activarConJugador(Jugador personaje) {
<<<<<<< HEAD
        if (activado) return;

        if (personaje.getHiloCliente() != null) {
            String mensaje = "Llave:" + this.getId() + ":Recoger:" + personaje.getId();
            personaje.getHiloCliente().enviarMensaje(mensaje);

            activado = true;
        } else {
            System.err.println("⚠️ [CLIENTE] HiloCliente es null en jugador " + personaje.getId());
=======
        if (!this.activado) {
            int slotLibre = personaje.getBarraInventario().getPrimeraCasillaLibre();
            if (slotLibre != -1) {
                Texture texturaLlave = new Texture(Gdx.files.internal("boton.png"));
                personaje.getBarraInventario().setIcono(slotLibre, texturaLlave);
                this.puerta.desbloquear();
                this.activado = true;
                this.remove();
            } else {
                System.out.println("Inventario lleno. No se puede recoger la llave.");
            }
>>>>>>> c3bf54b67a97052204f07f4e9aa74e62d547785f
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!activado) {
            super.draw(batch, parentAlpha);
        }
    }
}
