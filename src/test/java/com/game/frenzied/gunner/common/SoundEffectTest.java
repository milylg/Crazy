package com.game.frenzied.gunner.common;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SoundEffectTest {

    @Test
    public void forBulletShot() throws InterruptedException {
        SoundEffect.init(true);
        SoundEffect.Sound sound = SoundEffect.forBulletShot();
        sound.play();
        TimeUnit.SECONDS.sleep(7);
    }
}