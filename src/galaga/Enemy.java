package galaga;

public class Enemy extends Sprite {
    private final int row;

    public Enemy(int x, int y, int row) {
        super(x, y, 34, 24);
        this.row = row;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public int getRow() {
        return row;
    }
}
