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
        add(panel);
        pack();
        setMinimumSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            panel.requestFocusInWindow();
        }
    }
}
