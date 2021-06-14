package com.game.frenzied.gunner.common;


import org.junit.Assert;
import org.junit.Test;

public class SystemConstantTest{

    @Test
    public void testValueOf() {
        Assert.assertEquals("src/main/resources/", SystemConstant.valueOf("TEXTURE_DIR"));
        Assert.assertEquals("src/main/resources/sound/", SystemConstant.valueOf("SOUND_DIR"));
    }
}