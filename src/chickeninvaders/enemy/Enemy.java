package chickeninvaders.enemy;

import chickeninvaders.game.Cell;

import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class Enemy {

    public static final int SIZE = 30;
    private static final Map<Integer, Image> IMAGE_CACHE = new HashMap<>();

    private static Image loadImage(int type) {
        return IMAGE_CACHE.computeIfAbsent(type, t -> {
            try {
                String filename;
                switch (t) {
                    case 1: filename = "fast"; break;
                    case 2: filename = "zigzag"; break;
                    case 3: filename = "shooter"; break;
                    case 0:
                    default: filename = "normal"; break;
                }

                return ImageIO.read(new File("resources/images/" + filename + ".png"));
            } catch (Exception e) {
                return null;
            }
        });
    }

    public double x, y;
    protected final Cell cell;
    protected boolean alive = true;
    protected boolean entering;      // true while flying in from a screen corner to its slot
    protected double enterSpeed = 4.0;
    private boolean frozen = false;

    public Enemy(Cell cell, double startX, double startY, boolean entering) {
        this.cell = cell;
        this.x = startX;
        this.y = startY;
        this.entering = entering;
    }

    public Cell getCell() { return cell; }
    public int getType() {
        return this.getCell().getType();
    }

    public boolean isEntering() { return entering; }
    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }

    public void setFrozen(boolean frozen) { this.frozen = frozen; }
    public boolean isFrozen() { return frozen; }

    protected void updateEntering(double targetX, double targetY) {
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.hypot(dx, dy);
        if (dist < enterSpeed) {
            x = targetX;
            y = targetY;
            entering = false;
        } else {
            x += enterSpeed * dx / dist;
            y += enterSpeed * dy / dist;
        }
    }

    public abstract void update(double targetX, double targetY, double time);

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, SIZE, SIZE);
    }

    public abstract Color getColor();

    public void draw(Graphics2D g) {
        Image img = loadImage(getType());
        if (img != null) {
            g.drawImage(img, (int) x, (int) y, SIZE, SIZE , null);
        } else {
            g.setColor(frozen ? Color.CYAN.darker() : getColor());
            g.fillOval((int) x, (int) y, SIZE, SIZE);
            g.setColor(Color.BLACK);
            g.drawOval((int) x, (int) y, SIZE, SIZE);
        }
        // simple beak to indicate the chicken faces downward
        g.fillPolygon(new int[]{(int) x + SIZE / 2 - 4, (int) x + SIZE / 2 + 4, (int) x + SIZE / 2},
                new int[]{(int) y + SIZE - 2, (int) y + SIZE - 2, (int) y + SIZE + 6}, 3);
    }
}
