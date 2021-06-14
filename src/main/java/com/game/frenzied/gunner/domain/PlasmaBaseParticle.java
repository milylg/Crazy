package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.Vector;

/**
 * @author Administrator
 */
public class PlasmaBaseParticle extends BaseParticle {

	private static final long serialVersionUID = -5250833419817334839L;

	private static final float PLASMA_VELOCITY = 0.001f;
	private static final int PLASMA_LIFETIME = 40;
	private static final float PLASMA_SPIN = 0.2f;
	private final float PLASMA_SIZE = 0.01f;
	
	public PlasmaBaseParticle(AbstractActor ship) {
		position = new Vector(ship.getTailPosition());
		// Relative to the ship
		velocity = new Vector(ship.getVelocity());
		// Add the speed of the shot
		velocity.incrementXBy(Math.cos(gen.nextFloat() * 2 * Math.PI));
		velocity.incrementYBy(Math.sin(gen.nextFloat() * 2 * Math.PI));
		/**
		 * NOTE: our vectors may not be normalized but it will make the particles
		 * look like they are moving in 3D, this looks more natural
 		 */
		velocity.scaleBy(PLASMA_VELOCITY);
		init();
	}
	
	public PlasmaBaseParticle(Vector pos, Vector vel){
		position = pos;
		// They don't get to set our speed, only direction
		velocity = vel.normalize().scaleBy(PLASMA_VELOCITY);
		init();
	}
	
	public PlasmaBaseParticle(Vector pos) {
		position = new Vector(pos);
		velocity = new Vector();
		velocity.incrementXBy(Math.cos(gen.nextFloat() * 2 * Math.PI));
		velocity.incrementYBy(Math.sin(gen.nextFloat() * 2 * Math.PI));
		velocity.scaleBy(PLASMA_VELOCITY);
		init();
	}
	
	private void init(){
		theta = 0;
		omega = PLASMA_SPIN;
		width = PLASMA_SIZE;
		height = PLASMA_SIZE;
		// Rosy Brown
		colorR = 1.0f;
		colorG = 1.0f;
		colorB = 1.0f;
		colorA = 1.0f;	
	}

	@Override
	public void update() {
		super.update();

		if (width >= 0.0001f) {
			width -= age * 0.0001;
			height -= age * 0.0001;
		}
		if (age > PLASMA_LIFETIME) {
			delete();
		}
	}


	@Override
	protected void updateColor() {
		colorR -= 0.2f;
		colorG -= 0.1f;
		colorB -= 0.05f;
		if(colorB < 0.5f) {
			colorB = 0.5f;
		}
		if(colorG < 0.2f) {
			colorG = 0.2f;
		}
	}
}

