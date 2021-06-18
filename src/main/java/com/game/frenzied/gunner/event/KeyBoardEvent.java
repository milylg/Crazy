package com.game.frenzied.gunner.event;

import com.game.frenzied.gunner.FrenziedGunner;
import com.game.frenzied.gunner.domain.Cannon;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyBoardEvent {

    public static KeyboardEventHandler listener = new KeyboardEventHandler();
    static Cannon player;


    public static void update() {
        KeyboardEventHandler.poll();
        player = FrenziedGunner.getCannonPlayer();
        if (listener.keyDown(KeyEvent.VK_LEFT)) {
            player.turnLeft();
        }
        if (listener.keyDown(KeyEvent.VK_RIGHT)) {
            player.turnRight();
        }
        if (listener.keyDownOnce(KeyEvent.VK_SPACE)) {
            player.shootBall();
        }
        if (listener.keyDown(KeyEvent.VK_DOWN)) {
            player.leftMove();
        }
        if (listener.keyDown(KeyEvent.VK_UP)) {
            player.rightMove();
        }
        if (listener.keyDown(KeyEvent.VK_Q)) {
            player.quicklyShoot();
        }
    }


    static class KeyboardEventHandler implements KeyListener {

        private static final int KEY_COUNT = 256;

        public enum KeyState {
            /**
             * Not down
             */
            RELEASED,
            /**
             * Down, but not the first time
             */
            PRESSED,
            /**
             * Down for the first time
             */
            ONCE
        }

        /**
         * Current state of the keyboard
         */
        private static boolean[] currentKeys = null;

        /**
         * Polled keyboard state
         */
        private static KeyState[] keys = null;

        public KeyboardEventHandler() {
            currentKeys = new boolean[KEY_COUNT];
            keys = new KeyState[KEY_COUNT];
            for (int i = 0; i < KEY_COUNT; ++i) {
                keys[i] = KeyState.RELEASED;
            }
        }

        public static void poll() {
            for (int i = 0; i < KEY_COUNT; ++i) {
                // Set the key state
                if (currentKeys[i]) {
                    // If the key is down now, but was not
                    // down last frame, set it to ONCE,
                    // otherwise, set it to PRESSED
                    if (keys[i] == KeyState.RELEASED) {
                        keys[i] = KeyState.ONCE;
                    } else {
                        keys[i] = KeyState.PRESSED;
                    }
                } else {
                    keys[i] = KeyState.RELEASED;
                }
            }
        }

        public boolean keyDown(int keyCode) {
            return keys[keyCode] == KeyState.ONCE ||
                    keys[keyCode] == KeyState.PRESSED;
        }

        public boolean keyDownOnce(int keyCode) {
            return keys[keyCode] == KeyState.ONCE;
        }

        public boolean keyReleased(int keyCode) {
            return keys[keyCode] == KeyState.RELEASED;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            try {
                int keyCode = e.getKeyCode();
                if (keyCode >= 0 && keyCode < KEY_COUNT) {
                    currentKeys[keyCode] = true;
                }
            } catch (Exception x) {
                int keyCode = e.getKeyCode();
                if (keyCode >= 0 && keyCode < KEY_COUNT) {
                    currentKeys[keyCode] = true;
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            try {
                int keyCode = e.getKeyCode();
                if (keyCode >= 0 && keyCode < KEY_COUNT) {
                    currentKeys[keyCode] = false;
                }

            } catch (Exception x) {
                int keyCode = e.getKeyCode();
                if (keyCode >= 0 && keyCode < KEY_COUNT) {
                    currentKeys[keyCode] = false;
                }
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // Not needed
        }
    }

}
