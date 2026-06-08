package galaga;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final int PLAYER_Y = 520;
    private static final int TIMER_DELAY_MS = 16;
    private static final int MAX_STAGE = 3;

    private final Timer timer;
    private final Random random;
    private final Font titleFont;
    private final Font bodyFont;
    private final Font hudFont;
    private final List<Bullet> playerBullets;
    private final List<Bullet> enemyBullets;
    private final List<Enemy> enemies;

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
    private int starOffset;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(6, 10, 24));
        setFocusable(true);

        random = new Random();
        titleFont = new Font("SansSerif", Font.BOLD, 34);
        bodyFont = new Font("SansSerif", Font.PLAIN, 18);
        hudFont = new Font("SansSerif", Font.BOLD, 16);
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        enemies = new ArrayList<>();

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
        player = new Player(PANEL_WIDTH / 2 - 21, PLAYER_Y);
        score = 0;
        stage = 1;
        resetStage();
    }

    private void resetStage() {
        player.resetPosition(PANEL_WIDTH, PLAYER_Y);
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
        starOffset = (starOffset + 2) % PANEL_HEIGHT;
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
        player.clampToWidth(PANEL_WIDTH);
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
            if (bullet.isOutOfBounds(PANEL_HEIGHT)) {
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
            if (enemy.getX() <= 16 || enemy.getX() + enemy.getWidth() >= PANEL_WIDTH - 16) {
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
                player.resetPosition(PANEL_WIDTH, PLAYER_Y);

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
        g2.setColor(new Color(10, 18, 40));
        g2.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g2.setColor(new Color(100, 140, 255, 150));
        for (int i = 0; i < 80; i++) {
            int x = (i * 97) % PANEL_WIDTH;
            int y = ((i * 57) + starOffset) % PANEL_HEIGHT;
            int size = i % 3 == 0 ? 3 : 2;
            g2.fillOval(x, y, size, size);
        }
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
        g2.drawString("Stage: " + stage, PANEL_WIDTH - 100, 28);
        g2.setColor(new Color(120, 180, 255));
        g2.drawLine(0, 64, PANEL_WIDTH, 64);
    }

    private void drawPlayer(Graphics2D g2) {
        int[] bodyX = {
            player.getX(),
            player.getX() + player.getWidth() / 2,
            player.getX() + player.getWidth()
        };
        int[] bodyY = {
            player.getY() + player.getHeight(),
            player.getY(),
            player.getY() + player.getHeight()
        };

        g2.setColor(new Color(90, 255, 205));
        g2.fillPolygon(bodyX, bodyY, 3);
        g2.setColor(new Color(30, 120, 255));
        g2.fillRect(player.getX() + 10, player.getY() + 10, 22, 10);
    }

    private void drawEnemies(Graphics2D g2) {
        for (Enemy enemy : enemies) {
            int x = enemy.getX();
            int y = enemy.getY();

            g2.setColor(enemy.getRow() % 2 == 0 ? new Color(255, 120, 120) : new Color(255, 186, 80));
            g2.fillOval(x, y, enemy.getWidth(), enemy.getHeight());
            g2.setColor(new Color(255, 245, 245));
            g2.fillRect(x + 6, y + 7, 7, 4);
            g2.fillRect(x + 21, y + 7, 7, 4);
            g2.setColor(new Color(120, 30, 30));
            g2.drawArc(x + 9, y + 10, 16, 8, 180, 180);
        }
    }

    private void drawBullets(Graphics2D g2, List<Bullet> bullets, Color color) {
        g2.setColor(color);
        for (Bullet bullet : bullets) {
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
        int x = (PANEL_WIDTH - textWidth) / 2;
        g2.drawString(text, x, y);
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
