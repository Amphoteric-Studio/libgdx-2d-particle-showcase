package com.particle.showcase;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Generic Scene2D 2D particle showcase adapted from EfectosPruebas.
 */
public class ParticleShowcase extends ApplicationAdapter {
    private static final float WORLD_WIDTH = 1280f*2.5f;
    private static final float WORLD_HEIGHT = 720f*2.5f;

    private Stage stage;
    private Skin skin;
    private final Array<ParticleEffect> loadedEffects = new Array<>();

    private static class ParticleEffectActor extends Actor {
        private final ParticleEffect effect;

        ParticleEffectActor(ParticleEffect effect, float x, float y) {
            this.effect = effect;
            setPosition(x, y);
            effect.setPosition(x, y);
        }

        void startEffect() {
            effect.setPosition(getX(), getY());
            effect.start();
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            effect.update(delta);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            effect.draw(batch);
        }
    }

    @Override
    public void create() {
        stage = new Stage(new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        initialize();
    }

    public void initialize() {
        float screenWidth = stage.getWidth();
        if (screenWidth <= 0) screenWidth = 1920f;

        // --- FILA 1 (4 elementos): Y partículas = 1500 | Y botones = 1300 ---
        float colWidth1 = screenWidth / 4f;

        crearGrupoConBoton("Magic", 5, 120f, colWidth1 * 0.5f, 1500f, 1300f, "efect/efectosmagiaoscuraohechicera/Prueba de hechizo oscuro ");
        crearGrupoConBoton("Water", 6, 120f, colWidth1 * 1.5f, 1500f, 1300f, "efect/pruebadeefectosagua/");
        crearGrupoConBoton("Electricity", 5, 120f, colWidth1 * 2.5f, 1500f, 1300f, "efect/pruebadeefectoselectricidad/");
        crearGrupoConBoton("Fire", 4, 120f, colWidth1 * 3.5f, 1500f, 1300f, "efect/pruebadeefectosfuego/");

        // --- FILA 2 (4 elementos): Y partículas = 1000 | Y botones = 800 ---
        float colWidth2 = screenWidth / 4f;

        crearGrupoConBoton("Ice", 5, 120f, colWidth2 * 0.5f, 1000f, 800f, "efect/pruebadeefectoshielo/");
        crearGrupoConBoton("Magic 2", 6, 160f, colWidth2 * 1.5f, 1000f, 800f, "efect/pruebadeefectosmagia/");
        crearGrupoConBoton("Wind", 4, 120f, colWidth2 * 2.5f, 1000f, 800f, "efect/pruebadeefectosparte1viento/");
        crearGrupoConBoton("Blood", 4, 120f, colWidth2 * 3.5f, 1000f, 800f, "efect/pruebadeefectossangre/");

        // --- FILA 3 (3 elementos): Y partículas = 500 | Y botones = 300 ---
        float colWidth3 = screenWidth / 3f;

        crearGrupoConBoton("Earth", 4, 160f, colWidth3 * 0.5f, 500f, 300f, "efect/pruebadeefectostierra/");
        crearGrupoConBoton("Poison", 5, 160f, colWidth3 * 1.5f, 500f, 300f, "efect/pruebadeefectosveneno/");
        crearGrupoConBoton("Nature", 6, 120f, colWidth3 * 2.5f, 500f, 300f, "efect/pruebaefectonaturaleza/");
    }

    private void crearGrupoConBoton(
        String nombreBoton,
        int cantidad,
        float separacionX,
        float centroXColumna,
        float yParticulas,
        float yBoton,
        String rutaBase
    ) {
        float anchoGrupo = (cantidad - 1) * separacionX;
        float startX = centroXColumna - (anchoGrupo / 2f);

        final Array<ParticleEffectActor> efectos = new Array<>(cantidad);

        for (int n = 0; n < cantidad; n++) {
            String baseFilePath = rutaBase + (n + 1);
            ParticleEffect effect = loadParticleEffect(baseFilePath);
            if (effect != null) {
                effect.scaleEffect(1.5f);
                loadedEffects.add(effect);

                ParticleEffectActor actor = new ParticleEffectActor(effect, startX + (separacionX * n), yParticulas);
                efectos.add(actor);
                stage.addActor(actor);
                actor.startEffect();
            }
        }

        final TextButton button = new TextButton(nombreBoton, skin);
        button.getLabel().setFontScale(5f);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (ParticleEffectActor efecto : efectos) {
                    efecto.startEffect();
                }
            }
        });

        button.pack();
        button.setPosition(
            MathUtils.roundPositive(centroXColumna - (button.getWidth() / 2f)),
            MathUtils.roundPositive(yBoton)
        );

        stage.addActor(button);
    }

    private ParticleEffect loadParticleEffect(String baseFilePath) {
        FileHandle effectFile = Gdx.files.internal(baseFilePath);
        if (!effectFile.exists()) {
            FileHandle withExt = Gdx.files.internal(baseFilePath + ".p");
            if (withExt.exists()) {
                effectFile = withExt;
            } else {
                Gdx.app.log("ParticleShowcase", "Effect not found: " + baseFilePath);
                return null;
            }
        }

        ParticleEffect effect = new ParticleEffect();
        effect.load(effectFile, effectFile.parent());
        return effect;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        update(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        for (ParticleEffect effect : loadedEffects) {
            effect.dispose();
        }
        loadedEffects.clear();
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
    }
}
