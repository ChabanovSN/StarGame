package ru.geekbrains.stargame.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.math.Rnd;
import ru.geekbrains.stargame.pools.EnemyPool;
import ru.geekbrains.stargame.pools.HeallerPool;
import ru.geekbrains.stargame.pools.PowerPool;
import ru.geekbrains.stargame.sprite.Enemy;
import ru.geekbrains.stargame.sprite.Healler;
import ru.geekbrains.stargame.sprite.MainShip;
import ru.geekbrains.stargame.sprite.Power;

public class EnemiesEmitter {

    private Vector2 enemySmallV = new Vector2(0f, -0.2f);
    private Vector2 enemyMediumV = new Vector2(0f, -0.03f);
    private Vector2 enemyBigV = new Vector2(0f, -0.005f);

    private static final float ENEMY_SMALL_HEIGHT = 0.1f;
    private static final float ENEMY_SMALL_BULLET_HEIGHT = 0.01f;
    private static final float ENEMY_SMALL_BULLET_VY = -0.3f;
    private static final int ENEMY_SMALL_BULLET_DAMAGE = 1;
    private static final float ENEMY_SMALL_RELOAD_INTERVAL = 1f;
    private static final int ENEMY_SMALL_HP = 1;

    private static final float ENEMY_MEDIUM_HEIGHT = 0.15f;
    private static final float ENEMY_MEDIUM_BULLET_HEIGHT = 0.02f;
    private static final float ENEMY_MEDIUM_BULLET_VY = -0.3f;
    private static final int ENEMY_MEDIUM_BULLET_DAMAGE = 5;
    private static final float ENEMY_MEDIUM_RELOAD_INTERVAL = 4f;
    private static final int ENEMY_MEDIUM_HP = 5;

    private static final float ENEMY_BIG_HEIGHT = 0.2f;
    private static final float ENEMY_BIG_BULLET_HEIGHT = 0.04f;
    private static final float ENEMY_BIG_BULLET_VY = -0.3f;
    private static final int ENEMY_BIG_BULLET_DAMAGE = 10;
    private static final float ENEMY_BIG_RELOAD_INTERVAL = 3f;
    private static final int ENEMY_BIG_HP = 20;

    private Rect worldBounds;

    private float generateInterval = 4f;
    private float generateTimer;

    private TextureRegion[] enemySmallRegion;
    private TextureRegion[] enemyMediumRegion;
    private TextureRegion[] enemyBigRegion;
    private TextureRegion[] powerRegion;
    private TextureRegion[] heallerRegion;



    private TextureRegion bulletRegion;
    private MainShip mainShip;
    private EnemyPool enemyPool;
    private HeallerPool heallerPool;
    private PowerPool powerPool;

    private int stage = 1;


    public EnemiesEmitter(Rect worldBounds, EnemyPool enemyPool, TextureAtlas[] atlas,
                          HeallerPool heallerPool, PowerPool powerPool
                                                                  ,MainShip mainShip) {
        this.worldBounds = worldBounds;
        this.enemyPool = enemyPool;
        TextureRegion textureRegion0 = atlas[0].findRegion("enemy0");
        this.enemySmallRegion = Regions.split(textureRegion0, 1, 2, 2);
        TextureRegion textureRegion1 = atlas[0].findRegion("enemy1");
        this.enemyMediumRegion = Regions.split(textureRegion1, 1, 2, 2);
        TextureRegion textureRegion2 = atlas[0].findRegion("enemy2");
        this.enemyBigRegion = Regions.split(textureRegion2, 1, 2, 2);
        this.bulletRegion = atlas[0].findRegion("bulletEnemy");

        TextureRegion textureHealler = atlas[1].findRegion("hp");
        this.heallerRegion =  Regions.split(textureHealler, 1, 2, 2);
        this.heallerPool = heallerPool;
        TextureRegion texturePower = atlas[1].findRegion("batareyka");
        this.powerRegion =  Regions.split(texturePower, 1, 2, 2);
        this.powerPool = powerPool;
        this.mainShip =mainShip;

    }

    public void generateEnemies(float delta,int frags) {
        generateTimer += delta;
        stage = frags / 10 + 1;

        if (generateTimer >= generateInterval) {
            generateTimer = 0f;
            Enemy enemy = enemyPool.obtain();

            float type = (float) Math.random();
            if (type < 0.7f) {
                enemy.set(
                        enemySmallRegion,
                        enemySmallV,
                        bulletRegion,
                        ENEMY_SMALL_BULLET_HEIGHT,
                        ENEMY_SMALL_BULLET_VY,
                        ENEMY_SMALL_BULLET_DAMAGE*stage,
                        ENEMY_SMALL_RELOAD_INTERVAL,
                        ENEMY_SMALL_HEIGHT,
                        ENEMY_SMALL_HP*stage
                );
            } else if (type < 0.9f) {
                enemy.set(
                        enemyMediumRegion,
                        enemyMediumV,
                        bulletRegion,
                        ENEMY_MEDIUM_BULLET_HEIGHT,
                        ENEMY_MEDIUM_BULLET_VY,
                        ENEMY_MEDIUM_BULLET_DAMAGE *stage,
                        ENEMY_MEDIUM_RELOAD_INTERVAL,
                        ENEMY_MEDIUM_HEIGHT,
                        ENEMY_MEDIUM_HP*stage
                );
            } else{
                enemy.set(
                        enemyBigRegion,
                        enemyBigV,
                        bulletRegion,
                        ENEMY_BIG_BULLET_HEIGHT,
                        ENEMY_BIG_BULLET_VY,
                        ENEMY_BIG_BULLET_DAMAGE*stage,
                        ENEMY_BIG_RELOAD_INTERVAL,
                        ENEMY_BIG_HEIGHT,
                        ENEMY_BIG_HP*stage
                );
            }
            if(mainShip.getHp()<30 || Rnd.nextFloat(0.1f,1.0f)<=0.2f){
                Healler healler = heallerPool.obtain();
                      healler.set(
                              heallerRegion,
                              enemySmallV,
                              ENEMY_SMALL_HEIGHT,
                              ENEMY_MEDIUM_HP

                      );
                healler.pos.x = Rnd.nextFloat(worldBounds.getLeft() + healler.getHalfWidth(), worldBounds.getRight() - healler.getHalfWidth());
                healler.setBottom(worldBounds.getTop());



            }else if(Rnd.nextFloat(0.1f,1.0f)<=0.1f*stage){
                Power power = powerPool.obtain();
                power.set(
                        powerRegion,
                        enemySmallV,
                        ENEMY_SMALL_HEIGHT,
                        ENEMY_MEDIUM_HP

                );
                power.pos.x = Rnd.nextFloat(worldBounds.getLeft() + power.getHalfWidth(), worldBounds.getRight() - power.getHalfWidth());
                power.setBottom(worldBounds.getTop());
            }

            enemy.pos.x = Rnd.nextFloat(worldBounds.getLeft() + enemy.getHalfWidth(), worldBounds.getRight() - enemy.getHalfWidth());
            enemy.setBottom(worldBounds.getTop());
        }
    }
    public void setToNewGame() {
        stage = 1;
    }

    public int getStage() {
        return stage;
    }
}
