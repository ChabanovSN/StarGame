package ru.geekbrains.stargame.sprite;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.geekbrains.stargame.base.Sprite;
import ru.geekbrains.stargame.math.Rect;


public class RedHP extends Sprite {

    private static final float HEIGHT = 0.02f;

    private Rect worldBounds;
    private MainShip mainShip;
    public RedHP(TextureAtlas atlas,MainShip mainShip, Rect worldBounds) {
        super(atlas.findRegion("red_hp"));

        this.mainShip =mainShip;
        this.worldBounds=worldBounds;

          }

    @Override
    public void update(float delta) {
        super.update(delta);
        setTop(worldBounds.getTop()-0.033f);
        setRight(worldBounds.pos.x+0.002f);
        if(mainShip.getHp()<30) {
           setWidth((float) mainShip.getHp() / 600);
            setHeight(HEIGHT);
        }else{
            setHeight(HEIGHT);
            setWidth((float) mainShip.getHp() /(100)* worldBounds.getHalfWidth());
        }

    }
}
