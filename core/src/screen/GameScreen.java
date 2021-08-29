package screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import base.BaseScreen;
import math.Rect;
import pool.BulletPool;
import pool.EnemyPool;
import sprite.Background;
import sprite.Bullet;
import sprite.EnemyShip;
import sprite.MainShip;
import sprite.Star;
import utils.EnemyEmitter;

public class GameScreen extends BaseScreen {
    private static final int STAR_COUNT = 64;
    private Background background;
    private Texture bg;
    private TextureAtlas atlas;
    private Star[] stars;
    private BulletPool bulletPool;
    private EnemyPool enemyPool;
    private MainShip mainShip;
    private Sound bulletSound;
    private Sound laserSound;
    private Music music;
    private EnemyEmitter enemyEmitter;

    @Override
    public void show() {
        super.show();
            bg = new Texture("planet-earth-in-space.jpg");
            background = new Background(bg);
            atlas = new TextureAtlas("mainAtlas.tpack");
            stars = new Star[STAR_COUNT];
            for (int i = 0; i < stars.length; i++) {
                stars[i] = new Star(atlas);
            }
            bulletPool = new BulletPool();
            enemyPool = new EnemyPool(worldBounds, bulletPool);
        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
            mainShip = new MainShip(atlas, bulletPool, laserSound);
            bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
            enemyEmitter = new EnemyEmitter(worldBounds, bulletSound, enemyPool, atlas);
            music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
            music.setLooping(true);
            music.play();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollisions();
        draw();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        for (Star star : stars) {
            star.resize(worldBounds);
        }
        mainShip.resize(worldBounds);
    }

    @Override
    public void dispose() {
        super.dispose();
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
        enemyPool.dispose();
        bulletSound.dispose();
        laserSound.dispose();
        music.dispose();
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        mainShip.touchDown(touch, pointer, button);
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        mainShip.touchUp(touch, pointer, button);
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        mainShip.keyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        mainShip.keyUp(keycode);
        return false;
    }

    private void update (float delta) {
        for (Star star : stars) {
            star.update(delta);
        }
        mainShip.update(delta);
        bulletPool.updateActiveSprites(delta);
        enemyPool.updateActiveSprites(delta);
        enemyEmitter.generate(delta);
    }

    private void checkCollisions () {
        List<EnemyShip> enemyShipList = enemyPool.getActiveSprites();
        for (EnemyShip enemyShip : enemyShipList) {
            if (enemyShip.isDestroyed()) {
                continue;
            }
            float minDist = enemyShip.getHalfWidth() + mainShip.getHalfWidth();
            if (mainShip.pos.dst(enemyShip.pos) < minDist) {
                enemyShip.destroy();
            }
        }

        List<Bullet> bulletList = bulletPool.getActiveSprites();
        for (Bullet bullet: bulletList
             ) {
            if (bullet.isDestroyed()) {
                continue;
            }
            for (EnemyShip enemyShip: enemyShipList
                 ) {
                if (enemyShip.isDestroyed() || bullet.getOwner() != mainShip) {
                    continue;
                }
                if (enemyShip.isBulletCollision(bullet)) {
                    enemyShip.damage(bullet.getDamage());
                    bullet.destroy();
                }
            }
        }
    }

    private void freeAllDestroyed() {

        bulletPool.freeAllActiveSprites();
        enemyPool.freeAllActiveSprites();
    }

    private void draw() {
        batch.begin();
        background.draw(batch);
        for (Star star : stars) {
            star.draw(batch);
        }
        mainShip.draw(batch);
        bulletPool.drawActiveSprites(batch);
        enemyPool.drawActiveSprites(batch);
        batch.end();
    }
}
