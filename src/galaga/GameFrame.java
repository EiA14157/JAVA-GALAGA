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
        // 실제 게임 루프와 렌더링은 GamePanel이 전담한다.
        add(panel);
        pack();
        setMinimumSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            // 창이 보이면 패널에 포커스를 줘서 키 입력이 바로 동작하게 한다.
            panel.requestFocusInWindow();
        }
    }
}
