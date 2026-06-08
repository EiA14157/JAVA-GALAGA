package galaga;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Swing UI 생성은 이벤트 디스패치 스레드에서 시작하는 것이 안전하다.
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}
