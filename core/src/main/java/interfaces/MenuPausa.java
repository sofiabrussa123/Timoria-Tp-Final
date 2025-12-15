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

        // Botón seguir
        TextButton btnSeguir = new TextButton("Seguir", super.fuenteTextos);
        btnSeguir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (MenuPausa.this.nivelPausado != null) {
                    MenuPausa.this.nivelPausado.despausar();
                    juego.setScreen(MenuPausa.this.nivelPausado);
                }
            }
        });

        // Botón menú - Notificar al servidor
        TextButton btnMenu = new TextButton("Menú", super.fuenteTextos);
        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (MenuPausa.this.nivelPausado != null) {
                    MenuPausa.this.nivelPausado.volverAlMenu();
                } else {
                    juego.setScreen(new Menu(juego));
                }
            }
        });

        // Botón instrucciones
        TextButton btnInstrucciones = new TextButton("Instrucciones", super.fuenteTextos);
        btnInstrucciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EsceneManager.setEscenaActual(MenuPausa.this);
                juego.setScreen(new Instrucciones(juego));
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
        // ✅ CORREGIDO: Renderizar el fondo del nivel pausado
        if (this.nivelPausado != null) {
            // Renderizar solo el fondo estático, sin actualizar física
            this.nivelPausado.renderFondoPausado(delta);
        } else {
            // Si no hay nivel, usar render base
            super.render(delta);
        }

        // Dibujar la UI del menú de pausa encima
        super.escena.act(delta);
        super.escena.draw();
    }
}
