package galaga;

import java.awt.Rectangle;

public abstract class Sprite {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected Sprite(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() {
        // 게임의 모든 충돌 판정은 이 공통 사각형 히트박스를 사용한다.
        return new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
