package ru.geekbrains.stargame.sprite;


import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.geekbrains.stargame.base.Sprite;

public class GameOver extends Sprite {
    private float changeSize=0.008f;
    private final float stSize = getScale();
    public GameOver(TextureRegion region, float height) {
        super(region);
        setHeightProportion(height);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        scale +=changeSize;
        if(scale  >= stSize+(stSize/6))changeSize = - 0.008f;
        if(scale <= stSize-(stSize/6))changeSize =  0.008f;
    }
}
