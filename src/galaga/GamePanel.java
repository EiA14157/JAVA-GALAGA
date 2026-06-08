package galaga;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {
    private final Timer timer;
    private final GameSession session;
    private final GameRenderer renderer;
    private final InputState inputState;

    public GamePanel() {
        setPreferredSize(new Dimension(GameConfig.BASE_WIDTH, GameConfig.BASE_HEIGHT));
        setBackground(new Color(6, 10, 24));
        setFocusable(true);

        session = new GameSession();
        renderer = new GameRenderer(new GameAssets());
        inputState = new InputState();
        addKeyListener(new KeyHandler());

        timer = new Timer(GameConfig.TIMER_DELAY_MS, this);
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        session.update(inputState);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(this, g, session);
    }

    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_LEFT) {
                inputState.setLeftPressed(true);
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                inputState.setRightPressed(true);
            } else if (keyCode == KeyEvent.VK_SPACE) {
                inputState.setSpacePressed(true);
                // Fire once on key press so shooting feels responsive before repeat kicks in.
                session.firePlayerBulletIfPossible();
            } else if (keyCode == KeyEvent.VK_ENTER) {
                if (session.canRestart()) {
                    session.startPlaying();
                    requestFocusInWindow();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_LEFT) {
                inputState.setLeftPressed(false);
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                inputState.setRightPressed(false);
            } else if (keyCode == KeyEvent.VK_SPACE) {
                inputState.setSpacePressed(false);
            }
        }
    }
}
