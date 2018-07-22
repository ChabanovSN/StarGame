package ru.geekbrains.stargame.base;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.math.Rect;

import ru.geekbrains.stargame.pools.BulletPool;
import ru.geekbrains.stargame.pools.ExplosionPool;
import ru.geekbrains.stargame.sprite.Bullet;
import ru.geekbrains.stargame.sprite.Explosion;


public class Ship extends Sprite {

    private static final float DAMAGE_ANIMATE_INTERVAL = 0.1f;
    private float damageAnimateTimer = DAMAGE_ANIMATE_INTERVAL;

    protected Vector2 v = new Vector2();
    protected Rect worldBounds;

    protected BulletPool bulletPool;
    protected ExplosionPool explosionPool;
    protected TextureRegion bulletRegion;

    protected final Vector2 bulletV = new Vector2();
    protected float bulletHeight;
    protected int bulletDamage;

    protected float reloadInterval;
    protected float reloadTimer;
    protected float posYtail;

    protected int hp;

    protected Sound shootSound;

    private float xPos;

    public Ship(BulletPool bulletPool, Rect worldBounds, ExplosionPool explosionPool, Sound sound) {
        this.bulletPool = bulletPool;
        this.worldBounds = worldBounds;
        this.explosionPool = explosionPool;
        this.shootSound = sound;
    }


    public Ship(TextureRegion region, int rows, int cols, int frames, Sound sound,Rect worldBounds) {
        super(region, rows, cols, frames);
        this.shootSound = sound;
    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        damageAnimateTimer += delta;
              if (damageAnimateTimer >= DAMAGE_ANIMATE_INTERVAL) {
            frame = 0;
        }
    }


    protected void shoot() {
        Bullet bullet = bulletPool.obtain();
        bullet.set(this, bulletRegion, pos, bulletV, bulletHeight, worldBounds, bulletDamage);
        shootSound.play();
    }
    protected void shoot(int damage, Vector2 bulletV ) {
        Bullet bullet = bulletPool.obtain();
        bullet.set(this, bulletRegion, pos, bulletV, bulletHeight*2, worldBounds, bulletDamage*damage);
        shootSound.play();
    }

    public void boom() {
        Explosion explosion = explosionPool.obtain();
        explosion.set(getHeight()*(float)1.5, pos);
        hp = 0;

    }

    public void damage(int damage) {

       frame =1;
        if(getClass().getSimpleName().equals("MainShip")) System.out.println("frame "+frame);
        damageAnimateTimer = 0f;
        hp -= damage;
        if (hp <= 0) {

            boom();
            destroy();

        }
    }



    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setxPos(float xPos) {
        this.xPos = xPos; // охота на главный корабль
    }

    public float getPosYtail() {
        return posYtail;
    }
}


