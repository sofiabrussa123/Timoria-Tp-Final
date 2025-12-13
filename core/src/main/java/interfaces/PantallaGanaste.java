package interfaces;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import niveles.EscenaBase;

public class PantallaGanaste extends EscenaBase {

    public PantallaGanaste(Game juego) {
        super(juego, "FondoTransparente.png");

        super.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));

        Label mensaje = new Label("¡Ganaste!", super.fuenteTextos);
        mensaje.setFontScale(2);


        TextButton btnContinuar = new TextButton("Continuar", super.fuenteTextos);
        btnContinuar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String[] imagenesEpilogo = {
                    "6.png",
                    "7.png",
                    "8.png"
                };

                // Pasamos Menu.class en lugar de new Menu(juego)
                cambiarEscena(new SecuenciaImagenes(juego, imagenesEpilogo, Menu.class));
            }
        });

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();
        tabla.add(mensaje).pad(10).row();
        tabla.add(btnContinuar).pad(10);

        super.escena.addActor(tabla);
    }

    @Override
    public void render(float delta) {
        super.escena.act(delta);
        super.escena.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(escena);
    }
}
