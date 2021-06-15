package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.Sprite;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class PlaneFleetTest {

    @Test
    public void deploy() throws InterruptedException {
        PlaneFleet.deploy();
        TimeUnit.SECONDS.sleep(40);
    }

    @Test
    public void pause() throws InterruptedException {
        PlaneFleet.deploy();
        TimeUnit.SECONDS.sleep(4);
        PlaneFleet.pause(true); // pause
        System.out.println("pause start");
        TimeUnit.SECONDS.sleep(4);
        PlaneFleet.pause(false); // no pause
        System.out.println("pause end...... then print");
        TimeUnit.SECONDS.sleep(14);
    }
}