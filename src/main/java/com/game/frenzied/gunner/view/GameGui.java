package com.game.frenzied.gunner.view;

import com.game.frenzied.gunner.common.SystemConstant;
import com.game.frenzied.gunner.domain.Cannon;
import com.game.frenzied.gunner.domain.PlaneFleet;
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
    private JLabel gameState;
    private JLabel percentage;

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
    }


    private JPanel buttonMessage() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(this.getWidth(),30));
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pauseGame = new Button("Pause");
        startGame = new Button("Start");

        addPercentageLabel(panel);
        addAntiaircraftBallRemainLabel(panel);
        addCannonBallRemainLabel(panel);
        addGameStateLabel(panel);

        panel.add(startGame);
        panel.add(pauseGame);

        callBack = new Cannon.CallBack() {
            @Override
            public void update(int cannonballs, int antiBalls, int planes, int shotCount) {
                updateAntiBallLabel(antiBalls);
                updateCannonBallLabel(cannonballs);
                updatePercentage(planes, shotCount);
            }
        };

        return panel;
    }

    private void addAntiaircraftBallRemainLabel(JPanel panel) {
        antiaircraftBallRemain = new JLabel("Anti Ball Remain:200");
        antiaircraftBallRemain.setForeground(Color.GREEN);
        antiaircraftBallRemain.setFont(new Font(null, Font.PLAIN,16));
        panel.add(antiaircraftBallRemain);
    }

    private void addCannonBallRemainLabel(JPanel panel) {
        cannonballRemain = new JLabel("Cannon Ball Remain:200");
        cannonballRemain.setForeground(Color.GREEN);
        cannonballRemain.setFont(new Font(null, Font.PLAIN,16));
        panel.add(cannonballRemain);
    }

    private void addPercentageLabel(JPanel panel) {
        percentage = new JLabel("Percentage: 0.00%  | ");
        percentage.setForeground(Color.GREEN);
        percentage.setFont(new Font(null, Font.PLAIN,16));
        panel.add(percentage);
    }


    private void addGameStateLabel(JPanel panel) {
        gameState = new JLabel("State: running");
        gameState.setForeground(Color.GREEN);
        gameState.setFont(new Font(null, Font.PLAIN,16));
        panel.add(gameState);
    }


    public void registerPauseEventHandler(ActionListener action) {
        pauseGame.addActionListener(action);
    }

    public void registerStartEventHandler(ActionListener action) {
        startGame.addActionListener(action);
    }

    public void updateState(String message) {
        gameState.setText(message);
    }

    public void updateAntiBallLabel(int balls) {
        antiaircraftBallRemain.setText("Anti Ball Remain:" + balls + "  | ");
    }

    public void updateCannonBallLabel(int balls) {
        cannonballRemain.setText("Cannon Ball Remain:" + balls + "  | ");
    }

    public void updatePercentage(int planes, int shutCount) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getPercentInstance(Locale.CHINA);
        format.setMinimumFractionDigits(2);
        percentage.setText("Percentage: " + format.format(planes * 1f / shutCount) + "  | ");
    }


    private Cannon.CallBack callBack;

    public Cannon.CallBack getCallBack() {
        return callBack;
    }
}
