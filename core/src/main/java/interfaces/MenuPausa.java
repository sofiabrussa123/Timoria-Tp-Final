package interfaces;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import globales.EsceneManager;
import niveles.EscenaBase;
import niveles.NivelBase;

public class MenuPausa extends EscenaBase {

    private NivelBase nivelPausado;

    public MenuPausa(Game juego) {
        super(juego, "FondoTransparente.png");

        super.fuenteTextos = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);

        TextButton btnSeguir = new TextButton("Seguir", super.fuenteTextos);
        btnSeguir.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                MenuPausa.this.nivelPausado.despausar();
                cambiarEscena(MenuPausa.this.nivelPausado);
            }
        });

        TextButton btnMenu = new TextButton("Menú", super.fuenteTextos);
        btnMenu.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                cambiarEscena(new Menu(juego));
            }
        });

        TextButton btnInstrucciones = new TextButton("Instrucciones", super.fuenteTextos);
        btnInstrucciones.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                EsceneManager.setEscenaActual(MenuPausa.this);
                cambiarEscena(new Instrucciones(juego));
            }
        });

        table.center();
        table.add(btnSeguir).pad(10).row();
        table.add(btnMenu).pad(10).row();
        table.add(btnInstrucciones).pad(10);

        super.escena.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.escena);

        if (EsceneManager.getEscenaActual() instanceof NivelBase) {
            this.nivelPausado = (NivelBase) EsceneManager.getEscenaActual();
        }
    }

    @Override
    public void render(float delta) {
        this.nivelPausado.draw(delta);
        super.escena.act(delta);
        super.escena.draw();
    }
}
