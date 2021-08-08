package screen;

import base.BaseScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class MenuScreen extends BaseScreen {
    private Texture background;
    private Texture img;
    private Vector2 pos;

    @Override
    public void show() {
        super.show();
        background = new Texture("planet-earth-in-space.jpg");
        img = new Texture("badlogic.jpg");
        pos = new Vector2();
        batch.getProjectionMatrix().idt();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(img, pos.x, pos.y, 1f, 1f);
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        background.dispose();
        img.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}

