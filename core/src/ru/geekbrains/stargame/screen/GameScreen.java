package ru.geekbrains.stargame.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import ru.geekbrains.stargame.base.ActionListener;
import ru.geekbrains.stargame.base.Base2DScreen;
import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.math.Rnd;
import ru.geekbrains.stargame.pools.BulletPool;
import ru.geekbrains.stargame.pools.EnemyPool;
import ru.geekbrains.stargame.pools.ExplosionPool;
import ru.geekbrains.stargame.pools.HeallerPool;
import ru.geekbrains.stargame.sprite.Background;
import ru.geekbrains.stargame.sprite.Bullet;
import ru.geekbrains.stargame.sprite.Enemy;
import ru.geekbrains.stargame.sprite.GameOver;
import ru.geekbrains.stargame.sprite.Healler;
import ru.geekbrains.stargame.sprite.MainShip;
import ru.geekbrains.stargame.sprite.NewGame;
import ru.geekbrains.stargame.sprite.Star;
import ru.geekbrains.stargame.utils.EnemiesEmitter;


public class GameScreen extends Base2DScreen implements ActionListener {

    private static final int STAR_COUNT = 56;
   // private static final float STAR_HEIGHT = 0.01f;/// звезды будут разного размера и мерцать чтобы не было "снега"

    private GameOver gameOverClass;
    private NewGame newGame;
    private Background background;
    private Healler healler;
    private Texture bg;
    private Star star[];
    private TextureAtlas atlas;

    private MainShip mainShip;


    private BulletPool bulletPool;
    private EnemyPool enemyPool;
    private ExplosionPool explosionPool;
    private HeallerPool heallerPool;

    private EnemiesEmitter enemiesEmitter;
    private Star tail;
    private Music musicBk;
    private Sound explosionSound;
    private Sound bulletSound;
    private Sound laserSound;
    private Texture hp;

    public GameScreen(Game game) {
        super(game);


    }

