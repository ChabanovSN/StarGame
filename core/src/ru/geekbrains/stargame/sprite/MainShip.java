package ru.geekbrains.stargame.sprite;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.base.Ship;

import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.pools.BulletPool;
import ru.geekbrains.stargame.pools.ExplosionPool;


public class MainShip extends Ship {

    private static final float SHIP_HEIGHT = 0.15f;
    private static final float BOTTOM_MARGIN = 0.05f;
    private static final int INVALID_POINTER = -1;
    private Vector2 v = new Vector2();
    private Vector2 v0 = new Vector2(0.5f, 0f);
    private boolean pressedLeft;
    private boolean pressedRight;

    private int bulletDamageDefault =1;
    private float bulletHeightDefault = 0.01f;
    private int bulletDamageBonus = bulletDamageDefault;
     private Vector2 bulletVDefault = new Vector2(0,0.5f);
    private Vector2 bulletVBonus = bulletVDefault;
    private float bonusTime;
    private float bonusTimeStep=0;
    private int leftPointer = INVALID_POINTER;
    private int rightPointer = INVALID_POINTER;


    public MainShip(TextureAtlas atlas, Rect worldBounds, BulletPool bulletPool, ExplosionPool explosionPool, Sound sound) {
        super(atlas.findRegion("main_ship"), 1, 2, 2, sound, worldBounds );
    setHeightProportion(SHIP_HEIGHT);
    this.bulletPool = bulletPool;
    this.bulletRegion = atlas.findRegion("bulletMainShip");
    this.explosionPool = explosionPool;
    this.worldBounds = worldBounds;
    setToNewGame();

}
    public void setToNewGame() {
        pos.x = worldBounds.pos.x;
        this.bulletHeight = bulletHeightDefault;
        this.bulletV.set(bulletVDefault);
        this.bulletDamage = bulletDamageDefault;
        this.reloadInterval = 0.2f;
        this.hp = 100;
        this.bonusTime=0;
        this.bonusTimeStep=0;

        flushDestroy();
    }
    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        setBottom(worldBounds.getBottom() + BOTTOM_MARGIN);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        pos.mulAdd(v, delta);
        reloadTimer += delta;
        this.posYtail=getBottom();
        if (reloadTimer >= reloadInterval) {
            reloadTimer = 0f;

            if(bulletDamageBonus !=bulletDamageDefault){
                bonusTimeStep +=delta;
                shoot(bulletDamageBonus, bulletVBonus );
                if(bonusTimeStep>bonusTime){
                    bulletDamageBonus =bulletDamageDefault;
                    bulletVBonus=bulletVDefault;
                    bonusTimeStep=0;
                    bonusTime=0;
                    reloadInterval= 0.2f;
                }
            }

            else shoot();

        }

        checkAndHandleBounds();
    }

    private void checkAndHandleBounds() {
        if (getRight() > worldBounds.getRight()) setRight(worldBounds.getRight());
        if (getLeft() < worldBounds.getLeft()) setLeft(worldBounds.getLeft());

    }

    public void keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                pressedLeft = true;
                moveLeft();
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                pressedRight = true;
                moveRight();
                break;
            case Input.Keys.UP:
                shoot();
                break;
        }
    }

    public void keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                pressedLeft = false;
                if (pressedRight) {
                    moveRight();
                } else {
                    stop();
                }
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                pressedRight = false;
                if (pressedLeft) {
                    moveLeft();
                } else {
                    stop();
                }
                break;
        }
    }

    @Override
    public void touchDown(Vector2 touch, int pointer) {
        if (touch.x < worldBounds.pos.x) {
            if (leftPointer != INVALID_POINTER) {
                return;
            }
            leftPointer = pointer;
            moveLeft();
        } else {
            if (rightPointer != INVALID_POINTER) {
                return;
            }
            rightPointer = pointer;
            moveRight();
        }
    }

    @Override
    public void touchUp(Vector2 touch, int pointer) {
        if (pointer == leftPointer) {
            leftPointer = INVALID_POINTER;
            if (rightPointer != INVALID_POINTER) {
                moveRight();
            } else {
                stop();
            }
        } else if (pointer == rightPointer) {
            rightPointer = INVALID_POINTER;
            if (leftPointer != INVALID_POINTER) {
                moveLeft();
            } else {
                stop();
            }
        }
        stop();
    }


    private void moveLeft() {
        v.set(v0).rotate(180);
    }

    private void moveRight() {
        v.set(v0);
    }

    private void stop() {

        v.setZero();
    }
    public boolean isBulletCollision(Rect bullet) {
        return !(bullet.getRight() < getLeft()
                || bullet.getLeft() > getRight()
                || bullet.getBottom() > pos.y
                || bullet.getTop() < getBottom());
    }



    public void setBonus(Vector2 bulletVBonus, int bulletDamageBonus,float bonusTime) {
        this.bulletVBonus = bulletVBonus;
        this.bulletDamageBonus += bulletDamageBonus;
        this.bonusTime += bonusTime;
        reloadInterval -= 0.02f;
    }


}
