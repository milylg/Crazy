package com.game.frenzied.gunner;

import com.game.frenzied.gunner.common.ParticleSystem;
import com.game.frenzied.gunner.common.ScreenMessage;
import com.game.frenzied.gunner.common.SoundEffect;
import com.game.frenzied.gunner.domain.AbstractActor;
import com.game.frenzied.gunner.domain.BombShelter;
import com.game.frenzied.gunner.domain.Cannon;
import com.game.frenzied.gunner.domain.PlaneFleet;
import com.game.frenzied.gunner.gui.GameGui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrenziedGunner {

    private static final Logger logger = LoggerFactory.getLogger(FrenziedGunner.class);

    private static GameGui gameGui;
    private static Cannon playerCannon;

    /**
     * 游戏暂停状态
     */
    private static boolean paused;

    private static boolean gameOver = false;

    private static int width, height;

    private static boolean highScoreSubmitted;

    static {

    }

    public static void launch() {
        /**
         * 加载音频和音频设置
         * 窗口信息初始化
         * 初始化防空山体，
         * 初始化计分系统
         * 主窗口初始化
         * 菜单栏初始化
         */
        SoundEffect.init(true);
        ParticleSystem.init();
        ScreenMessage.init();
        gameGui = new GameGui();
        gameGui.registerStartEventHandler(new StartEvent());
        gameGui.registerPauseEventHandler(new PauseEvent());
    }

    private static class StartEvent implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            init();
            gameGui.updateState("State: Running   ");
        }
    }

    private static class PauseEvent implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            paused = isPaused() ? false : true;
            // TODO: REFACTOR IT
            gameGui.updateState(paused ? "State: Paused   " : "State: Running   ");
            PlaneFleet.pause();
        }
    }

    /**
     * This is called by ScenePanel as the beginning of the game
     * put any game initialization code here.
     * <p>
     * 游戏初始化
     */
    public static void init() {
        paused = false;
        gameOver = false;

        width = gameGui.getWidth();
        height = gameGui.getHeight();

        AbstractActor.abstractActors.clear();

        BombShelter.buildShelter();

        playerCannon = Cannon.build();

        gameGui.updateAntiBallLabel(playerCannon.getAntiaircraftBalls());
        gameGui.updateCannonBallLabel(playerCannon.getCannonBalls());

        playerCannon.setHintMessageCallback(gameGui.getCallBack());
        playerCannon.setAliveCallback(gameGui.getAliveCallback());
        playerCannon.resetAlive();

        PlaneFleet.reset();
        PlaneFleet.deploy();
    }

    /**
     * This is called every frame by the ScenePanel
     * put game code here
     */
    public static void update() {
        if (paused) {
            return;
        }
        gameMechanics();
        AbstractActor.collisionDetection();
        AbstractActor.updateActors();
        ScreenMessage.updateMessages();
        ParticleSystem.updateParticles();
    }

    /**
     * This is called by ScenePanel at the end of the game
     * any cleanup code should go here
     */
    public static void gameMechanics() {
        // Game over man!
        if (!playerCannon.isAlive()) {
            gameOver = true;
            if (!highScoreSubmitted) {
                highScoreSubmitted = true;
                ScreenMessage.add(new ScreenMessage("Game Over!"));
            }
        }
    }


    public static boolean isPaused() {
        return paused;
    }

    public static void togglePause() {
        paused = paused ? false : true;
    }

    /**
     * Used by main menu to determine if we should show start game or resume game
     *
     * @return 游戏是否处于开始的状态
     */
    public static boolean isStarted() {
        return playerCannon.isAlive();
    }

    public static Cannon getCannonPlayer() {
        return playerCannon;
    }

}
