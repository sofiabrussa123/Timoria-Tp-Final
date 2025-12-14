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
    private EscenaBase siguienteEscena;
    private Texture[] texturas;
    private boolean esperandoInput = false;
    private float tiempoEspera = 0.3f; // Evita avance accidental
    private float tiempoTranscurrido = 0f;
    private HiloCliente hiloCliente;

    public SecuenciaImagenes(Game juego, String[] rutasImagenes, EscenaBase siguienteEscena) {
        super(juego, rutasImagenes[0]);
        this.juego = juego;
        this.rutasImagenes = rutasImagenes;
        this.siguienteEscena = siguienteEscena;

        // Cargar todas las texturas
        this.texturas = new Texture[rutasImagenes.length];
        for (int i = 0; i < rutasImagenes.length; i++) {
            this.texturas[i] = new Texture(Gdx.files.internal(rutasImagenes[i]));
        }
    }
    
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
