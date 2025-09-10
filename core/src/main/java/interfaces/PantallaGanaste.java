package interfaces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.timoria.Principal;
import niveles.EscenaBase;

public class PantallaGanaste extends EscenaBase {

    public PantallaGanaste(Principal juego) {
    	super(juego, "FondoTransparente.png");
        super.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));

        Label mensaje = new Label("¡Ganaste!", super.fuenteTextos);
        mensaje.setFontScale(2);

        TextButton btnMenu = new TextButton("Menú", super.fuenteTextos);
        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cambiarEscena(new Menu(juego));
            }
        });

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();
        tabla.add(mensaje).pad(10);
        tabla.row();
        tabla.add(btnMenu).pad(10);

        super.escena.addActor(tabla);
    }

    @Override
    public void render(float delta) {
    	
    	//Convertir el fondo a sprite para hacerlo transparente
    	Sprite fondoTransparente = new Sprite(this.fondo);

        batch.begin();
        fondoTransparente.setSize(super.escena.getViewport().getWorldWidth(), super.escena.getViewport().getWorldHeight());
        fondoTransparente.setPosition(0, 0);
        fondoTransparente.draw(batch); 
        batch.end();
    	super.escena.act(delta);
        super.escena.draw();
    }
}