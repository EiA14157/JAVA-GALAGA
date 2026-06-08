package galaga;

import java.awt.Dimension;
import javax.swing.JFrame;

public class GameFrame extends JFrame {
    private final GamePanel panel;

    public GameFrame() {
        setTitle("Java Galaga");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        panel = new GamePanel();
        // GamePanel owns the game loop and all rendering work.
        add(panel);
        pack();
        setMinimumSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            // Hand focus to the panel so keyboard input works immediately.
            panel.requestFocusInWindow();
        }
    }
}
