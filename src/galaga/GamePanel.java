package galaga;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {
    private static final int BASE_WIDTH = 800;
    private static final int BASE_HEIGHT = 600;
    private static final int PLAYER_Y = 520;
    private static final int TIMER_DELAY_MS = 16;
    private static final int MAX_STAGE = 3;
    private static final int HUD_LINE_Y = 64;
    private static final String ASSET_DIR = "assets";

    private final Timer timer;
    private final Font titleFont;
    private final Font bodyFont;
    private final Font hudFont;
    private final List<Bullet> playerBullets;
    private final List<Bullet> enemyBullets;
    private final List<Enemy> enemies;
    private final BufferedImage backgroundSprite;
    private final BufferedImage playerSprite;
    private final BufferedImage enemySprite;
    private final BufferedImage playerBulletSprite;
    private final BufferedImage enemyBulletSprite;

    private Player player;
    private GameState gameState;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean spacePressed;
    private int score;
    private int stage;
    private int playerShotCooldown;
    private int enemyShotCooldown;
    private int enemyDirection;

    public GamePanel() {
        setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
        setBackground(new Color(6, 10, 24));
        setFocusable(true);

        titleFont = new Font("SansSerif", Font.BOLD, 34);
        bodyFont = new Font("SansSerif", Font.PLAIN, 18);
        hudFont = new Font("SansSerif", Font.BOLD, 16);
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        enemies = new ArrayList<>();
        backgroundSprite = loadSprite("space_background.png");
        playerSprite = loadSprite("player_ship.png");
        enemySprite = loadSprite("enemy_bug.png");
        playerBulletSprite = loadSprite("player_laser.png");
        enemyBulletSprite = loadSprite("enemy_laser.png");

        addKeyListener(new KeyHandler());
        startNewGame();
        gameState = GameState.START;

        timer = new Timer(TIMER_DELAY_MS, this);
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void startNewGame() {
        player = new Player(BASE_WIDTH / 2 - 21, PLAYER_Y);
        score = 0;
        stage = 1;
        resetStage();
    }

    private void resetStage() {
        player.resetPosition(BASE_WIDTH, PLAYER_Y);
        playerBullets.clear();
        enemyBullets.clear();
        enemies.clear();
        enemyDirection = 1;
        playerShotCooldown = 0;
        enemyShotCooldown = 25;
        createEnemyFormation();
    }

    private void createEnemyFormation() {
        int rows = 3 + Math.min(stage, 2);
        int cols = 6;
        int startX = 120;
        int startY = 70;
        int gapX = 88;
        int gapY = 54;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int enemyX = startX + col * gapX;
                int enemyY = startY + row * gapY;
                enemies.add(new Enemy(enemyX, enemyY, row));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) {
            updateGame();
        }
        repaint();
    }

    private void updateGame() {
        handlePlayerMovement();
        handlePlayerShooting();
        updateBullets();
        updateEnemies();
        handleEnemyShooting();
        handleCollisions();
        checkStageProgress();
    }

    private void handlePlayerMovement() {
        if (leftPressed) {
            player.moveLeft();
        }
        if (rightPressed) {
            player.moveRight();
        }
        player.clampToWidth(BASE_WIDTH);
    }

    private void handlePlayerShooting() {
        if (playerShotCooldown > 0) {
            playerShotCooldown--;
        }

        if (spacePressed && playerShotCooldown == 0) {
            firePlayerBullet();
        }
    }

    private void firePlayerBullet() {
        int bulletX = player.getX() + player.getWidth() / 2 - 2;
        int bulletY = player.getY() - 12;
        playerBullets.add(new Bullet(bulletX, bulletY, 4, 12, 10, true));
        playerShotCooldown = 10;
    }

    private void updateBullets() {
        updateBulletList(playerBullets);
        updateBulletList(enemyBullets);
    }

    private void updateBulletList(List<Bullet> bullets) {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update();
            if (bullet.isOutOfBounds(BASE_HEIGHT)) {
                iterator.remove();
            }
        }
    }

    private void updateEnemies() {
        if (enemies.isEmpty()) {
            return;
        }

        int horizontalSpeed = 1 + stage;
        boolean shouldDrop = false;

        for (Enemy enemy : enemies) {
            enemy.move(horizontalSpeed * enemyDirection, 0);
            if (enemy.getX() <= 16 || enemy.getX() + enemy.getWidth() >= BASE_WIDTH - 16) {
                shouldDrop = true;
            }
        }

        if (shouldDrop) {
            enemyDirection *= -1;
            for (Enemy enemy : enemies) {
                enemy.move(0, 18);
                if (enemy.getY() + enemy.getHeight() >= PLAYER_Y - 20) {
                    gameState = GameState.GAME_OVER;
                }
            }
        }
    }

    private void handleEnemyShooting() {
        enemyBullets.clear();
    }

    private void handleCollisions() {
        handlePlayerBulletCollisions();
        handleEnemyBulletCollisions();
    }

    private void handlePlayerBulletCollisions() {
        Iterator<Bullet> bulletIterator = playerBullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            Enemy hitEnemy = null;

            for (Enemy enemy : enemies) {
                if (bullet.getBounds().intersects(enemy.getBounds())) {
                    hitEnemy = enemy;
                    break;
                }
            }

            if (hitEnemy != null) {
                bulletIterator.remove();
                enemies.remove(hitEnemy);
                score += 100 + hitEnemy.getRow() * 25;
            }
        }
    }

    private void handleEnemyBulletCollisions() {
        Iterator<Bullet> iterator = enemyBullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            if (bullet.getBounds().intersects(player.getBounds())) {
                iterator.remove();
                player.loseLife();
                player.resetPosition(BASE_WIDTH, PLAYER_Y);

                if (player.getLives() <= 0) {
                    gameState = GameState.GAME_OVER;
                }
                break;
            }
        }
    }

    private void checkStageProgress() {
        if (!enemies.isEmpty()) {
            return;
        }

        if (stage >= MAX_STAGE) {
            gameState = GameState.CLEAR;
            return;
        }

        stage++;
        resetStage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.scale(getScaleX(), getScaleY());

        drawBackground(g2);

        if (gameState == GameState.START) {
            drawStartScreen(g2);
        } else {
            drawGame(g2);

            if (gameState == GameState.GAME_OVER) {
                drawOverlay(g2, "GAME OVER", "Press Enter to restart");
            } else if (gameState == GameState.CLEAR) {
                drawOverlay(g2, "STAGE CLEAR", "You finished all waves. Press Enter to restart");
            }
        }

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2) {
        if (backgroundSprite != null) {
            g2.drawImage(backgroundSprite.getScaledInstance(BASE_WIDTH, BASE_HEIGHT, Image.SCALE_SMOOTH), 0, 0, null);
            return;
        }

        g2.setColor(new Color(10, 18, 40));
        g2.fillRect(0, 0, BASE_WIDTH, BASE_HEIGHT);
    }

    private void drawGame(Graphics2D g2) {
        drawHud(g2);
        drawPlayer(g2);
        drawEnemies(g2);
        drawBullets(g2, playerBullets, new Color(255, 246, 115));
        drawBullets(g2, enemyBullets, new Color(255, 110, 110));
    }

    private void drawHud(Graphics2D g2) {
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score, 20, 28);
        g2.drawString("Lives: " + player.getLives(), 20, 52);
        g2.drawString("Stage: " + stage, BASE_WIDTH - 100, 28);
        g2.setColor(new Color(120, 180, 255));
        g2.drawLine(0, HUD_LINE_Y, BASE_WIDTH, HUD_LINE_Y);
    }

    private void drawPlayer(Graphics2D g2) {
        drawSprite(g2, playerSprite, player.getX() - 4, player.getY() - 8, player.getWidth() + 8, player.getHeight() + 16);
    }

    private void drawEnemies(Graphics2D g2) {
        for (Enemy enemy : enemies) {
            drawSprite(g2, enemySprite, enemy.getX() - 5, enemy.getY() - 4, enemy.getWidth() + 10, enemy.getHeight() + 12);
        }
    }

    private void drawBullets(Graphics2D g2, List<Bullet> bullets, Color color) {
        for (Bullet bullet : bullets) {
            BufferedImage sprite = bullet.isFromPlayer() ? playerBulletSprite : enemyBulletSprite;
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
        int x = (BASE_WIDTH - textWidth) / 2;
        g2.drawString(text, x, y);
    }

    private double getScaleX() {
        return getWidth() / (double) BASE_WIDTH;
    }

    private double getScaleY() {
        return getHeight() / (double) BASE_HEIGHT;
    }

    private BufferedImage loadSprite(String fileName) {
        File file = new File(ASSET_DIR, fileName);
        if (!file.exists()) {
            return null;
        }

        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            return null;
        }
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

    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_LEFT) {
                leftPressed = true;
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                rightPressed = true;
            } else if (keyCode == KeyEvent.VK_SPACE) {
                spacePressed = true;
                if (gameState == GameState.PLAYING && playerShotCooldown == 0) {
                    firePlayerBullet();
                }
            } else if (keyCode == KeyEvent.VK_ENTER) {
                if (gameState == GameState.START || gameState == GameState.GAME_OVER || gameState == GameState.CLEAR) {
                    startNewGame();
                    gameState = GameState.PLAYING;
                    requestFocusInWindow();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (keyCode == KeyEvent.VK_LEFT) {
                leftPressed = false;
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                rightPressed = false;
            } else if (keyCode == KeyEvent.VK_SPACE) {
                spacePressed = false;
            }
        }
    }
}
