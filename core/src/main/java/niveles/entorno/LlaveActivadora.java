package niveles.entorno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

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
        if (activado) return;

        if (personaje.getHiloCliente() != null) {
            String mensaje = "Llave:" + this.getId() + ":Recoger:" + personaje.getId();
            personaje.getHiloCliente().enviarMensaje(mensaje);

            activado = true;
        } else {
            System.err.println("⚠️ [CLIENTE] HiloCliente es null en jugador " + personaje.getId());
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!activado) {
            super.draw(batch, parentAlpha);
        }
    }
}
