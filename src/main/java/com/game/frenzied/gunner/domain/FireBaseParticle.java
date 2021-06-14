package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.Vector;

/**
 * @author Administrator
 * fire cause more particle
 */
public class FireBaseParticle extends BaseParticle {
    private static final long serialVersionUID = 9082476987034302079L;
    private static final float PARTICLE_VELOCITY = 0.01f;
    private static final int PARTICLE_LIFETIME = 30;
    private static final float PARTICLE_SIZE = 0.01f;
    private static final float PARTICLE_SPIN = 0.01f;
    private static final float SHRINK_RATE = 0.00005f;

    public FireBaseParticle(AbstractActor ship) {
        position = new Vector(ship.getTailPosition());
        // Relative to the ship
        velocity = new Vector(ship.getVelocity());
        // Add the speed of the shot
        velocity.incrementXBy(-PARTICLE_VELOCITY * (Math.cos(ship.getTheta()) + (gen.nextFloat() - 0.5f) / 2));
        velocity.incrementYBy(-PARTICLE_VELOCITY * (Math.sin(ship.getTheta()) + (gen.nextFloat() - 0.5f) / 2));
        init();
    }

    public FireBaseParticle(Vector pos, Vector vel) {
        position = new Vector(pos);
        velocity = vel.normalize().scaleBy(PARTICLE_VELOCITY);
        init();
    }

    public FireBaseParticle(Vector pos) {
        position = new Vector(pos);
        velocity = new Vector(gen.nextFloat() * 6 * Math.PI);
        velocity.scaleBy(PARTICLE_VELOCITY);
        init();
    }

    private void init() {
        velocity.scaleBy(rand(0.9f, 1));

        theta = 0;
        omega = PARTICLE_SPIN;
        width = PARTICLE_SIZE;
        height = PARTICLE_SIZE;

        colorR = 1.0f;
        colorG = 1.0f;
        colorB = 1.0f;
        colorA = 1.0f;
    }

    @Override
    public void update() {
        super.update();

        if (width >= 0.0001f) {
            width -= age * SHRINK_RATE;
            height -= age * SHRINK_RATE;
        }

        if (age > PARTICLE_LIFETIME) {
            delete();
        }
    }

    @Override
    protected void updateColor() {
        colorR -= 0.015f;
        colorG -= 0.05f;
        colorB -= 0.15f;
    }

    /**
     * returns a random float between positive floats low and high
     */
    private static float rand(float l, float h) {
        return gen.nextFloat() * (h - l) + l;
    }
}
