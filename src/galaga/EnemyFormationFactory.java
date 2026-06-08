package galaga;

import java.util.ArrayList;
import java.util.List;

public class EnemyFormationFactory {
    public List<Enemy> createFormation(int stage) {
        // Later stages add rows so the wave gets denser without changing layout logic.
        int rows = 3 + Math.min(stage, 2);
        int cols = 6;
        int startX = 120;
        int startY = 70;
        int gapX = 88;
        int gapY = 54;
        List<Enemy> enemies = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int enemyX = startX + col * gapX;
                int enemyY = startY + row * gapY;
                enemies.add(new Enemy(enemyX, enemyY, row));
            }
        }

        return enemies;
    }
}
