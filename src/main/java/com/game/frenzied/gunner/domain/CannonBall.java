package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.ParticleSystem;
import com.game.frenzied.gunner.common.SoundEffect;
import com.game.frenzied.gunner.common.Sprite;
import com.game.frenzied.gunner.common.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CannonBall extends AbstractActor {

    private static final Logger log = LoggerFactory.getLogger(CannonBall.class);

    private static final long serialVersionUID = -6745335968576285239L;
    private static final float BULLET_VELOCITY = 0.035f;
    private static final float BULLET_SIZE = 0.05f;
    private static final int BULLET_LIFETIME = 200;
    private static final float BULLET_DENSITY = 90;

    private static final float STATIC_WIND_RESISTANCE = -0.00002f;
    private static final float GRAVITY = -0.0004f;

    private CannonBall(AbstractActor ship) {
        // Call our other constructor with a zero deflection angle
        this(ship, 0);
    }

    private CannonBall(AbstractActor ship, float deflectionAngle) {
        position = new Vector(ship.getNosePosition());
        // Relative to the Cannon
        velocity = new Vector(ship.getVelocity());
        // Add the speed of the shot
        velocity.incrementXBy(BULLET_VELOCITY * Math.cos(ship.getTheta() - deflectionAngle));
        velocity.incrementYBy(BULLET_VELOCITY * Math.sin(ship.getTheta() - deflectionAngle));

        sprite = Sprite.bullet();
        width = BULLET_SIZE;
        height = BULLET_SIZE;
        theta = 0;
        id = generateId();
        parentId = ship.id;
        abstractActors.add(this);
    }

    protected static CannonBall buildFor(AbstractActor parentActor) {
        return new CannonBall(parentActor);
    }

    @Override
    public void handleCollision(AbstractActor other) {

        if (other.parentId == parentId) {
            ParticleSystem.addDebrisParticle(this);
            SoundEffect.forSmallBoomHit().play();
            delete();
            return;
        }

        if (other.id == parentId) {
            // if not deal with ball collision then suggest use it to mock fire of ball out
//            Vector difference = getPosition().minus(other.getPosition());
//            difference.scaleBy(0.3f);
//            difference.incrementBy(getPosition());
//            ParticleSystem.addExplosion(difference);

            // TODO: if handle ball with itself cannon collision , then prefect collision chek function
        }

        if (other.id != parentId) {
            ParticleSystem.addDebrisParticle(this);
            SoundEffect.forSmallBoomHit().play();
            delete();
        }
    }

    @Override
    public void update() {
        // CL - Update our rotation and position as defined in Actor.update()
        super.update();

        /* Remove the bullet if it exceeds it's life span */
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
