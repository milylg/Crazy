package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.ParticleSystem;
import com.game.frenzied.gunner.common.SoundEffect;
import com.game.frenzied.gunner.common.Sprite;
import com.game.frenzied.gunner.common.Vector;

/**
 * @Author: milylg
 * @Description: 
 * @CreateDate: 2021/6/14 11:35
 */
public class TransportPlane extends AbstractActor implements Transportable {

    private static final float TRANSPORT_PLANE_SIZE = 0.13f;
    private static final int TRANSPORT_PLANE_LIFE = 300;


    public TransportPlane() {
        width = TRANSPORT_PLANE_SIZE;
        height = TRANSPORT_PLANE_SIZE;
        position = new Vector(-1.8f, 0.35f + gen.nextFloat() / 2);
        velocity = new Vector((0.01f + gen.nextFloat() / 500), 0.0f);
        id = generateId();
        sprite = Sprite.transportPlane();
        isAlive = true;
        abstractActors.add(this);
    }



    @Override
    public void update() {
        super.update();
        if (age > TRANSPORT_PLANE_LIFE) {
            delete();
        }
    }

    @Override
    public void handleCollision(AbstractActor other) {
        if (other instanceof AntiaircraftBall) {
            ParticleSystem.addExplosion(position);
            // TODO: test it sounds
            SoundEffect.forSmallBoomHit().play();
            delete();
            return;
        }
        if (other instanceof CannonBall) {
            ParticleSystem.addExplosion(position);
            // TODO: test it sounds
            SoundEffect.forSmallBoomHit().play();
            delete();
            return;
        }
    }

    @Override
    public void release() {
        Caisson.buildFor(this);
    }
}
