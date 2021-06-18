package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.ScreenMessage;
import com.game.frenzied.gunner.common.Sprite;
import com.game.frenzied.gunner.common.Vector;

/**
 * @Author: milylg
 * @Description: 
 * @CreateDate: 2021/6/14 11:44
 */
public class Caisson extends AbstractActor {

    private static final float CAISSON_SIZE = 0.05f;
    private static final int CAISSON_LIFETIME = 200;
    private static final float GRAVITY = -0.0004f;

    private int cannonBalls;
    private int antiaircraftBalls;
    private boolean isOnTheGround;


    public Caisson(AbstractActor parent) {
        position = new Vector(parent.getPosition());
        velocity = new Vector(parent.getVelocity().x(), 0.0f);

        width = CAISSON_SIZE;
        height = CAISSON_SIZE;
        theta = 0;
        id = generateId();
        parentId = parent.id;

        sprite = Sprite.caisson();

        isAlive = true;

        cannonBalls = 20 + gen.nextInt(20);
        antiaircraftBalls = 200 + gen.nextInt(200);

        isOnTheGround = false;

        abstractActors.add(this);
    }

    protected static Caisson buildFor(AbstractActor parent) {
        return new Caisson(parent);
    }

    @Override
    public void update() {
        super.update();
        if (age >= CAISSON_LIFETIME) {
            delete();
        }
        if (isOnTheGround || position.y() <= -0.85f) {
            velocity = new Vector();
            isOnTheGround = true;
            return;
        }
        velocity.incrementYBy(GRAVITY);
    }

    @Override
    public void handleCollision(AbstractActor other) {
        if (other instanceof Cannon) {
            ((Cannon)other).addBalls(antiaircraftBalls, cannonBalls);
            ScreenMessage message = new ScreenMessage("+ Balls", position);
            ScreenMessage.add(message);
            delete();
        }
        if (other instanceof Brick) {
            isOnTheGround = true;
        }
    }

}
