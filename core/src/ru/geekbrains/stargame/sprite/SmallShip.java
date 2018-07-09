package ru.geekbrains.stargame.sprite;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.geekbrains.stargame.base.BaseEnemyShip;
import ru.geekbrains.stargame.math.Rect;

public class SmallShip extends BaseEnemyShip {
    public SmallShip(TextureAtlas atlas) {
               super(atlas.findRegion("enemy0"), 1, 2, 2);
    }
    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
    }
    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
