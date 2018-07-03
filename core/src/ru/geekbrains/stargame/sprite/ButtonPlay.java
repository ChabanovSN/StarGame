package ru.geekbrains.stargame.sprite;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.base.Sprite;
import ru.geekbrains.stargame.math.Rect;

public class ButtonPlay extends Sprite {


    private float sizePicture;
    public ButtonPlay(TextureRegion region,float height) {
        super(region);
        setHeightProportion(height);
        sizePicture = height/(float)2;

    }

    @Override
    public void resize(Rect worldBounds) {
          pos.set(worldBounds.getLeft()+sizePicture,worldBounds.getBottom()+sizePicture);
    }

    @Override
    public void touchDown(Vector2 touch, int pointer) {
               super.touchDown(touch, pointer);
               if(isMe(touch))scale -= 0.1;
    }

    @Override
    public void touchUp(Vector2 touch, int pointer) {
        super.touchUp(touch, pointer);
        if(isMe(touch))scale += 0.1;
    }


}
