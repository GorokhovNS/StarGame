package sprite;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import base.Sprite;
import math.Rect;

public class GameOver extends Sprite {
private static final float HEIGHT = 0.08f;
private static final float BOTTOM_MARGIN = 0.02f;


    public GameOver(TextureAtlas atlas) {
        super(atlas.findRegion("message_game_over"));
    }

    @Override
    public void resize(Rect worldBounds) {
        setHeightProportion(HEIGHT);
        setBottom(BOTTOM_MARGIN);
    }
}
