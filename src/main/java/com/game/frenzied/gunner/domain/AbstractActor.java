package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.Sprite;
import com.game.frenzied.gunner.common.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Random;

/**
 * abstract class of actor in game active
 * <p>
 * 游戏场景中的参与者抽象类
 */
public abstract class AbstractActor implements Serializable {

    private static final long serialVersionUID = 744085604446096658L;

    protected static final Logger logger = LoggerFactory.getLogger(AbstractActor.class);

    /**
     * About range of bullet
     */
    private static final double MAX_VELOCITY = 0.1;
    /**
     * The actor maximum sport speed
     */
    private static final float MAX_OMEGA = 0.2f;

    /**
     * The width value of right boundary
     */
    private static final float RIGHT_WIDTH = 2f;
    /**
     * The width value of left boundary
     */
    private static final float LEFT_WIDTH = -2f;

    /**
     * Common random number generator object
     */
    public static Random gen = new Random();

    /**
     * All the actors currently in play
     * We use the fully qualified named space for the Vector container so
     * it doesn't clash with our name space. Vectors work like ArrayLists,
     * but are synchronized.
     */
    public static java.util.Vector<AbstractActor> abstractActors = new java.util.Vector<>();

    /**
     * Used by generateId();
     */
    public static int lastId;

    /**
     * These fields are protected so that our descendants can modify them
     */
    protected Vector position;
    protected Vector velocity;

    /**
     * Angular position
     */
    protected float theta;

    /**
     * This is the texture of this object
     */
    protected Sprite sprite;

    /**
     * the radius of the object
     */
    protected float width;

    protected float height;

    /**
     * unique ID for each Actor
     */
    protected int id;
    /**
     * Actor age in frames
     */
    protected int age;

    protected int alive;

    protected boolean isAlive;


    /**
     * unique ID for each Actor
     */
    protected int parentId;

    /**
     *  Angular velocity : 角速度
     *
     *  the actors current angular velocity
     */
    protected float omega;

    /**
     * Call back before render loop for update to update it's position and do any housekeeping
     */
    public void update() {

        // Limit maximum speed
        if (omega > MAX_OMEGA) {
            omega = MAX_OMEGA;
            logger.info("already to maximum speed");
        }

        if (velocity.magnitude() > MAX_VELOCITY) {
            velocity.normalizeTo(MAX_VELOCITY);
        }

        theta = normalizeAngle(theta);

        /** Update position and angle of rotation */
        theta += omega;
        position.incrementBy(velocity);

        age++;

        checkBounds();
    }

    protected float normalizeAngle(float angle) {
        while (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }
        while (angle < -Math.PI) {
            angle += 2 * Math.PI;
        }
        return angle;
    }

    // Returns a position vector at the "back" of the spite

    /**
     * @author name : Viriya.L.Gen
     * @create time   : 2020/6/15 1:07
     * @function :
     */
    public Vector getTailPosition() {
        Vector tail = new Vector(position);
        tail.incrementXBy(-Math.cos(theta) * getRadius());
        tail.incrementYBy(-Math.sin(theta) * getRadius());
        return tail;
    }

    /**
     * @return a position vector at the "front" of the sprite
     */
    public Vector getNosePosition() {
        Vector nose = new Vector(position);
        nose.incrementXBy(Math.cos(theta) * getRadius());
        nose.incrementYBy(Math.sin(theta) * getRadius());
        return nose;
    }

    /**
     * CL - We need to synchronize removing actors so we don't have threads
     * stepping on eachother's toes.
     * <p>
     * NOTE: thread concurrency is an advanced topic. This is a base
     * implementation to handle the problem.
     */
    protected void delete() {
        // NOTE: This needs to be thread safe.
        AbstractActor.abstractActors.remove(this);
        setAlive(false);
    }

