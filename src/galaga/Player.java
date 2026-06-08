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
        // Clamp both sides so the ship never leaves the visible play field.
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
        // Restore the ship to the default spawn point after hits or stage resets.
        x = panelWidth / 2 - width / 2;
        y = yPosition;
    }
}
