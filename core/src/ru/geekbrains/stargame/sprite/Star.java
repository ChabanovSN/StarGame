package ru.geekbrains.stargame.sprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.base.Ship;
import ru.geekbrains.stargame.base.Sprite;
import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.math.Rnd;



public class Star extends Sprite {

    private Vector2 v = new Vector2();
    private Rect worldBounds;


    public Star(TextureRegion region, float vx, float vy, float height) {
        super(region);
        v.set(vx, vy);
        setHeightProportion(height);
    }
    public Star(TextureRegion region, float height) {
        super(region);

        setHeightProportion(height);
    }
    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
        float posX = Rnd.nextFloat(worldBounds.getLeft(), worldBounds.getRight());
        float posY = Rnd.nextFloat(worldBounds.getBottom(), worldBounds.getTop());
        pos.set(posX, posY);
    }

    @Override
    public void update(float delta) {
        pos.mulAdd(v, delta);
        checkAndHandleBounds();
        scale =Rnd.nextFloat(0.3f,1.5f); // эффект мерцания
    }

    public void update(Ship ship) {

        if (!ship.isDestroyed()) {
            pos.x = ship.pos.x;
            if (ship.getClass().getSimpleName().equals("MainShip")) {
                pos.y = ship.getBottom();
            } else {
                pos.y = ship.getTop();
            }

            scale = Rnd.nextFloat(0.5f, 1.5f);// эффект мерцания
        } else {
            if (scale < 10f) scale += 0.5f;
        }

    }

    private void checkAndHandleBounds() {
        if (getRight() < worldBounds.getLeft()) setLeft(worldBounds.getRight());
        if (getLeft() > worldBounds.getRight()) setRight(worldBounds.getLeft());
        if (getTop() < worldBounds.getBottom()) setBottom(worldBounds.getTop());
        if (getBottom() > worldBounds.getTop()) setTop(worldBounds.getBottom());
    }
}
