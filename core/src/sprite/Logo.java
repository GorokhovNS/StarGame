package sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import base.Sprite;
import math.Rect;

public class Logo extends Sprite {
    public static final float V_LEN = 0.01f;
    private Vector2 touch;
    private Vector2 v;


    public Logo(Texture texture) {
        super(new TextureRegion(texture));
        this.v = new Vector2();
        this.touch = new Vector2();
    }

    @Override
    public void resize(Rect worldBounds) {
        setHeightProportion(0.2f);
    }

    @Override
    public void update(float delta) {
        if (touch.dst(pos) > V_LEN) {
    pos.add(v);
        } else {
    pos.set(touch);
        }
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        this.touch.set(touch);
        v.set(touch.cpy().sub(pos).setLength(V_LEN));
        return false;
    }
}