    /**
     * Call back upon collision detection for object to handle collision
     * It could...
     * Bounce off
     * Explode into many smaller objects
     * Just explode
     *
     * @param other the object this actor collided with
     */
    abstract public void handleCollision(AbstractActor other);

    /**
     * @return the actors current position
     */
    public Vector getPosition() {
        return position;
    }

    /**
     * @return the actors current velocity
     */
    public Vector getVelocity() {
        return velocity;
    }

    /**
     * @return the actors current rotational position
     */
    public float getTheta() {
        return theta;
    }

    /**
     * @return the actors current rotational position in degrees
     */
    public float getThetaDegrees() {
        return theta * 180 / (float) Math.PI;
    }


    /**
     * @return the actors Sprite/Texture
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * @return the actors size (for texture scaling and collision detection)
     */
    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getRadius() {
        return height;
    }


    /**
     * 获取动能值
     *
     * @return Momentum value
     */
    public float getKineticEnergy() {
        // NOTE: Mass is missing from the equation so we throw in volume and leave out density
        float speed = (float) velocity.magnitude();
        return 10 * getMass() * speed * speed;
    }

    public float getMass() {
        /**
         * This does not account for different actors having different densities
         * but the mass should scale with the cube of the linear scale (the volume)
         * But the area is more fun!
         */
        return width * height;
    }

    public Vector getMomentum() {
        Vector p = new Vector(velocity);
        p.scaleBy(getMass());
        return p;
    }

    /**
     * This checks that the position vector is in bounds (the on screen region)
     * and if it passes one side it moves it to the opposite edge.
     */
    private void checkBounds() {
        /**
         * x between -2 and 2
         * y between -1 and 1
         *
         * The width is double length,so range of x is four.
         *
         * The length like something about OpenGL lib
         */
        if (position.x() > RIGHT_WIDTH) {
            position.incrementXBy(-4f);
        } else if (position.x() < LEFT_WIDTH) {
            position.incrementXBy(4);
        }

        if (position.y() > 1) {
            position.incrementYBy(-2f);
        } else if (position.y() < -1) {
            position.incrementYBy(2f);
        }
    }

    protected int generateId() {
        // Pseudo random increments
        return (lastId = +gen.nextInt(1000) + 1);
    }

    public static void removeActorId(int idToRemove) {
        for (AbstractActor a : abstractActors) {
            if (a.id == idToRemove) {
                abstractActors.remove(a);
            }
        }
    }

    public static void updateActors() {
        // Update each actor
        for (int i = 0; i < abstractActors.size(); i++) {
            /**
             * We get the actor only once in case we the actor is removed
             * during the update phase. E.G. Bullets FramesToLive reaches 0.
             */
            AbstractActor a = abstractActors.get(i);
            // Track down actors without ids.
            if (a.id == 0) {
                logger.info("DEBUG: " + a + " actor without ID set");
            }
            a.update();
        }
    }

    public static void collisionDetection() {
        /**
         * Collision detection
         * For each actor, check for collisions with the remaining actors
         * For collision purposes we are modeling each actor as a circle
         * This algorithm is 1/2 n^2 compares, but it should be sufficient for our purposes
         */
        for (int i = 0; i < abstractActors.size(); i++) {
            AbstractActor a = abstractActors.get(i);
            for (int j = i + 1; j < abstractActors.size(); j++) {
                AbstractActor b = abstractActors.get(j);
                if (a.checkCollision(b)) {
                    a.handleCollision(b);
                    b.handleCollision(a);
                }
            }
        }
    }

