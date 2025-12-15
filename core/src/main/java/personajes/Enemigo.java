package personajes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import interfaces.IdManager;
import niveles.NivelBase;

public class Enemigo extends Actor implements IdManager{

    private int vida = 50;
    private Texture textura;
    private float anchoHitbox;
    private float altoHitbox;
    private int daño = 10;
    private int cooldown = 1;
    private float tiempoTranscurrido = 0;
    private float alcanceAtaque = 0.15f;
    private boolean enCooldown = false;
    private boolean atacandoVisualmente = false;
    private float tiempoAtaqueVisual = 0.1f;
    private float contadorAtaqueVisual = 0f;
    private boolean muerto = false;
    private int id;

    public Enemigo(float x, float y, int id){
        this.id = id;
        this.textura = new Texture("enemigo.png");
        this.anchoHitbox = 48;
        this.altoHitbox = 48;

        setSize(anchoHitbox, altoHitbox);
        setPosition(x, y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        this.tiempoTranscurrido += delta;

        if (atacandoVisualmente) {
            contadorAtaqueVisual += delta;
            if (contadorAtaqueVisual >= tiempoAtaqueVisual) {
                atacandoVisualmente = false;
                contadorAtaqueVisual = 0f;
            }
        }

        if (this.tiempoTranscurrido >= cooldown && enCooldown) {
            enCooldown = false;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float xDraw = getX();
        float yDraw = getY();
        float anchoDraw = getWidth();
        float altoDraw = getHeight();

        if (atacandoVisualmente) {
            float alcancePixeles = alcanceAtaque / NivelBase.PIXELES_A_METROS;

            anchoDraw += 2 * alcancePixeles;
            altoDraw += 2 * alcancePixeles;

            xDraw -= alcancePixeles;
            yDraw -= alcancePixeles;
        }

        batch.draw(this.textura, xDraw, yDraw, anchoDraw, altoDraw);
    }

    // ✅ NUEVO: Método para mostrar animación de ataque (llamado desde red)
    public void mostrarAnimacionAtaque() {
        this.atacandoVisualmente = true;
        this.contadorAtaqueVisual = 0f;
    }

    public void aplicarDañoJugador(Jugador jugador) {
        jugador.recibirDaño(daño);
    }

    public void recibirDaño(int dañoAtaque) {
        if (dañoAtaque <= 0 || vida <= 0) return;

        if (vida - dañoAtaque < 0) {
            this.vida = 0;
        } else {
            this.vida -= dañoAtaque;
        }

        if (vida == 0) muerto = true;
    }

    public void eliminar() {
        this.remove();
    }

    public void actualizarPosicion(float x, float y) {
        float yd = y * 100 - (altoHitbox / 2);
        float xd = x * 100 - (anchoHitbox / 2);
        setPosition(xd, yd);
        System.out.println("👹 Enemigo visual actualizado a (" + x + ", " + y + ")");
    }

    public boolean getMuerto() {
        return this.muerto;
    }

    public void dispose() {
        textura.dispose();
    }

    @Override
    public int getId() {
        return this.id;
    }
}
