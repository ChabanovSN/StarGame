package ru.geekbrains.stargame.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.base.Base2DScreen;
import ru.geekbrains.stargame.base.Ship;
import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.math.Rnd;
import ru.geekbrains.stargame.pools.BulletPool;
import ru.geekbrains.stargame.pools.EnemyPool;
import ru.geekbrains.stargame.sprite.Background;
import ru.geekbrains.stargame.sprite.MainShip;
import ru.geekbrains.stargame.sprite.Star;
import ru.geekbrains.stargame.utils.EnemiesEmitter;


public class GameScreen extends Base2DScreen {

    private static final int STAR_COUNT = 56;
   // private static final float STAR_HEIGHT = 0.01f;/// звезды будут разного размера и мерцать чтобы не было "снега"

    private Background background;
    private Texture bg;
    private Star star[];
    private TextureAtlas atlas;

    private MainShip mainShip;
    private Ship enemyShipOne;
    private Ship enemyShip[]= new Ship[3];


    private float generateInterval = 4f;
    private float generateTimer;

    private BulletPool bulletPool;
    private EnemyPool enemyPool;

    private EnemiesEmitter enemiesEmitter;
    Star tail;

    public GameScreen(Game game) {
        super(game);

    }

    @Override
    public void show() {
        super.show();

        bg = new Texture("textures/bg.png");
        background = new Background(new TextureRegion(bg));
        atlas = new TextureAtlas("textures/mainAtlas.tpack");
        TextureRegion starRegion = atlas.findRegion("star");
        star = new Star[STAR_COUNT];
        for (int i = 0; i < star.length; i++) {
            star[i] = new Star(starRegion, Rnd.nextFloat(-0.005f, 0.005f), Rnd.nextFloat(-0.5f, -0.1f), Rnd.nextFloat(0.005f,0.013f));
        }


        bulletPool = new BulletPool();
        mainShip = new MainShip(atlas, bulletPool);
        tail = new Star(starRegion,mainShip.pos.x,mainShip.pos.y,0.09f); /// tail of the mainShip
        enemyPool = new EnemyPool(bulletPool, worldBounds);
        this.enemiesEmitter = new EnemiesEmitter(worldBounds, enemyPool, atlas);
    }
    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollisions();
        deleteAllDestroyed();
        draw();
    }

    public void update(float delta) {
        for (int i = 0; i < star.length; i++) {
            star[i].update(delta);
        }
        mainShip.update(delta);
        tail.update(mainShip);
        bulletPool.updateActiveSprites(delta);
        enemyPool.updateActiveSprites(delta);
        enemiesEmitter.generateEnemies(delta);
    }

    public void draw() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        for (int i = 0; i < star.length; i++) {
            star[i].draw(batch);
        }
        mainShip.draw(batch);
        tail.draw(batch);
        bulletPool.drawActiveSprites(batch);
        enemyPool.drawActiveSprites(batch);
        batch.end();
    }

    public void checkCollisions() {

    }

    public void deleteAllDestroyed() {
        bulletPool.freeAllDestroyedActiveSprites();
        enemyPool.freeAllDestroyedActiveSprites();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        for (int i = 0; i < star.length; i++) {
            star[i].resize(worldBounds);
        }
        mainShip.resize(worldBounds);
        tail.resize(worldBounds);
    }

    @Override
    public void dispose() {
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
        enemyPool.dispose();
        super.dispose();
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

    @Override
    public void touchDown(Vector2 touch, int pointer) {
             mainShip.touchDown(touch,pointer);
    }

    @Override
    public void touchUp(Vector2 touch, int pointer) {
              mainShip.touchUp(touch,pointer);

    }
    private void mainShipTail(){

    }

}
