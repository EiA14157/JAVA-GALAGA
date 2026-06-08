package galaga;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GameAssets {
    private final BufferedImage backgroundSprite;
    private final BufferedImage playerSprite;
    private final BufferedImage enemySprite;
    private final BufferedImage playerBulletSprite;
    private final BufferedImage enemyBulletSprite;

    public GameAssets() {
        backgroundSprite = loadSprite("space_background.png");
        playerSprite = loadSprite("player_ship.png");
        enemySprite = loadSprite("enemy_bug.png");
        playerBulletSprite = loadSprite("player_laser.png");
        enemyBulletSprite = loadSprite("enemy_laser.png");
    }

    public BufferedImage getBackgroundSprite() {
        return backgroundSprite;
    }

    public BufferedImage getPlayerSprite() {
        return playerSprite;
    }

    public BufferedImage getEnemySprite() {
        return enemySprite;
    }

    public BufferedImage getPlayerBulletSprite() {
        return playerBulletSprite;
    }

    public BufferedImage getEnemyBulletSprite() {
        return enemyBulletSprite;
    }

    private BufferedImage loadSprite(String fileName) {
        File file = new File(GameConfig.ASSET_DIR, fileName);
        if (!file.exists()) {
            return null;
        }

        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            // 이미지를 불러오지 못하면 도형 렌더링으로 대체한다.
            return null;
        }
    }
}
