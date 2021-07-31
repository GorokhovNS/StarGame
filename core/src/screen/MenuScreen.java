package screen;

import base.BaseScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class MenuScreen extends BaseScreen {
    private Texture background;
    private Texture img;
    private Vector2 pos;
    private Vector2 v;
    private Vector2 newPos;
    private Vector2 direction;
    private final int SPEED = 5;

    @Override
    public void show() {
        super.show();
        background = new Texture("planet-earth-in-space.jpg");
        img = new Texture("badlogic.jpg");
        pos = new Vector2();
        v = new Vector2();
        direction = new Vector2();
        newPos = new Vector2();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(img, pos.x, pos.y);
        batch.end();
        direction.set(newPos).sub(pos).nor();
        v.set(direction).scl(SPEED);
        pos.add(v);

    }

    @Override
    public void dispose() {
        super.dispose();
        background.dispose();
        img.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        newPos.set(screenX, Gdx.graphics.getHeight() - screenY);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        pos.set(screenX, Gdx.graphics.getHeight() - screenY);
        return super.touchDragged(screenX, screenY, pointer);
    }
}

