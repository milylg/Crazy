package com.game.frenzied.gunner.gui;

import com.game.frenzied.gunner.domain.Cannon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Locale;

public class GameGui extends JFrame {

    private static final long serialVersionUID = -934931618056969704L;

    private static final Logger log = LoggerFactory.getLogger(GameGui.class);

    /**
     *  Applies our could scheme to a swing component
     */
    public static void colorize(JComponent component) {
        if (component instanceof JButton) {
            component.setBackground(Color.DARK_GRAY);
            component.setForeground(Color.WHITE);
        } else {
            component.setBackground(Color.BLACK);
            component.setForeground(Color.GREEN);
        }

        // Recursively colorize all children
        for (Component child: component.getComponents()) {
            if (child instanceof JComponent) {
                colorize((JComponent) child);
            }
        }
    }


    /**
     * 	These are the fields for the GUI class
     */

    private GameScene scene;
    private Button pauseGame;
    private Button startGame;
    private JLabel antiaircraftBallRemain;
    private JLabel cannonballRemain;
    private JLabel percentage;
    private JLabel alive;

    public GameGui() {
        // This means the program stops at the close of the main window.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a BorderLayout manager for the panels
        setLayout(new BorderLayout());

        scene = new GameScene();

        // Add them to the content pane and put where you want them.
        add(scene, BorderLayout.CENTER);
        add(buttonMessage(), BorderLayout.SOUTH);

        // Pack the contents of the window and display it.
        pack();
        setLocationRelativeTo(null);
        setAutoRequestFocus(true);

        // Initially it is not going to be visible
        setVisible(true);
        // colorize(this.getRootPane());
    }


    private JPanel buttonMessage() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(this.getWidth(),30));
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pauseGame = new Button("Pause");
        startGame = new Button("Start");

        addAliveLabel(panel);
        addPercentageLabel(panel);
        addAntiaircraftBallRemainLabel(panel);
        addCannonBallRemainLabel(panel);

        panel.add(startGame);
        panel.add(pauseGame);

        countBallCallBack = (cannonballs, antiBalls) -> {
            updateAntiBallLabel(antiBalls);
            updateCannonBallLabel(cannonballs);

        };

        hitRateCallBack = (planes, shotCount) -> {
            updateHitRate(planes, shotCount);
        };

        aliveCallback = (isAlive -> {
            updateAlive(isAlive);
        });

        return panel;
    }


    private static final Font DEFAULT_FONT = new Font(null, Font.PLAIN,15);


    private void addAntiaircraftBallRemainLabel(JPanel panel) {
        antiaircraftBallRemain = new JLabel("[AntiBall: 100]");
        antiaircraftBallRemain.setForeground(Color.GREEN);
        antiaircraftBallRemain.setFont(DEFAULT_FONT);
        panel.add(antiaircraftBallRemain);
    }

    private void addCannonBallRemainLabel(JPanel panel) {
        cannonballRemain = new JLabel("[CannonBall: 20]");
        cannonballRemain.setForeground(Color.GREEN);
        cannonballRemain.setFont(DEFAULT_FONT);
        panel.add(cannonballRemain);
    }

    private void addPercentageLabel(JPanel panel) {
        percentage = new JLabel("[Hit Rate: 0.00%]");
        percentage.setForeground(Color.GREEN);
        percentage.setFont(DEFAULT_FONT);
        panel.add(percentage);
    }

    private void addAliveLabel(JPanel panel) {
        alive = new JLabel();
        alive.setOpaque(true);
        alive.setForeground(Color.GREEN);
        alive.setBackground(Color.GRAY);
        alive.setFont(DEFAULT_FONT);
        panel.add(alive);
    }


    public void registerPauseEventHandler(ActionListener action) {
        pauseGame.addActionListener(action);
    }

    public void registerStartEventHandler(ActionListener action) {
        startGame.addActionListener(action);
    }

    public void updateOperateState(boolean isStarted) {
        if (isStarted) {
            pauseGame.setBackground(Color.DARK_GRAY);
            pauseGame.setForeground(Color.ORANGE);
            startGame.setBackground(Color.DARK_GRAY);
            startGame.setForeground(Color.ORANGE);
        } else {
            pauseGame.setBackground(Color.DARK_GRAY);
            pauseGame.setForeground(Color.ORANGE);
            startGame.setBackground(Color.GREEN);
            startGame.setForeground(Color.DARK_GRAY);
        }
    }

    public void updateAntiBallLabel(int balls) {
        antiaircraftBallRemain.setText("[AntiBall: " + balls + "]");
    }

    public void updateCannonBallLabel(int balls) {
        cannonballRemain.setText("[CannonBall: " + balls + "]");
    }

    public void updateHitRate(int planes, int shutCount) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getPercentInstance(Locale.CHINA);
        format.setMinimumFractionDigits(2);
        percentage.setText("[Hit Rate: " + format.format(planes * 1f / shutCount) + "]");
    }

    public void updateAlive(boolean isAlive) {
        if (!isAlive) {
            alive.setForeground(Color.ORANGE);
        } else {
            alive.setForeground(Color.GREEN);
        }
        alive.setText(isAlive ? "  Alive  " : "  Died  ");
    }


    private Cannon.CountBallCallBack countBallCallBack;

    public Cannon.CountBallCallBack getCallBack() {
        return countBallCallBack;
    }

    private Cannon.AliveCallback aliveCallback;

    public Cannon.AliveCallback getAliveCallback() {
        return aliveCallback;
    }

    private Cannon.HitRateCallBack hitRateCallBack;

    public Cannon.HitRateCallBack getHitRateCallBack() {
        return hitRateCallBack;
    }
}
