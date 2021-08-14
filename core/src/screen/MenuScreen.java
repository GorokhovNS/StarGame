package screen;

import base.BaseScreen;
import math.Rect;
import sprite.Background;
import sprite.ExitButton;
import sprite.PlayButton;
import sprite.Star;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

public class MenuScreen extends BaseScreen {
    private static final int STAR_COUNT = 120;
    private final Game game;


    private Background background;
    private Texture bg;
    private Vector2 pos;
    private TextureAtlas atlas;
    private Star[] stars;
    private ExitButton exitButton;
    private PlayButton playButton;


    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        super.show();
        bg = new Texture("planet-earth-in-space.jpg");
        background = new Background(bg);
        atlas = new TextureAtlas("menuAtlas.tpack");
        stars = new Star[STAR_COUNT];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star(atlas);
        }
exitButton = new ExitButton(atlas);
        playButton = new PlayButton (atlas, game);


    }

    @Override
    public void resize(Rect worldBounds) {
        background.resize(worldBounds);
        for (Star star : stars) {
            star.resize(worldBounds);
        }
        exitButton.resize(worldBounds);
        playButton.resize(worldBounds);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
update(delta);
draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        bg.dispose();
        atlas.dispose();
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        exitButton.touchDown(touch, pointer, button);
        playButton.touchDown(touch, pointer, button);
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        exitButton.touchUp(touch, pointer, button);
        playButton.touchUp(touch, pointer, button);
        return false;
    }

    private void update(float delta) {
    for (Star star : stars) {
        star.update(delta);
    }
    }

    private void draw() {
        batch.begin();
        background.draw(batch);
        for (Star star : stars) {
            star.draw(batch);
        }
        exitButton.draw(batch);
        playButton.draw(batch);
        batch.end();
    }
}

