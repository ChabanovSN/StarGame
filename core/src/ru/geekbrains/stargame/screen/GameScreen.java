package ru.geekbrains.stargame.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.base.Base2DScreen;
import ru.geekbrains.stargame.base.BaseEnemyShip;
import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.math.Rnd;
import ru.geekbrains.stargame.sprite.Background;
import ru.geekbrains.stargame.sprite.BigShip;
import ru.geekbrains.stargame.sprite.MainShip;
import ru.geekbrains.stargame.sprite.MeddleShip;
import ru.geekbrains.stargame.sprite.SmallShip;
import ru.geekbrains.stargame.sprite.Star;



public class GameScreen extends Base2DScreen {

    private static final int STAR_COUNT = 56;
   // private static final float STAR_HEIGHT = 0.01f;/// звезды будут разного размера и мерцать чтобы не было "снега"

    private Background background;
    private Texture bg;
    private Star star[];
    private TextureAtlas atlas;

    private MainShip mainShip;
    private  BaseEnemyShip enemyShipOne;
    private BaseEnemyShip enemyShip[]= new BaseEnemyShip[3];


    private float generateInterval = 4f;
    private float generateTimer;

    public GameScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        bg = new Texture("textures/bg.png");
        background = new Background(new TextureRegion(bg));
        atlas = new TextureAtlas("textures/mainAtlas.tpack");
        TextureRegion starRegion = atlas.findRegion("star");
        star = new Star[STAR_COUNT];
        for (int i = 0; i < star.length; i++) {
            star[i] = new Star(starRegion, Rnd.nextFloat(-0.005f, 0.005f), Rnd.nextFloat(-0.5f, -0.1f), Rnd.nextFloat(0.005f,0.013f));
        }
        mainShip = new MainShip(atlas);

        enemyShip[0] = new BigShip(atlas);
        enemyShip[1] = new MeddleShip(atlas);
        enemyShip[2] = new SmallShip(atlas);

        for (int i = 0; i <enemyShip.length ; i++) {
            enemyShip[i].setWorldBounds(super.getWorldBounds()); /// это что бы получить границы мира без resize()
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollisions();
        deleteAllDestroyed();
        draw();
    }

    public void update(float delta) {
        for (Star s : star) s.update(delta);
        mainShip.update(delta);
        generateTimer += delta;
        if (generateInterval <= generateTimer) {
            generateTimer = 0f;
            if (enemyShipOne == null)
                enemyShipOne = enemyShip[(int) Rnd.nextFloat(0, 3)];
            else if (enemyShipOne.isGone())
                enemyShipOne = enemyShip[(int) Rnd.nextFloat(0, 3)];
        }

        if (enemyShipOne != null) {
            if (!enemyShipOne.isGone()) {
                enemyShipOne.setxPos(mainShip.pos.x); // охота на главный корабль
                enemyShipOne.update(delta);
            }
        }

        checkArrayShip(); /// обнуление кораблей


    }

    private void checkArrayShip() {
        int checkCount=0;
        for(BaseEnemyShip ship :enemyShip){
            if(ship.isGone())checkCount++;
        }
        if(checkCount==3){
            for(BaseEnemyShip ship :enemyShip){
               ship.setGone(false);
            }
        }
    }

    public void draw() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        background.draw(batch);
        for (Star s : star) s.draw(batch);
        mainShip.draw(batch);
        if (enemyShipOne != null) {
            if (!enemyShipOne.isGone()) enemyShipOne.draw(batch);
        }
        batch.end();
    }

    public void checkCollisions() {

    }

    public void deleteAllDestroyed() {

    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        for (int i = 0; i < star.length; i++) {
            star[i].resize(worldBounds);
        }
        mainShip.resize(worldBounds);
        if (enemyShipOne != null) enemyShipOne.resize(worldBounds);

    }

    @Override
    public void dispose() {
        bg.dispose();
        atlas.dispose();
        super.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        mainShip.keyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        mainShip.keyUp(keycode);
        return false;
    }

    @Override
    public void touchDown(Vector2 touch, int pointer) {
             mainShip.touchDown(touch,pointer);
    }

    @Override
    public void touchUp(Vector2 touch, int pointer) {
              mainShip.touchUp(touch,pointer);

    }


}
