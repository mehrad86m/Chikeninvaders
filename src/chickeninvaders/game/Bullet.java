package chickeninvaders.game;

import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;

public class Bullet {
    public double x, y;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 26;
    public static final double SPEED = 8;
    private boolean alive = true;
    private static Image bulletImage;
    static {
        try {
            bulletImage = ImageIO.read(new File("resources/images/shot.png"));
        } catch (Exception e) {
            bulletImage = null;
        }
    }

    public Bullet(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y -= SPEED;
        if (y < -HEIGHT) alive = false;
    }

    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, WIDTH, HEIGHT);
    }

    public void draw(Graphics2D g) {
        if (bulletImage != null) {
            g.drawImage(bulletImage, (int) x, (int) y, WIDTH, HEIGHT, null);
        } else {
            g.setColor(Color.red);
            g.fillRect((int) x, (int) y, WIDTH, HEIGHT);
        }
    }
}
