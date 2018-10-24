package ru.geekbrains.stargame.sprite;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.geekbrains.stargame.base.Sprite;
import ru.geekbrains.stargame.math.Rect;


public class GreenHP extends Sprite {

    private static final float HEIGHT = 0.03f;

    private Rect worldBounds;
    private MainShip mainShip;
    public GreenHP(TextureAtlas atlas,MainShip mainShip, Rect worldBounds) {
        super(atlas.findRegion("green_hp"));

        this.mainShip =mainShip;
        this.worldBounds=worldBounds;

    }

    @Override
    public void update(float delta) {
        super.update(delta);
        setTop(worldBounds.getTop()-0.025f);
        setLeft(worldBounds.pos.x-0.02f);
        if(mainShip.getHp()>=30) {
            setWidth((float) mainShip.getHp() /(100)* worldBounds.getHalfWidth());
            setHeight(HEIGHT);
        }else{
            setHeight(0);
            setWidth(0);
        }

    }
}
