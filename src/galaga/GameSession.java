package galaga;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameSession {
    private final List<Bullet> playerBullets;
    private final List<Bullet> enemyBullets;
    private final List<Enemy> enemies;
    private final EnemyFormationFactory formationFactory;

    private Player player;
    private GameState gameState;
    private int score;
    private int stage;
    private int playerShotCooldown;
    private int enemyDirection;

    public GameSession() {
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        enemies = new ArrayList<>();
        formationFactory = new EnemyFormationFactory();
        startNewGame();
        gameState = GameState.START;
    }

    public void update(InputState inputState) {
        if (gameState != GameState.PLAYING) {
            return;
        }

        // Update one frame of gameplay in a stable, easy-to-follow order.
        handlePlayerMovement(inputState);
        handlePlayerShooting(inputState);
        updateBullets();
        updateEnemies();
        handleEnemyShooting();
        handleCollisions();
        checkStageProgress();
    }

    public void startPlaying() {
        startNewGame();
        gameState = GameState.PLAYING;
    }

    public void firePlayerBulletIfPossible() {
        if (gameState == GameState.PLAYING && playerShotCooldown == 0) {
            firePlayerBullet();
        }
    }

    public boolean canRestart() {
        return gameState == GameState.START || gameState == GameState.GAME_OVER || gameState == GameState.CLEAR;
    }

    public Player getPlayer() {
        return player;
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<Bullet> getPlayerBullets() {
        return playerBullets;
    }

    public List<Bullet> getEnemyBullets() {
        return enemyBullets;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public int getScore() {
        return score;
    }

    public int getStage() {
        return stage;
    }

    private void startNewGame() {
        // Reset score and stage whenever a completely new run begins.
        player = new Player(GameConfig.BASE_WIDTH / 2 - 21, GameConfig.PLAYER_Y);
        score = 0;
        stage = 1;
        resetStage();
    }

    private void resetStage() {
        // Clear transient state before rebuilding the current wave.
        player.resetPosition(GameConfig.BASE_WIDTH, GameConfig.PLAYER_Y);
        playerBullets.clear();
        enemyBullets.clear();
        enemies.clear();
        enemyDirection = 1;
        playerShotCooldown = 0;
        enemies.addAll(formationFactory.createFormation(stage));
    }

    private void handlePlayerMovement(InputState inputState) {
        if (inputState.isLeftPressed()) {
            player.moveLeft();
        }
        if (inputState.isRightPressed()) {
            player.moveRight();
        }
        player.clampToWidth(GameConfig.BASE_WIDTH);
    }

    private void handlePlayerShooting(InputState inputState) {
        if (playerShotCooldown > 0) {
            playerShotCooldown--;
        }

        if (inputState.isSpacePressed() && playerShotCooldown == 0) {
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
            // Bullets outside the screen no longer matter for collisions or drawing.
            if (bullet.isOutOfBounds(GameConfig.BASE_HEIGHT)) {
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
            // Touching a side wall flips the swarm and makes it drop on the next pass.
            if (enemy.getX() <= 16 || enemy.getX() + enemy.getWidth() >= GameConfig.BASE_WIDTH - 16) {
                shouldDrop = true;
            }
        }

        if (shouldDrop) {
            enemyDirection *= -1;
            for (Enemy enemy : enemies) {
                enemy.move(0, 18);
                if (enemy.getY() + enemy.getHeight() >= GameConfig.PLAYER_Y - 20) {
                    gameState = GameState.GAME_OVER;
                }
            }
        }
    }

    private void handleEnemyShooting() {
        // Enemy firing is not implemented yet, so the list is kept empty for now.
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
                // Lower rows are worth a bit more to reward riskier shots.
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
                player.resetPosition(GameConfig.BASE_WIDTH, GameConfig.PLAYER_Y);

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

        // Finishing the last wave clears the game, otherwise build the next stage.
        if (stage >= GameConfig.MAX_STAGE) {
            gameState = GameState.CLEAR;
            return;
        }

        stage++;
        resetStage();
    }
}
