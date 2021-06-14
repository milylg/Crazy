package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.*;

import java.awt.*;

public class Cannon extends AbstractActor implements Weapon, Mobile {

    private static final float STANDARD_WIDTH = 0.26f;
    private static final float STANDARD_HEIGHT = 0.127f;

    public static final Vector CENTER_POSITION = new Vector(-0.0f, -0.9f);

    public static final float ROTATION_INCREMENT = 0.03f;
    private int cannonBalls;
    private int antiaircraftBalls;
    private int shotCount;
    private boolean isCannonBallWarned;
    private boolean isAntiBallWarned;

    private CallBack hintMessageCallback;

    public interface CallBack {
        void update(int cannonballs, int antiBalls, int planes, int shotCount);
    }

    public Cannon() {
        this(CENTER_POSITION, new Vector());
    }

    public Cannon(Vector pos, Vector vel) {
        position = pos;
        velocity = vel;
        width = STANDARD_WIDTH;
        height = STANDARD_HEIGHT;
        sprite = Sprite.cannon();
        id = generateId();
        theta = 0f;
        alive = 100;
        isAlive = true;
        cannonBalls = 20;
        antiaircraftBalls = 100;
        shotCount = 0;
        isCannonBallWarned = false;
        isAntiBallWarned = false;
    }


    @Override
    public void handleCollision(AbstractActor other) {

        if (other instanceof Missile) {
            this.setAlive(false);
            other.setAlive(false);
            ParticleSystem.addExplosion(position);

            ScreenMessage screenMessage = new ScreenMessage("You Died!");
            ScreenMessage.add(screenMessage);
        }
    }

    public boolean isAlive() {
        return true;
    }

    @Override
    public void shootBall() {

        if (!isAlive) {
            return;
        }
        if (cannonBalls <= 0) {
            SoundEffect.forEmptyBall().play();
            return;
        }

        if (!isCannonBallWarned && cannonBalls <= 8) {
            SoundEffect.forWarnClock().play();
            ScreenMessage message = new ScreenMessage("[WARN]", position, Color.RED);
            ScreenMessage.add(message);
            isCannonBallWarned = true;
        }

        CannonBall ball = new CannonBall(this);
        AbstractActor.abstractActors.add(ball);
        cannonBalls --;
        shotCount ++;
        hintMessageCallback.update(cannonBalls, antiaircraftBalls, PlaneFleet.hitPlanes(), shotCount);

        SoundEffect.forBulletShot().play();

        Vector difference = getPosition().minus(ball.getPosition());
        difference.scaleBy(-1.0f);
        difference.incrementBy(getPosition());
        ParticleSystem.addExplosion(difference);
    }

    @Override
    public void quicklyShoot() {

        if (!isAlive) {
            return;
        }
        if (antiaircraftBalls <= 0) {
            SoundEffect.forEmptyBall().play();
            return;
        }

        if (!isAntiBallWarned && antiaircraftBalls < 20) {
            SoundEffect.forWarnClock().play();
            ScreenMessage message = new ScreenMessage("[WARN]", position, Color.RED);
            ScreenMessage.add(message);
            isAntiBallWarned = true;
        }

        AntiaircraftBall ball = new AntiaircraftBall(this);
        AbstractActor.abstractActors.add(ball);
        antiaircraftBalls --;
        shotCount ++;
        hintMessageCallback.update(cannonBalls, antiaircraftBalls, PlaneFleet.hitPlanes(), shotCount);

        Vector difference = getPosition().minus(ball.getPosition());
        difference.scaleBy(-1.0f);
        difference.incrementBy(getPosition());
        ParticleSystem.addPlasmaParticle(difference);
        SoundEffect.forQuicklyShot().play();
    }

    @Override
    public void turnLeft() {
        theta += ROTATION_INCREMENT;
    }

    @Override
    public void turnRight() {
        theta -= ROTATION_INCREMENT;
    }

    @Override
    public void leftMove() {
        position.incrementXBy(-0.01d);
    }

    @Override
    public void rightMove() {
        position.incrementXBy(0.01d);
    }

    @Override
    public void move() {
        /* not deal with it */
    }

    private void death() {
        ParticleSystem.addExplosion(position);
        ScreenMessage.add(new ScreenMessage("You Died!", position));
        AbstractActor.abstractActors.remove(this);
        alive = 0;
        if(SoundEffect.isEnabled()) {
            //SoundEffect.forDeath().play();
        }
    }

    public void addBalls(int antiaircraftBalls, int cannonBalls) {
        this.antiaircraftBalls += antiaircraftBalls;
        this.cannonBalls += cannonBalls;
        hintMessageCallback.update(this.cannonBalls, this.antiaircraftBalls, PlaneFleet.hitPlanes(), shotCount);
    }

    public int getAntiaircraftBalls() {
        return antiaircraftBalls;
    }

    public int getCannonBalls() {
        return cannonBalls;
    }

    public void setHintMessageCallback(CallBack hintMessageCallback) {
        this.hintMessageCallback = hintMessageCallback;
    }
}
