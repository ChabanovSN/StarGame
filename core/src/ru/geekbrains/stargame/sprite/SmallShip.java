package ru.geekbrains.stargame.sprite;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ru.geekbrains.stargame.base.BaseEnemyShip;

public class BigShip extends BaseEnemyShip {
    public BigShip(TextureAtlas atlas) {
               super(atlas.findRegion("enemy2"), 1, 2, 2);
    }
}
