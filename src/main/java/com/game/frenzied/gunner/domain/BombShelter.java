package com.game.frenzied.gunner.domain;

import com.game.frenzied.gunner.common.Vector;

import java.util.List;

/**
 * @Author: milylg
 * @Description: 
 * @CreateDate: 2021/6/13 17:50
 */
public class BombShelter {


    public static void buildShelter() {
        Vector left = new Vector(-0.3f,-0.6f);
        Brick brick;
        for (int i = 0; i < 9; i ++) {
            brick = new Brick(left);
            AbstractActor.abstractActors.add(brick);
            left.incrementXBy(0.08f);
        }

        left.setX(-0.3f);
        left.setY( -0.55f);
        for (int j = 0; j < 9; j ++) {
            brick = new Brick(left);
            AbstractActor.abstractActors.add(brick);
            left.incrementXBy(0.08f);
        }
    }

}
