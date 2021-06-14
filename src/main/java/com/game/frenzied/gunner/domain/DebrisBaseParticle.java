package com.game.frenzied.gunner.domain;


import com.game.frenzied.gunner.common.Vector;

/**
 * @author Administrator
 */
public class DebrisBaseParticle extends BaseParticle {

	private static final long serialVersionUID = 3447074213725536147L;
	/**
	 * 碎片的速率
	 */
	private static final float DEBRIS_VELOCITY = 0.01f;
	/**
	 * 碎片的生命周期
	 */
	private static final int DEBRIS_LIFETIME = 20;
	/**
	 * 碎片的旋转
	 */
	private static final float DEBRIS_SPIN = 0;
	private final float DEBRIS_SIZE = 0.02f;

	public DebrisBaseParticle(AbstractActor ship) {
		position = new Vector(ship.getTailPosition());
		// Relative to the ship
		velocity = new Vector(ship.getVelocity());
		// Add the speed of the shot
		velocity.incrementXBy(Math.cos(gen.nextFloat() * 2 * Math.PI));
		velocity.incrementYBy(Math.sin(gen.nextFloat() * 2 * Math.PI));
		// NOTE our vectors may not be normalized but it will make the particles
		//      look like they are moving in 3D, this looks more natural
		velocity.scaleBy(DEBRIS_VELOCITY);
		init();
	}

	public DebrisBaseParticle(Vector pos, Vector vel) {
		position = pos;
		// They don't get to set our speed, only direction
		velocity = vel.normalize().scaleBy(DEBRIS_VELOCITY);
		init();
	}

	public DebrisBaseParticle(Vector pos) {
		position = pos;
		velocity = new Vector();
		velocity.incrementXBy(Math.cos(gen.nextFloat() * 2 * Math.PI));
		velocity.incrementYBy(Math.sin(gen.nextFloat() * 2 * Math.PI));
		velocity.scaleBy(DEBRIS_VELOCITY);
		init();
	}

	private void init(){
		theta = 0;
		// TODO textures for our DEBRIS
		omega = DEBRIS_SPIN;
		width = DEBRIS_SIZE;
		height = DEBRIS_SIZE;
		// Rosy Brown
		colorR = 0.6372549019607844f;
		colorG = 0.4607843137254902f;
		colorB = 0.4607843137254902f;
		colorA = 1.0f;
	}

	@Override
	public void update() {
		super.update();
		if (age > DEBRIS_LIFETIME) {
			delete();
		}
	}


	@Override
	protected void updateColor() {
		// We stay brown
	}
}