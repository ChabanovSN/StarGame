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

    private Vector2 v = new Vector2();
    private Vector2 v0 = new Vector2(0.5f, 0f);
    private boolean pressedLeft;
    private boolean pressedRight;
    private final int pointerDefault=-1;/// мультитач: работает только на один палец( нажатие) так больше похоже на джойстик
    private int pointer = pointerDefault;///мультитач: при этом можно вообще только в одну точку нажимать и корабль будет ходить влево-вправо


    public MainShip(TextureAtlas atlas, BulletPool bulletPool, ExplosionPool explosionPool, Sound sound) {
        super(atlas.findRegion("main_ship"), 1, 2, 2, sound );
    setHeightProportion(SHIP_HEIGHT);
    this.bulletPool = bulletPool;
    this.bulletRegion = atlas.findRegion("bulletMainShip");
    this.bulletHeight = 0.01f;
    this.bulletV.set(0, 0.5f);
    this.bulletDamage = 1;
    this.reloadInterval = 0.2f;
    this.explosionPool = explosionPool;
    this.hp = 6;
}

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        setBottom(worldBounds.getBottom() + BOTTOM_MARGIN);
    }

    @Override
    public void update(float delta) {
        pos.mulAdd(v, delta);
        reloadTimer += delta;
        if (reloadTimer >= reloadInterval) {
            reloadTimer = 0f;
            shoot();

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
        if (this.pointer == pointerDefault) {
            this.pointer = pointer;
            touch.y = 0;
            if (touch.x < 0) {
                if (touch.x < getLeft()) {
                    moveLeft();
                } else moveRight();

            } else if (touch.x > 0) {
                if (touch.x > getRight()) {
                    moveRight();
                } else moveLeft();

            } else {
                stop();
            }
        } else {
            return;
        }
    }

    @Override
    public void touchUp(Vector2 touch, int pointer) {
        if(this.pointer == pointer) stop();
    }

    private void moveLeft() {
        v.set(v0).rotate(180);
    }

    private void moveRight() {
        v.set(v0);
    }

    private void stop() {
        this.pointer= pointerDefault;
        v.setZero();
    }
    public boolean isBulletCollision(Rect bullet) {
        return !(bullet.getRight() < getLeft()
                || bullet.getLeft() > getRight()
                || bullet.getBottom() > pos.y
                || bullet.getTop() < getBottom());
    }
}
