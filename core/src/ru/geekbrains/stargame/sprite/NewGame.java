package ru.geekbrains.stargame.sprite;


import com.badlogic.gdx.graphics.g2d.TextureRegion;


import ru.geekbrains.stargame.base.ActionListener;
import ru.geekbrains.stargame.base.ScaledTouchUpButton;


public class NewGame extends ScaledTouchUpButton {

    public NewGame(TextureRegion textureRegion, ActionListener actionListener, float pressScale) {
               super(textureRegion, actionListener, pressScale);
               setHeightProportion(0.05f);
               pos.y=-0.3f;


    }


}
