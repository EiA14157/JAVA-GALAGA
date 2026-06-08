package galaga;

import java.util.ArrayList;
import java.util.List;

public class EnemyFormationFactory {
    public List<Enemy> createFormation(int stage) {
        // 스테이지가 올라갈수록 적 행 수를 늘려 밀도를 높인다.
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