    /**
     * Check for a collision between this actor and another in the next frame
     *
     * @param other - another actor
     * @return truth if a collision will occur in the next frame
     */
    private boolean checkCollision(AbstractActor other) {
        /*
         * To check for a collision in the next frame we use
         * parametric equations for the position of each object
         * and find where there paths will intersect, and
         * check if it's in the next frame.
         *
         * We model each objects motion with the vector equation
         * 		P + t * V
         *
         * So for two objects we have
         * 		P(1) + t * V(1)
         * and
         * 		P(2) + t * V(2)
         *
         * To find the intersection we set the two equations
         * equal to each other
         * 		P(1) + t * V(1) = P(2) + t * V(2)
         *
         * Then solve for t
         * 		t * V(1) - t * V(2) = P(2) - P(1)
         *
         * 		t * (V(1) - V(2)) = P(2) - P(1)
         *
         *		    P(2) - P(1)
         *		t = -----------
         *		    V(1) - V(2)
         *
         * Since we simply increment the position by the velocity
         * each frame, we just just need to check if there is
         * an intersection in t = 0..1.
         */

        float deltaVelocityX = other.velocity.x() - velocity.x();
        float deltaVelocityY = other.velocity.y() - velocity.y();
        float deltaPositionX = position.x() - other.position.x();
        float deltaPositionY = position.y() - other.position.y();

        /**
         * Our sizes are the diameter of each object and we want the distance between their centers
         */
        float minDistance = getRadius() + other.getRadius();

        /*
         * Since we are looking for an intersection in two dimensions
         * we check for a collision in each dimension and return
         * true only if both are true.
         */
        boolean collideX = isCollision1D(deltaPositionX, deltaVelocityX, minDistance);
        boolean collideY = isCollision1D(deltaPositionY, deltaVelocityY, minDistance);

        return collideX && collideY;
    }

    /**
     * Check for a collision on one dimension
     *
     * @param deltaP  - delta position
     * @param deltaV  - delta velocity
     * @param minDist - minimum distance between particles for a collision to occur, usually the sum of their radii
     * @return truth if a collision will occur in the next frame
     */
    private static boolean isCollision1D(float deltaP, float deltaV, float minDist) {
        /* Since we want to detect collision of objects, rather than just
         * point like particles, we check for collisions our minimum
         * collision distance each side of the point of collision if our
         * our objects where both just points.
         *
         * The code for point collisions is:
         * 		float t = deltaP / deltaV;
         * 		return t >= 0 && t < 1;
         *
         * Note: this doesn't protect against dividing by zero
         */
        // Don't divide by zero
        if (deltaV != 0) {
            // Calculate the extremes of our collision range
            float a = (deltaP - minDist) / deltaV;
            float b = (deltaP + minDist) / deltaV;

            /*
             * There are six cases, excluding the cases
             * where a and b are swapped by a negative
             * deltaV:
             *
             * a--b    a--b    a--b
             *     a--b    a--b
             *      a--------b
             * <-----0------1----->
             * We only check for the two non collision
             * cases, else assume the collision case
             * takes place.
             */
            if (a > 1 && b > 1) {
                return false;
            }
            if (a <= 0 && b <= 0) {
                return false;
            }
        } else {
            /**
             * The zero velocity case is actually much simpler
             */
            if (deltaP >= minDist) {
                return false;
            }
            if (-deltaP >= minDist) {
                return false;
            }
        }
        return true;
    }
    /* End Collision Detection */

    /**
     * Returns a random position on the edge of the screen for an asteroid or bandit
     */
    protected static Vector randomEdge() {
        switch (gen.nextInt(4)) {
            case (0):
                return new Vector(1, gen.nextFloat() * 2 - 1);
            case (1):
                return new Vector(gen.nextFloat() * 2 - 1, 1);
            case (2):
                return new Vector(-1, gen.nextFloat() * 2 - 1);
            default:
                // should only be 3
                return new Vector(gen.nextFloat() * 2 - 1, -1);
        }
    }


    /**
     * @return : Vector 一个随机的位置向量
     * @description : 参与者获取一个随机位置
     * @create : 2020/7/7  13:15
     */
    protected static Vector randomPosition() {
        return new Vector(gen.nextFloat() - gen.nextFloat(), gen.nextFloat() - gen.nextFloat());
    }


    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

}
