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
import com.badlogic.gdx.utils.Align;


import java.util.List;

import ru.geekbrains.stargame.base.ActionListener;
import ru.geekbrains.stargame.base.Base2DScreen;
import ru.geekbrains.stargame.base.Font;
import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.math.Rnd;
import ru.geekbrains.stargame.pools.BulletPool;
import ru.geekbrains.stargame.pools.EnemyPool;
import ru.geekbrains.stargame.pools.ExplosionPool;
import ru.geekbrains.stargame.pools.HeallerPool;
import ru.geekbrains.stargame.pools.PowerPool;
import ru.geekbrains.stargame.sprite.Background;
import ru.geekbrains.stargame.sprite.Bullet;
import ru.geekbrains.stargame.sprite.ButtonNewGame;
import ru.geekbrains.stargame.sprite.Enemy;
import ru.geekbrains.stargame.sprite.GreenHP;
import ru.geekbrains.stargame.sprite.Healler;
import ru.geekbrains.stargame.sprite.MainShip;
import ru.geekbrains.stargame.sprite.MessageGameOver;
import ru.geekbrains.stargame.sprite.Power;
import ru.geekbrains.stargame.sprite.RedHP;
import ru.geekbrains.stargame.sprite.Star;
import ru.geekbrains.stargame.utils.EnemiesEmitter;
import ru.geekbrains.stargame.utils.ScoreManager;


public class GameScreen extends Base2DScreen implements ActionListener {


    private static final int STAR_COUNT = 56;
    private static final float FONT_SIZE = 0.02f;


    private enum State {PLAYING, GAME_OVER}

    private State state;


    private Background background;

    private Texture bg;
    private Star star[];
    private TextureAtlas[] atlas;


    private MainShip mainShip;


    private BulletPool bulletPool;
    private EnemyPool enemyPool;
    private ExplosionPool explosionPool;
    private HeallerPool heallerPool;
    private PowerPool powerPool;

    private EnemiesEmitter enemiesEmitter;
    private Star tail;
    private Music musicBk;
    private Sound explosionSound;
    private Sound bulletSound;
    private Sound laserSound;


    private MessageGameOver messageGameOver;
    private ButtonNewGame buttonNewGame;
    private RedHP redHP;
    private GreenHP greenHP;
    private ScoreManager scoreManager;
    private boolean checkMainShipDestroyed=false;
    private Font font;

    private int frags;
    private StringBuilder sbFrags = new StringBuilder();
    private StringBuilder sbHp = new StringBuilder();
    private StringBuilder sbStage = new StringBuilder();
    private StringBuilder bestScore = new StringBuilder();

    public GameScreen(Game game) {
        super(game);


    }

