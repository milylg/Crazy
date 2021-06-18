package com.game.frenzied.gunner.common;

import javazoom.jl.player.advanced.AdvancedPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * @Description : play a sound of type
 * TODO: What if someone wanted to play with sound off?
 * @Author : Viriya.L.Gen
 * @DateTime : 2020/6/15  17:47
 */
public class SoundEffect {

    private static final Logger log = LoggerFactory.getLogger(SoundEffect.class);

    private static final String BULLET_SHOT_FILE = "explode.mp3";
    private static final String BULLET_HIT_FILE = "ship_explosion.mp3";
    private static final String BULLET_QUICKLY_SHOT_FILE = "flak.mp3";
    private static final String TRANSPORT_PLANE_FLY_FILE = "transport.mp3";
    private static final String EMPTY_BALL_FILE = "empty-ball.mp3";
    private static final String WARN_CLOCK_FILE = "alarm-clock.mp3";
    private static final String SMALL_BOOM_HIT = "small-explode.mp3";

    /**
     * whether open sound of actor
     */
    private static boolean enabled;

    /**
     * We only want to make one copy of each sound effect and play the
     * same sound multiple times. So we have them as static members/fields
     */
    private static boolean loaded;
    private static Sound bulletShot;
    private static Sound cannonBoomHit;
    private static Sound bulletQuicklyShot;
    private static Sound transportPlaneFly;
    private static Sound emptyBall;
    private static Sound warnClock;
    private static Sound smallBoomHit;


    /**
     * Initializes our sound effects
     *
     * @param isEnabled : whether open sound of actor
     */
    public static void init(boolean isEnabled) {
        loaded = false;
        if (isEnabled) {
            loadSounds();
        } else {
            log.info("the sound media resource load closed");
            enabled = false;
        }
    }

    static private void loadSounds() {
        enabled = true;
        bulletShot = new Sound(BULLET_SHOT_FILE);
        cannonBoomHit = new Sound(BULLET_HIT_FILE);
        bulletQuicklyShot = new Sound(BULLET_QUICKLY_SHOT_FILE);
        transportPlaneFly = new Sound(TRANSPORT_PLANE_FLY_FILE);
        emptyBall = new Sound(EMPTY_BALL_FILE);
        warnClock = new Sound(WARN_CLOCK_FILE);
        smallBoomHit = new Sound(SMALL_BOOM_HIT);
        loaded = true;
    }

    static public boolean isEnabled() {
        return enabled;
    }

    static public boolean isEnabled(boolean toggle) {
        enabled = toggle;
        if (enabled && !loaded) {
            loadSounds();
        }
        return enabled;
    }

    /**
     * @return our sound for shooting bullets
     */
    static public Sound forBulletShot() {
        return bulletShot;
    }

    /**
     * @return our sound for when bullets hit things
     */
    static public Sound forSmallBoomHit() {
        return smallBoomHit;
    }

    static public Sound forCannonBoom() {
        return cannonBoomHit;
    }


    static public Sound forQuicklyShot() {
        return bulletQuicklyShot;
    }

    static public Sound forTransportPlaneFly() {
        return transportPlaneFly;
    }

    static public Sound forEmptyBall() {
        return emptyBall;
    }

    static public Sound forWarnClock() {
        return warnClock;
    }


    public static class Sound {

        static final Logger log = LoggerFactory.getLogger(SoundEffect.class);
        static final String SOUND_DIR;

        static {
            SOUND_DIR = SystemConstant.valueOf("SOUND_DIR");
        }

        private File soundFile;

        /**
         * Constructor takes the filePath
         */
        public Sound(String filename) {
            this(new File(SOUND_DIR, filename));
        }

        public Sound(File file) {
            soundFile = file;
        }

        private AdvancedPlayer open() {
            try {
                FileInputStream fis = new FileInputStream(soundFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                return new AdvancedPlayer(bis);
            } catch (Exception e) {
                log.warn("open file failed:{}", soundFile);
            }
            return null;
        }

        /**
         * FIXME: There must be a cleaner way to do the playing of sounds in threads.
         */
        public void play() {
            // Run in new thread to play in background
            // by creating an anonymous class like a Functor in C++
            // or a block in Ruby
            new Thread() {
                @Override
                public void run() {
                    try {
                        AdvancedPlayer player = open();
                        player.play();
                        player.close();
                    } catch (Exception e) {
                       log.warn("play media file exception:{}", e.getMessage());
                    }
                }
            }.start();
        }
    }
}
