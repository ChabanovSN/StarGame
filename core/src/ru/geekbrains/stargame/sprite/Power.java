package ru.geekbrains.stargame.sprite;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.base.Ship;
import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.pools.BulletPool;
import ru.geekbrains.stargame.pools.ExplosionPool;


public class Power extends Ship {

    private enum State {DESCENT, FIGHT}
    private MainShip mainShip;
    private State state;
    private int bonusDamage;
    private float bonusTime = 2f;
    private Vector2 v0 = new Vector2();
    private Vector2 descentV = new Vector2(0, -0.15f);
    private Vector2 bonusV = new Vector2(0, 1.0f);

    public Power(BulletPool bulletPool, Rect worldBounds, ExplosionPool explosionPool, MainShip mainShip, Sound sound ) {
        super(bulletPool, worldBounds, explosionPool, sound);
        this.v.set(v0);
        this.state = State.DESCENT;
        this.v.set(descentV);
        this.mainShip = mainShip;
        this.bonusDamage = 5;
        regions = new TextureRegion[1];
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        angle -=1;

        pos.mulAdd(v, delta);
        switch (state) {
            case DESCENT:
                if (getTop() <= worldBounds.getTop()) {
                    v.set(v0);
                    state = State.FIGHT;
                }
                break;
            case FIGHT:

                if (getBottom() < worldBounds.getBottom()) {

                    bonusDamage=0;
                    boom();
                    destroy();
                }
                break;
        }
    }

    public void set(
            TextureRegion[] region,
            Vector2 v0,
             float height,
              int hp

    ) {
        this.regions = region;
        this.v0.set(v0);

        setHeightProportion(height);

        this.v.set(descentV);
        this.state = State.DESCENT;
        this.hp = hp;
    }
    public boolean isBulletCollision(Rect bullet) {
        return !(bullet.getRight() < getLeft()
                || bullet.getLeft() > getRight()
                || bullet.getBottom() > getTop()
                || bullet.getTop() < pos.y);
    }

    @Override
    public void boom() {
        super.boom();

        mainShip.setBonus(bonusV,bonusDamage,bonusTime);


    }
}
