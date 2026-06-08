package galaga;

import javax.swing.JFrame;

public class GameFrame extends JFrame {
    private final GamePanel panel;

    public GameFrame() {
        setTitle("Java Galaga");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        panel = new GamePanel();
        add(panel);
        pack();
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
