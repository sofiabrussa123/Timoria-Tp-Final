package interfaces;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import Red.HiloCliente;
import niveles.EscenaBase;

public class SecuenciaImagenes extends EscenaBase {

    private String[] rutasImagenes;
    private int imagenActual = 0;
    private Game juego;
    private Class<?> siguienteEscenaClase;
    private Texture[] texturas;
    private boolean esperandoInput = false;
    private float tiempoEspera = 0.3f; // Evita avance accidental
    private float tiempoTranscurrido = 0f;
    private HiloCliente hiloCliente;

    public SecuenciaImagenes(Game juego, String[] rutasImagenes) {
        super(juego, rutasImagenes[0]);
        this.juego = juego;
        this.rutasImagenes = rutasImagenes;

        // Cargar todas las texturas
        this.texturas = new Texture[rutasImagenes.length];
        for (int i = 0; i < rutasImagenes.length; i++) {
            this.texturas[i] = new Texture(Gdx.files.internal(rutasImagenes[i]));
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.inputManager);
        esperandoInput = false;
        tiempoTranscurrido = 0f;
    }

    @Override
    public void render(float delta) {
        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tiempoTranscurrido += delta;

        // Dibujar la imagen actual
        escena.getBatch().begin();
        if (imagenActual < texturas.length) {
            Texture texActual = texturas[imagenActual];
            escena.getBatch().draw(
                texActual,
                0,
                0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
            );
        }
        escena.getBatch().end();

        // Después del tiempo de espera, permitir input
        if (tiempoTranscurrido > tiempoEspera) {
            esperandoInput = true;
        }

        // Avanzar con input
        if (esperandoInput && (this.inputManager.getIsEPressed() ||
            this.inputManager.getIsOPressed() ||
            Gdx.input.justTouched())) {

            imagenActual++;
            tiempoTranscurrido = 0f;
            esperandoInput = false;

            if (imagenActual >= rutasImagenes.length) {
                this.hiloCliente.enviarMensaje("FinHistoria");
            }
        }

        escena.act(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        // Liberar todas las texturas
        for (Texture tex : texturas) {
            if (tex != null) {
                tex.dispose();
            }
        }
    }
}
