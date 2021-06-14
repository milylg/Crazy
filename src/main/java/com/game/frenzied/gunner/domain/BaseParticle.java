package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.ParticleSystem;
import com.game.frenzied.gunner.domain.AbstractActor;

/**
 * 碎片的抽象类
 *
 * @author Administrator
 */
public abstract class BaseParticle extends AbstractActor {

	private static final long serialVersionUID = -1288874741029406494L;

	public float colorR;
	public float colorG;
	public float colorB;
	public float colorA;
	
	@Override
	public void update(){
		// Update our position
		super.update();
		updateColor();
	}

	/**
	 * 更新碎片的颜色，不同尺寸大小的粒子颜色是不同的
	 */
	abstract protected void updateColor();
	
	@Override
	public void delete(){
		ParticleSystem.baseParticles.remove(this);
	}
	
	@Override
	public void handleCollision(AbstractActor other) {
		/**
		 * We have no collision value
		 */
		return;
	}
}
