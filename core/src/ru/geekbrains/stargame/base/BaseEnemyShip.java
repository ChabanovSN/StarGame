package ru.geekbrains.stargame.base;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.math.Rnd;

public class BaseEnemyShip extends Sprite {
    private static final float SHIP_HEIGHT = 0.15f;
    private Vector2 v;
    private Vector2 v0 = new Vector2(0.0f, -0.05f);
    private Rect worldBounds;
    private float xPos;
    private boolean gone=false;// проверка прохождения экрана
    public BaseEnemyShip(TextureRegion region, int rows, int cols, int frames) {
        super(region, rows, cols, frames);
        setHeightProportion(SHIP_HEIGHT);
        v = new Vector2(Rnd.nextFloat(-0.4f,0.4f),0.6f);
        pos.set(v);


    }


    public void setWorldBounds(Rect worldBounds) {
        this.worldBounds = worldBounds;

         }

    public void setxPos(float xPos) {
        this.xPos = xPos; // охота на главный корабль
    }

    public boolean isGone() {
        return gone;
    }

    public void setGone(boolean gone) {
        this.gone = gone;
    }

    private void checkAndHandleBounds() {
        if (worldBounds != null){
            if (getRight() > worldBounds.getRight()) setRight(worldBounds.getRight());
        if (getLeft() < worldBounds.getLeft()) setLeft(worldBounds.getLeft());
        if (getBottom() < worldBounds.getBottom()) {

            newShip();
        }
    }

    }
    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;

       }

    @Override
    public void update(float delta) {
       fall();
        pos.mulAdd(v0, delta);
        checkAndHandleBounds();
    }
    private void fall(){
        v0.x=xPos;
        v0.y -=0.01f; /// ускорение книзу
          v.set(v0);
    }

    public void newShip(){
        gone = true; // проверка прохождения экрана
        v0 = new Vector2(0.0f, -0.3f);
        v = new Vector2(Rnd.nextFloat(-0.4f, 0.4f), 0.6f);

        xPos = 0.0f;
        pos.set(v);

    }
}
