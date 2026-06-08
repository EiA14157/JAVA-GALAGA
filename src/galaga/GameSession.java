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

        // 한 프레임의 게임 로직을 이해하기 쉬운 순서로 갱신한다.
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
        // 새 게임이 시작되면 점수와 스테이지를 처음 상태로 되돌린다.
        player = new Player(GameConfig.BASE_WIDTH / 2 - 21, GameConfig.PLAYER_Y);
        score = 0;
        stage = 1;
        resetStage();
    }

    private void resetStage() {
        // 현재 웨이브를 다시 만들기 전에 일시적인 상태를 모두 비운다.
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
            // 화면 밖으로 나간 탄환은 충돌 판정과 렌더링 대상에서 제외한다.
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
            // 적 무리가 좌우 벽에 닿으면 방향을 바꾸고 한 칸 아래로 내려오게 한다.
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
        // 아직 적 발사 패턴이 구현되지 않아 목록을 비운 상태로 유지한다.
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
                // 아래쪽 적일수록 조금 더 높은 점수를 줘서 위험한 샷에 보상을 준다.
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

        // 마지막 웨이브를 끝내면 클리어, 아니면 다음 스테이지를 구성한다.
        if (stage >= GameConfig.MAX_STAGE) {
            gameState = GameState.CLEAR;
            return;
        }

        stage++;
        resetStage();
    }
}
