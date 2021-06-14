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
 * @CreateDate: 2021/6/13 15:21
 */
public class AntiaircraftBall extends AbstractActor {

    private static final Logger log = LoggerFactory.getLogger(AntiaircraftBall.class);

    private static final long serialVersionUID = -6745335968576285239L;
    private static final float BULLET_VELOCITY = 0.035f;
    private static final float BULLET_SIZE = 0.025f;
    private static final int BULLET_LIFETIME = 200;
    private static final float BULLET_DENSITY = 90;

    private static final float STATIC_WIND_RESISTANCE = -0.0000002f;
    private static final float GRAVITY = -0.00008f;

    public AntiaircraftBall(AbstractActor ship) {
        // Call our other constructor with a zero deflection angle
        this(ship, 0);
    }

    public AntiaircraftBall(AbstractActor ship, float deflectionAngle) {
        position = new Vector(ship.getNosePosition());
        // Relative to the Cannon
        velocity = new Vector(ship.getVelocity());
        // Add the speed of the shot
        velocity.incrementXBy(BULLET_VELOCITY * Math.cos(ship.getTheta() - deflectionAngle));
        velocity.incrementYBy(BULLET_VELOCITY * Math.sin(ship.getTheta() - deflectionAngle));

        sprite = Sprite.smallBullet();
        width = BULLET_SIZE;
        height = BULLET_SIZE;
        theta = 0;
        id = generateId();
        parentId = ship.id;
    }

    @Override
    public void handleCollision(AbstractActor other) {

        if (other.parentId == parentId) {
            return;
        }

        if (other.id == parentId) {
            return;
        }

        if (other instanceof Cannon || other instanceof Brick) {
            return;
        }

        if (other.id != parentId) {
            other.setAlive(false);
            delete();
        }
    }

    @Override
    public void update() {
        super.update();

        if(age > BULLET_LIFETIME) {
            delete();
        }
        velocity.incrementXBy(STATIC_WIND_RESISTANCE);
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
