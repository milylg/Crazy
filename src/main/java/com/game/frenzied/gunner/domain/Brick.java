package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.ParticleSystem;
import com.game.frenzied.gunner.common.SoundEffect;
import com.game.frenzied.gunner.common.Sprite;
import com.game.frenzied.gunner.common.Vector;

/**
 * @Author: milylg
 * @Description: 
 * @CreateDate: 2021/6/13 17:32
 */
public class Brick extends AbstractActor {

    private static final float BRICK_SIZE = 0.1f;


    public Brick(Vector pos) {
        position = new Vector(pos);
        velocity = new Vector();
        sprite = Sprite.brick();
        width = BRICK_SIZE;
        height = BRICK_SIZE;
        theta = 0;
        id = generateId();
        alive = 5;
    }

    @Override
    public void handleCollision(AbstractActor other) {
        if (other instanceof CannonBall || other instanceof Missile) {
            ParticleSystem.addDebrisParticle(this);
            SoundEffect.forBulletHit().play();
            if (--alive <=0) {
                delete();
            }
        }

    }
}
