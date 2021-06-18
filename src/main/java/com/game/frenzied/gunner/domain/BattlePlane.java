package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.ParticleSystem;
import com.game.frenzied.gunner.common.Sprite;
import com.game.frenzied.gunner.common.Vector;

import java.util.concurrent.TimeUnit;

/**
 * @Author: milylg
 * @Description: 
 * @CreateDate: 2021/6/13 20:42
 */
public class BattlePlane extends AbstractActor implements Weapon {


    private static final float BATTLE_PLANE_SIZE = 0.15f;
    private static final int BATTLE_LIFE = 320;
    private static final int BALLS = 5;

    private boolean isToLeft;
    private boolean isHit;
    private boolean isReport;
    private PlaneFleet.ReportCallback reportCallback;

    public enum OrientationType {

        TO_LEFT(1.8f),
        TO_RIGHT(-1.8f);

        private float x;

        OrientationType(float fromX) {
            this.x = fromX;
        }

        public int minus() {
            return x < 0 ? 1: -1;
        }
    }

    private BattlePlane(OrientationType type) {
        width = BATTLE_PLANE_SIZE;
        height = BATTLE_PLANE_SIZE;
        position = new Vector(type.x, 0.35f + gen.nextFloat() / 2);
        velocity = new Vector((0.01f + gen.nextFloat() / 500) * type.minus(), 0.0f);
        id = generateId();
        alive = 15;

        isToLeft = type == OrientationType.TO_LEFT;
        sprite = isToLeft
                ? Sprite.toLeftBattlePlane()
                : Sprite.toRightBattlePlane();

        isAlive = true;
        isHit = false;
        isReport = false;

        abstractActors.add(this);
    }

    public static BattlePlane buildActorOf(OrientationType type) {
        return new BattlePlane(type);
    }

    public void setReportCallback(PlaneFleet.ReportCallback reportCallback) {
        this.reportCallback = reportCallback;
    }

    @Override
    public void update() {
        super.update();
        if (age >= BATTLE_LIFE) {
            delete();
            return;
        }
        if (position.y() < -0.85f) {
            ParticleSystem.addExplosion(position);
            delete();
            return;
        }
        if (!isReport) {
            reportCallback.report();
            isReport = true;
        }
        if (isHit) {
            ParticleSystem.addPlasmaParticle(position);
            return;
        }
    }

    @Override
    public void handleCollision(AbstractActor other) {
        if (other instanceof CannonBall) {
            ParticleSystem.addDebrisParticle(this);
            delete();
            setAlive(false);
            return;
        }
        if (other instanceof AntiaircraftBall) {
            // TODO: black and clear it
            velocity.incrementYBy(-0.001f);
            ParticleSystem.addPlasmaParticle(position);
            if (alive < 5) {
                isHit = true;
            }
            if (--alive <= 0) {
                delete();
                setAlive(false);
            }
            return;
        }
        if (other instanceof Brick) {
            ParticleSystem.addDebrisParticle(this);
            delete();
            setAlive(false);
            return;
        }
    }

    @Override
    public void shootBall() {
        try {
            for (int use = 0; use < BALLS; use++) {
                if (isAlive) {
                    Missile.buildFor(this, isToLeft);
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        } catch (InterruptedException e) {
            logger.warn(e.getLocalizedMessage());
        }
    }

    @Override
    public void quicklyShoot() {
        /**
         * do nothing
         */
    }

    @Override
    public void turnLeft() {
        /**
         * do nothing
         */
    }

    @Override
    public void turnRight() {
        /**
         * do nothing
         */
    }

}
