package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.ParticleSystem;
import com.game.frenzied.gunner.common.SoundEffect;
import com.game.frenzied.gunner.common.Sprite;
import com.game.frenzied.gunner.common.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: milylg
 * @Description: 
 * @CreateDate: 2021/6/13 23:56
 */
public class Missile extends AbstractActor {
    private static final Logger log = LoggerFactory.getLogger(CannonBall.class);

    private static final long serialVersionUID = -6745335968576285239L;
    private static final float BULLET_VELOCITY = 0.035f;
    private static final float BULLET_SIZE = 0.05f;
    private static final int BULLET_LIFETIME = 200;
    private static final float BULLET_DENSITY = 90;

    private static final float STATIC_WIND_RESISTANCE = -0.00002f;
    private static final float GRAVITY = -0.0004f;


    public Missile(AbstractActor ship, boolean isToLeft) {
        position = new Vector(ship.getPosition());
        velocity = new Vector(ship.getVelocity());

        width = BULLET_SIZE;
        height = BULLET_SIZE;
        theta = 0;
        id = generateId();
        parentId = ship.id;

        sprite = isToLeft
                ? Sprite.missileLeft()
                : Sprite.missileRight();

        isAlive = true;
    }

    @Override
    public void handleCollision(AbstractActor other) {

        if (other instanceof Missile) {
            return;
        }
        if (other instanceof BattlePlane) {
            return;
        }
        if (other instanceof TransportPlane) {
            return;
        }
        if (other instanceof  Caisson) {
            return;
        }

        if (other.id != parentId) {
            ParticleSystem.addDebrisParticle(this);
            SoundEffect.forBulletHit().play();
            delete();
            setAlive(false);
        }
    }

    @Override
    public void update() {
        super.update();
        velocity.incrementYBy(GRAVITY);
        if (Math.abs(position.x()) > 1.95f || Math.abs(position.y()) > 0.95f) {
            delete();
        }
    }

    @Override
    public float getMass() {
        // This does not account for different actors having different densities
        // but the mass should scale with the cube of the linear scale (the volume)
        return width * height * height * BULLET_DENSITY;
    }


}