    @Override
    public void show() {
        super.show();
        musicBk = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
        musicBk.setLooping(true);
        musicBk.play();

        atlas = new TextureAtlas[2];
        atlas[0] = new TextureAtlas("textures/mainAtlas.tpack");
        atlas[1] = new TextureAtlas("textures/bonus.pack");


        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));
        laserSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.wav"));

        bg = new Texture("textures/bg.png");
        background = new Background(new TextureRegion(bg));

        TextureRegion starRegion = atlas[0].findRegion("star");
        star = new Star[STAR_COUNT];
        for (int i = 0; i < star.length; i++) {
            star[i] = new Star(starRegion, Rnd.nextFloat(-0.005f, 0.005f), Rnd.nextFloat(-0.5f, -0.1f), Rnd.nextFloat(0.005f, 0.013f));
        }

        bulletPool = new BulletPool();
        explosionPool = new ExplosionPool(atlas[0], explosionSound);
        mainShip = new MainShip(atlas[0], worldBounds, bulletPool, explosionPool, laserSound);
        tail = new Star(starRegion, 0.09f); /// tail of the Ship
        redHP = new RedHP(atlas[1],mainShip,worldBounds);
        greenHP = new GreenHP(atlas[1],mainShip,worldBounds);
        enemyPool = new EnemyPool(bulletPool, worldBounds, explosionPool, mainShip, bulletSound);
        heallerPool = new HeallerPool(bulletPool, worldBounds, explosionPool, mainShip, bulletSound);
        powerPool = new PowerPool(bulletPool, worldBounds, explosionPool, mainShip, bulletSound);
        enemiesEmitter = new EnemiesEmitter(worldBounds, enemyPool, atlas, heallerPool,powerPool, mainShip);

        messageGameOver = new MessageGameOver(atlas[0]);
        buttonNewGame = new ButtonNewGame(atlas[0], this);
        font = new Font("font/font.fnt", "font/font.png");
        font.setWorldSize(FONT_SIZE);
        scoreManager = new ScoreManager();
        startNewGame();
    }


    private void printInfo() {
        sbFrags.setLength(0);
        sbHp.setLength(0);
        sbStage.setLength(0);
        font.draw(batch, sbFrags.append("Frags: ").append(frags), worldBounds.getLeft(), worldBounds.getTop());
        font.draw(batch, sbHp.append("HP: ").append(mainShip.getHp()), worldBounds.pos.x, worldBounds.getTop(), Align.center);
        font.draw(batch, sbStage.append("Stage: ").append(enemiesEmitter.getStage()), worldBounds.getRight(), worldBounds.getTop(), Align.right);
        redHP.draw(batch);
        greenHP.draw(batch);

    }
    private void printBestScore() {
        bestScore.setLength(0);
        font. setColor(1,0.5f,0,1);
        font.draw(batch, bestScore.append(scoreManager.getBestResult()), worldBounds.pos.x, messageGameOver.getTop()*2, Align.center);

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

        explosionPool.updateActiveSprites(delta);
        heallerPool.updateActiveSprites(delta);
        bulletPool.updateActiveSprites(delta);
        enemyPool.updateActiveSprites(delta);
        powerPool.updateActiveSprites(delta);
        redHP.update(delta);
        greenHP.update(delta);

        switch (state) {
            case PLAYING:
                for (int i = 0; i < star.length; i++) {
                    star[i].update(delta);
                }
                mainShip.update(delta);
                tail.update(mainShip);
                enemiesEmitter.generateEnemies(delta,frags);
                if (mainShip.isDestroyed()) {
                    state = State.GAME_OVER;
                   checkMainShipDestroyed=true;
                }

                break;
            case GAME_OVER:
                bulletSound.dispose();
                for (int i = 0; i < star.length; i++) {
                    star[i].update(mainShip);
                }
                if(checkMainShipDestroyed && mainShip.isDestroyed()){
                    scoreManager.SaveScore(frags,enemiesEmitter.getStage());
                    checkMainShipDestroyed=false;
                }
              //  scoreManager.LoadScore();
                break;


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


        bulletPool.drawActiveSprites(batch);
        enemyPool.drawActiveSprites(batch);
        for( Enemy enemy : enemyPool.getActiveObjects()){
            enemy.getTail().draw(batch);
        }
        explosionPool.drawActiveSprites(batch);
        heallerPool.drawActiveSprites(batch);
        powerPool.drawActiveSprites(batch);
        mainShip.draw(batch);
        tail.draw(batch);
        if (state == State.GAME_OVER) {
            messageGameOver.draw(batch);
            buttonNewGame.draw(batch);
            printBestScore();
        }
        printInfo();
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
                mainShip.boom();
                mainShip.destroy();
                state = State.GAME_OVER;
                checkMainShipDestroyed=true;
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
                    if (enemy.isDestroyed()) {
                        frags++;
                        break;
                    }
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
        List<Power> powers = powerPool.getActiveObjects();
        for (Power power : powers) { // столкновение с power

            if (power.isDestroyed()) {
                continue;
            }
            float minDist = power.getHalfWidth() + mainShip.getHalfWidth();
            if (power.pos.dst2(mainShip.pos) < minDist * minDist) {
                power.boom();
                power.destroy();
                return;
            }
        }
        for (Power power : powers) {  //// выстрел в power

            if (power.isDestroyed()) {
                continue;
            }
            for (Bullet bullet : bulletList) {
                if (bullet.getOwner() != mainShip || bullet.isDestroyed()) {
                    continue;
                }
                if (power.isBulletCollision(bullet)) {
                    power.damage(bullet.getDamage());
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
            powerPool.freeAllDestroyedActiveSprites();

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
        redHP.resize(worldBounds);
        greenHP.resize(worldBounds);

    }

    @Override
    public void dispose() {
        bg.dispose();
       for (TextureAtlas a :atlas) a.dispose();
        bulletPool.dispose();
        enemyPool.dispose();
        explosionPool.dispose();
        musicBk.dispose();
        explosionSound.dispose();
        bulletSound.dispose();
        laserSound.dispose();
        heallerPool.dispose();
        powerPool.dispose();
        font.dispose();
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
             buttonNewGame.touchDown(touch,pointer);
    }

    @Override
    public void touchUp(Vector2 touch, int pointer) {
              mainShip.touchUp(touch,pointer);
              buttonNewGame.touchUp(touch,pointer);
    }


    @Override
    public void actionPerformed(Object src) {

        if (src == buttonNewGame) {

              startNewGame();
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
    private void startNewGame() {
        state = State.PLAYING;
        frags = 0;

        mainShip.setToNewGame();
        enemiesEmitter.setToNewGame();
        checkMainShipDestroyed=false;

        bulletPool.freeAllActiveSprites();
        enemyPool.freeAllActiveSprites();
        explosionPool.freeAllActiveSprites();
        heallerPool.freeAllActiveSprites();
        powerPool.freeAllActiveSprites();
    }
}
