package chickeninvaders.game;

import java.awt.*;

public class Egg {
    public double x, y;
    public double dx, dy;
    public static final int SIZE = 10;
    private boolean alive = true;
    private boolean frozen = false;

    public Egg(double x, double y, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public void update(int panelWidth, int panelHeight) {
        if (frozen) return;
        x += dx;
        y += dy;
        if (y > panelHeight + SIZE || y < -SIZE || x < -SIZE || x > panelWidth + SIZE) {
            alive = false;
        }
    }

    public void setFrozen(boolean frozen) { this.frozen = frozen; }

    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, SIZE, SIZE);
    }

    public void draw(Graphics2D g) {
        g.setColor(frozen ? Color.CYAN.darker() : new Color(230, 220, 150));
        g.fillOval((int) x, (int) y, SIZE, SIZE);
        g.setColor(Color.BLACK);
        g.drawOval((int) x, (int) y, SIZE, SIZE);
    }
}
