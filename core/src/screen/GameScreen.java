package screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import base.BaseScreen;
import base.Font;
import math.Rect;
import pool.BulletPool;
import pool.EnemyPool;
import pool.ExplosionPool;
import sprite.Background;
import sprite.Bullet;
import sprite.EnemyShip;
import sprite.GameOver;
import sprite.MainShip;
import sprite.NewGameButton;
import sprite.Star;
import utils.EnemyEmitter;

public class GameScreen extends BaseScreen {
    private static final int STAR_COUNT = 64;
    private static final float PADDING = 0.01f;
    private static final String FRAGS = "KILLS: ";
    private static final String HP = "HP: ";
    private static final String LEVEL = "LEVEL: ";
    private Background background;
    private Texture bg;
    private TextureAtlas atlas;
    private Star[] stars;
    private BulletPool bulletPool;
    private ExplosionPool explosionPool;
    private EnemyPool enemyPool;
    private MainShip mainShip;
    private Sound bulletSound;
    private Sound laserSound;
    private Sound explosionSound;
    private Music music;
    private EnemyEmitter enemyEmitter;
    private GameOver gameOver;
    private NewGameButton newGameButton;
    private Font font;
    private int frags;
    private StringBuilder sbFrags;
    private StringBuilder sbHP;
    private StringBuilder sbLevel;

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
            explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
            explosionPool = new ExplosionPool(atlas, explosionSound);
            enemyPool = new EnemyPool(worldBounds, bulletPool, explosionPool);
        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));
            mainShip = new MainShip(atlas, bulletPool, explosionPool, laserSound);
            bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
            enemyEmitter = new EnemyEmitter(worldBounds, bulletSound, enemyPool, atlas);
            gameOver = new GameOver(atlas);
            newGameButton = new NewGameButton(atlas, this);
            music = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
            music.setLooping(true);
            music.play();
            font = new Font("font/font.fnt", "font/font.png");
            font.setSize(0.015f);
            frags = 0;
            sbFrags = new StringBuilder();
            sbHP = new StringBuilder();
            sbLevel = new StringBuilder();
    }

    public void startNewGame() {
        mainShip.startNewGame();
        bulletPool.freeAllActiveSprite();
        enemyPool.freeAllActiveSprite();
        explosionPool.freeAllActiveSprite();
        frags = 0;
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
        gameOver.resize(worldBounds);
        newGameButton.resize(worldBounds);
    }

    @Override
    public void dispose() {
        super.dispose();
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
        explosionPool.dispose();
        enemyPool.dispose();
        explosionSound.dispose();
        bulletSound.dispose();
        laserSound.dispose();
        music.dispose();
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        if (mainShip.isDestroyed()) {
            newGameButton.touchDown(touch, pointer, button);
        } else {
            mainShip.touchDown(touch, pointer, button);
        }
        return false;
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        if (mainShip.isDestroyed()) {
            newGameButton.touchUp(touch, pointer, button);
        } else {
            mainShip.touchUp(touch, pointer, button);
        }
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
        explosionPool.updateActiveSprites(delta);
        if (!mainShip.isDestroyed()) {
            mainShip.update(delta);
            bulletPool.updateActiveSprites(delta);
            enemyPool.updateActiveSprites(delta);
            enemyEmitter.generate(delta, frags);
        }
    }

    private void checkCollisions () {
        if (mainShip.isDestroyed()) {
            return;
        }
        List<EnemyShip> enemyShipList = enemyPool.getActiveSprites();
        for (EnemyShip enemyShip : enemyShipList) {
            if (enemyShip.isDestroyed()) {
                continue;
            }
            float minDist = enemyShip.getHalfWidth() + mainShip.getHalfWidth();
            if (mainShip.pos.dst(enemyShip.pos) < minDist) {
                mainShip.damage(enemyShip.getBulletDamage() * 2);
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
                    if(enemyShip.isDestroyed()) {
                        frags++;
                    }
                }
            }
            if (bullet.getOwner() != mainShip && mainShip.isBulletCollision(bullet)) {
                mainShip.damage(bullet.getDamage());
                bullet.destroy();
            }
        }
    }

    private void freeAllDestroyed() {

        bulletPool.freeAllDestroyedActiveSprites();
        explosionPool.freeAllDestroyedActiveSprites();
        enemyPool.freeAllDestroyedActiveSprites();
    }

    private void draw() {
        batch.begin();
        background.draw(batch);
        for (Star star : stars) {
            star.draw(batch);
        }
        if (!mainShip.isDestroyed()) {
            mainShip.draw(batch);
            bulletPool.drawActiveSprites(batch);
            enemyPool.drawActiveSprites(batch);
        } else {
            gameOver.draw(batch);
                newGameButton.draw(batch);
        }
        explosionPool.drawActiveSprites(batch);
        printIngo();
        batch.end();
    }

    private void printIngo() {
        sbFrags.setLength(0);
       font.draw(batch, sbFrags.append(FRAGS).append(frags), worldBounds.getLeft() + PADDING, worldBounds.getTop() - PADDING);
       sbHP.setLength(0);
       font.draw(batch, sbHP.append(HP).append(mainShip.getHp()), worldBounds.pos.x, worldBounds.getTop() - PADDING, Align.center);
       sbLevel.setLength(0);
       font.draw(batch, sbLevel.append(LEVEL).append(enemyEmitter.getLevel()), worldBounds.getRight() - PADDING, worldBounds.getTop()- PADDING, Align.right);
    }
}
