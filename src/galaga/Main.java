package galaga;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Swing UI work should start on the event dispatch thread.
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}
