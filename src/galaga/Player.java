package galaga;

public class Player extends Sprite {
    private final int moveSpeed;
    private int lives;

    public Player(int x, int y) {
        super(x, y, 42, 22);
        this.moveSpeed = 6;
        this.lives = 3;
    }

    public void moveLeft() {
        x -= moveSpeed;
    }

    public void moveRight() {
        x += moveSpeed;
    }

    public void clampToWidth(int panelWidth) {
        if (x < 0) {
            x = 0;
        }
        // 우주선이 화면 양쪽 경계를 벗어나지 않도록 위치를 고정한다.
        int maxX = panelWidth - width;
        if (x > maxX) {
            x = maxX;
        }
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }

    public void resetPosition(int panelWidth, int yPosition) {
        // 피격되거나 스테이지가 바뀌면 기본 시작 위치로 되돌린다.
        x = panelWidth / 2 - width / 2;
        y = yPosition;
    }
}
