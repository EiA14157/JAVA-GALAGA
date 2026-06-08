package galaga;

public class Bullet extends Sprite {
    private final int speed;
    private final boolean fromPlayer;

    public Bullet(int x, int y, int width, int height, int speed, boolean fromPlayer) {
        super(x, y, width, height);
        this.speed = speed;
        this.fromPlayer = fromPlayer;
    }

    public void update() {
        // Player bullets move up, enemy bullets move down.
        if (fromPlayer) {
            y -= speed;
        } else {
            y += speed;
        }
    }

    public boolean isFromPlayer() {
        return fromPlayer;
    }

    public boolean isOutOfBounds(int panelHeight) {
        return y + height < 0 || y > panelHeight;
    }
}
