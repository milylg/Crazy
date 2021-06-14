package com.game.frenzied.gunner.common;

import com.game.frenzied.gunner.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 粒子系统
 *
 * @author Administrator
 * <p>
 * TODO LIST:
 * 1. When number of asteroid  less more directore value then increment it
 */
public class ParticleSystem {

    private static final Logger logger = LoggerFactory.getLogger(ParticleSystem.class);

    public static java.util.Vector<BaseParticle> baseParticles;

    public static final int DENSITY = 5;
    public static final int DENSITY_MULTIPLE = 2;
    public static final int DENSITY_EXPLOSION_MULTIPLE = 3;


    public static void init() {
        logger.info("Initializing Particles");
        baseParticles = new java.util.Vector<>();
    }

    /**
     * @param ship
     */
    public static void addFireParticle(AbstractActor ship) {
        for (int i = 0; i < DENSITY; i++) {
            baseParticles.add(new FireBaseParticle(ship));
        }
    }

    public static void addDebrisParticle(AbstractActor abstractActor) {

        for (int i = 0; i < DENSITY * DENSITY_MULTIPLE; i++) {
            baseParticles.add(new DebrisBaseParticle(abstractActor));
        }

    }

    /**
     * @param position
     */
    public static void addPlasmaParticle(Vector position) {
        for (int i = 0; i < DENSITY * DENSITY_MULTIPLE; i++) {
            baseParticles.add(new PlasmaBaseParticle(position));
        }
    }

    public static void addExplosion(Vector pos) {
        for (int i = 0; i < DENSITY * DENSITY_EXPLOSION_MULTIPLE; i++) {
            baseParticles.add(new FireBaseParticle(pos));
        }
    }

    public static void updateParticles() {
        for (int i = 0; i < baseParticles.size(); i++) {
            BaseParticle p = baseParticles.get(i);
            p.update();
        }
    }
}
