package galaga;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;

public class GameRenderer {
    private final Font titleFont;
    private final Font bodyFont;
    private final Font hudFont;
    private final GameAssets assets;

    public GameRenderer(GameAssets assets) {
        this.assets = assets;
        titleFont = new Font("SansSerif", Font.BOLD, 34);
        bodyFont = new Font("SansSerif", Font.PLAIN, 18);
        hudFont = new Font("SansSerif", Font.BOLD, 16);
    }

    public void render(JPanel panel, Graphics g, GameSession session) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // 창 크기가 바뀌어도 내부 좌표계는 800x600 기준으로 유지한다.
        g2.scale(panel.getWidth() / (double) GameConfig.BASE_WIDTH, panel.getHeight() / (double) GameConfig.BASE_HEIGHT);

        drawBackground(g2);

        if (session.getGameState() == GameState.START) {
            drawStartScreen(g2);
        } else {
            drawGame(g2, session);

            if (session.getGameState() == GameState.GAME_OVER) {
                drawOverlay(g2, "GAME OVER", "Press Enter to restart");
            } else if (session.getGameState() == GameState.CLEAR) {
                drawOverlay(g2, "STAGE CLEAR", "You finished all waves. Press Enter to restart");
            }
        }

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2) {
        BufferedImage backgroundSprite = assets.getBackgroundSprite();
        if (backgroundSprite != null) {
            g2.drawImage(backgroundSprite.getScaledInstance(GameConfig.BASE_WIDTH, GameConfig.BASE_HEIGHT, Image.SCALE_SMOOTH), 0, 0, null);
            return;
        }

        g2.setColor(new Color(10, 18, 40));
        g2.fillRect(0, 0, GameConfig.BASE_WIDTH, GameConfig.BASE_HEIGHT);
    }

    private void drawGame(Graphics2D g2, GameSession session) {
        drawHud(g2, session);
        drawPlayer(g2, session.getPlayer());
        drawEnemies(g2, session.getEnemies());
        drawBullets(g2, session.getPlayerBullets(), assets.getPlayerBulletSprite(), new Color(255, 246, 115));
        drawBullets(g2, session.getEnemyBullets(), assets.getEnemyBulletSprite(), new Color(255, 110, 110));
    }

    private void drawHud(Graphics2D g2, GameSession session) {
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + session.getScore(), 20, 28);
        g2.drawString("Lives: " + session.getPlayer().getLives(), 20, 52);
        g2.drawString("Stage: " + session.getStage(), GameConfig.BASE_WIDTH - 100, 28);
        g2.setColor(new Color(120, 180, 255));
        g2.drawLine(0, GameConfig.HUD_LINE_Y, GameConfig.BASE_WIDTH, GameConfig.HUD_LINE_Y);
    }

    private void drawPlayer(Graphics2D g2, Player player) {
        drawSprite(g2, assets.getPlayerSprite(), player.getX() - 4, player.getY() - 8, player.getWidth() + 8, player.getHeight() + 16);
    }

    private void drawEnemies(Graphics2D g2, List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            drawSprite(g2, assets.getEnemySprite(), enemy.getX() - 5, enemy.getY() - 4, enemy.getWidth() + 10, enemy.getHeight() + 12);
        }
    }

    private void drawBullets(Graphics2D g2, List<Bullet> bullets, BufferedImage sprite, Color color) {
        for (Bullet bullet : bullets) {
            if (sprite != null) {
                drawSprite(g2, sprite, bullet.getX() - 2, bullet.getY() - 4, bullet.getWidth() + 6, bullet.getHeight() + 8);
                continue;
            }

            g2.setColor(color);
            g2.fillRoundRect(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight(), 4, 4);
        }
    }

    private void drawStartScreen(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        drawCenteredString(g2, "JAVA GALAGA", 190);

        g2.setFont(bodyFont);
        g2.setColor(new Color(214, 225, 255));
        drawCenteredString(g2, "Arrow Keys: Move", 280);
        drawCenteredString(g2, "Space: Fire", 315);
        drawCenteredString(g2, "Enter: Start Game", 350);
    }

    private void drawOverlay(Graphics2D g2, String title, String subtitle) {
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(150, 190, 500, 170, 24, 24);
        g2.setColor(Color.WHITE);
        g2.setFont(titleFont);
        drawCenteredString(g2, title, 255);
        g2.setFont(bodyFont);
        drawCenteredString(g2, subtitle, 310);
    }

    private void drawCenteredString(Graphics2D g2, String text, int y) {
        int textWidth = g2.getFontMetrics().stringWidth(text);
        int x = (GameConfig.BASE_WIDTH - textWidth) / 2;
        g2.drawString(text, x, y);
    }

    private void drawSprite(Graphics2D g2, BufferedImage sprite, int x, int y, int width, int height) {
        if (sprite != null) {
            g2.drawImage(sprite.getScaledInstance(width, height, Image.SCALE_SMOOTH), x, y, null);
            return;
        }

        g2.setColor(new Color(90, 255, 205));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x, y, width, height, 8, 8);
    }
}
