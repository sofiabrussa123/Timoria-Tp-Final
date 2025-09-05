package io.github.timoria;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import personajes.Personaje;

public class BarraVida {

    private ShapeRenderer renderer;
    private Personaje jugador;

    public BarraVida(Personaje jugador) {
        this.jugador = jugador;
        this.renderer = new ShapeRenderer();
    }

    public void render() {
        int vida = jugador.getVida();
        int vidaMax = jugador.getVidaMaxima();

        float porcentaje = (float) vida / vidaMax;
        float anchoBarra = 200;
        float altoBarra = 20;

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Fondo gris
        renderer.setColor(Color.DARK_GRAY);
        renderer.rect(10, 760, anchoBarra, altoBarra); // esquina superior izquierda

        // Vida roja
        renderer.setColor(Color.RED);
        renderer.rect(10, 760, anchoBarra * porcentaje, altoBarra);

        renderer.end();
    }

    public void dispose() {
        renderer.dispose();
    }
}