    @Override
    public void show() {
        super.show();
        musicBk  = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
        musicBk.setLooping(true);
        musicBk.play();


        atlas = new TextureAtlas("textures/mainAtlas.tpack");

            explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
            bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
            laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));

            bg = new Texture("textures/bg.png");
            background = new Background(new TextureRegion(bg));

            TextureRegion starRegion = atlas.findRegion("star");
            star = new Star[STAR_COUNT];
            for (int i = 0; i < star.length; i++) {
                star[i] = new Star(starRegion, Rnd.nextFloat(-0.005f, 0.005f), Rnd.nextFloat(-0.5f, -0.1f), Rnd.nextFloat(0.005f, 0.013f));
            }
            bulletPool = new BulletPool();
            explosionPool = new ExplosionPool(atlas, explosionSound);
            mainShip = new MainShip(atlas, bulletPool, explosionPool, laserSound);
            tail = new Star(starRegion, 0.09f); /// tail of the Ship
            enemyPool = new EnemyPool(bulletPool, worldBounds, explosionPool, mainShip, bulletSound,tail);
            heallerPool = new HeallerPool(bulletPool, worldBounds, explosionPool, mainShip, bulletSound);
            enemiesEmitter = new EnemiesEmitter(worldBounds, enemyPool, atlas,heallerPool,mainShip);

            TextureRegion gameOverRegion = atlas.findRegion("message_game_over");
            gameOverClass = new GameOver(gameOverRegion,0.08f);
            TextureRegion newGameRegion = atlas.findRegion("button_new_game");
            newGame = new NewGame(newGameRegion,this,0.9f);



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


         if(!mainShip.isDestroyed()) {
             for (int i = 0; i < star.length; i++) {
                 star[i].update(delta);
             }
             mainShip.update(delta);
             tail.update(mainShip);
             heallerPool.updateActiveSprites(delta);
             bulletPool.updateActiveSprites(delta);
             enemyPool.updateActiveSprites(delta);
             enemiesEmitter.generateEnemies(delta);
             explosionPool.updateActiveSprites(delta);
         }else {
             newGame.update(delta);
             gameOverClass.update(delta);

             for (int i = 0; i < star.length; i++) {
                 star[i].update(mainShip);
             }

         }

    }

    public void draw() {

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);

            for (int i = 0; i < star.length; i++) {
                star[i].draw(batch);
            }
        if(!mainShip.isDestroyed()) {
            mainShip.draw(batch);
            tail.draw(batch);
            bulletPool.drawActiveSprites(batch);
            enemyPool.drawActiveSprites(batch);
            explosionPool.drawActiveSprites(batch);
            heallerPool.drawActiveSprites(batch);
       }else {
           gameOverClass.draw(batch);
            newGame.draw(batch);
        }
        batch.end();

    }

    public void checkCollisions() {
        List<Enemy> enemyList = enemyPool.getActiveObjects();
        for (Enemy enemy : enemyList)/// удар с главным короблем
        {
            if (enemy.isDestroyed()) {
                continue;
            }
            float minDist = enemy.getHalfWidth() + mainShip.getHalfWidth();
            if (enemy.pos.dst2(mainShip.pos) < minDist * minDist) {
                enemy.boom();
                enemy.destroy();
                return;
            }
        }

        List<Bullet> bulletList = bulletPool.getActiveObjects();

        for (Bullet bullet : bulletList)  /// выстрел по главному кораблю
        {
            if (bullet.isDestroyed() || bullet.getOwner() == mainShip) {
                continue;
            }
            if (mainShip.isBulletCollision(bullet)) {
                mainShip.damage(bullet.getDamage());
                bullet.destroy();
            }
        }

        for (Enemy enemy : enemyList) {  //// выстрел по врагу
            if (enemy.isDestroyed()) {
                continue;
            }
            for (Bullet bullet : bulletList) {
                if (bullet.getOwner() != mainShip || bullet.isDestroyed()) {
                    continue;
                }
                if (enemy.isBulletCollision(bullet)) {
                    enemy.damage(bullet.getDamage());
                    bullet.destroy();
                }
            }
        }

        List<Healler> heallers = heallerPool.getActiveObjects();
        for (Healler healler : heallers) { // столкновение с аптечкой

            if (healler.isDestroyed()) {
                continue;
            }
            float minDist = healler.getHalfWidth() + mainShip.getHalfWidth();
            if (healler.pos.dst2(mainShip.pos) < minDist * minDist) {
                healler.boom();
                healler.destroy();
                return;
            }
        }
        for (Healler healler : heallers) {  //// выстрел в аптечку

            if (healler.isDestroyed()) {
                continue;
            }
            for (Bullet bullet : bulletList) {
                if (bullet.getOwner() != mainShip || bullet.isDestroyed()) {
                    continue;
                }
                if (healler.isBulletCollision(bullet)) {
                    healler.damage(bullet.getDamage());
                    bullet.destroy();
                }
            }
        }

    }

    public void deleteAllDestroyed() {

            bulletPool.freeAllDestroyedActiveSprites();
            enemyPool.freeAllDestroyedActiveSprites();
            explosionPool.freeAllDestroyedActiveSprites();
            heallerPool.freeAllDestroyedActiveSprites();

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
     if(mainShip.isDestroyed())   newGame.resize(worldBounds);
    }

    @Override
    public void dispose() {
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
        enemyPool.dispose();
        explosionPool.dispose();
        musicBk.dispose();
        explosionSound.dispose();
        bulletSound.dispose();
        laserSound.dispose();
        heallerPool.dispose();
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
     super.touchDown(touch, pointer);
             mainShip.touchDown(touch,pointer);
        if(mainShip.isDestroyed())newGame.touchDown(touch,pointer);


    }

    @Override
    public void touchUp(Vector2 touch, int pointer) {
       super.touchUp(touch, pointer);
              mainShip.touchUp(touch,pointer);
          if(mainShip.isDestroyed())newGame.touchUp(touch,pointer);

    }
    private void mainShipTail(){

    }

    @Override
    public void actionPerformed(Object src) {
        if (src == newGame) {
            explosionPool.dispose();
            enemyPool.dispose();
            bulletPool.dispose();
            mainShip.setHp(10);
            mainShip.flushDestroy();

        } else {
            throw new RuntimeException("Unknown src");
        }
    }

    @Override
    public void pause() {
        super.pause();
        musicBk.stop();
    }

    @Override
    public void resume() {
        super.resume();
        musicBk.play();
    }
}
