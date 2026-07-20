package chickeninvaders.game;

import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;

public class Plane {
    private Image planeImage;

    public double x, y;
    public static final int WIDTH = 40;
    public static final int HEIGHT = 40;

    private int lives;
    private static final int MAX_LIVES = 5;

    private double speed;
    private long fireRateMs;
    private long lastShotTime = 0;
    private boolean sniperDoubleBossDamage = false;

    private int simultaneousBullets = 1;
    private long rapidFireEndTime = 0;
    private long shieldEndTime = 0;

    public Plane(int panelWidth, int panelHeight, String planeName) {
        this.x = panelWidth / 2.0 - WIDTH / 2.0;
        this.y = panelHeight - HEIGHT - 20;
        if ("Advanced".equals(planeName)) {
            this.speed = 8;
            this.fireRateMs = 200;
            this.lives = 4;
            this.sniperDoubleBossDamage = false;
        } else if ("Sniper".equals(planeName)) {
            this.speed = 5;
            this.fireRateMs = 400;
            this.lives = 3;
            this.sniperDoubleBossDamage = true;
        } else { // Default Plane
            this.speed = 6;
            this.fireRateMs = 300;
            this.lives = 3;
            this.sniperDoubleBossDamage = false;
        }

        try {
            planeImage = ImageIO.read(new File("resources/images/4.png"));
        } catch (Exception e) {
            planeImage = null;
        }
    }

    public void moveLeft() { x = Math.max(0, x - speed); }
    public void moveRight(int panelWidth) { x = Math.min(panelWidth - WIDTH, x + speed); }
    public void moveUp() { y = Math.max(0, y - speed); }
    public void moveDown(int panelHeight) { y = Math.min(panelHeight - HEIGHT, y + speed); }

    public boolean canShoot() {
        long now = System.currentTimeMillis();
        long effectiveRate = isRapidFireActive() ? fireRateMs / 3 : fireRateMs;
        return now - lastShotTime >= effectiveRate;
    }

    public void registerShot() {
        lastShotTime = System.currentTimeMillis();
    }

    public int getSimultaneousBullets() { return simultaneousBullets; }

    public static final int MAX_BULLETS = 5;
    public void increaseAddFire() {
        if (simultaneousBullets < MAX_BULLETS) simultaneousBullets++;
    }

    public boolean isRapidFireActive() { return System.currentTimeMillis() < rapidFireEndTime; }
    public void activateRapidFire(long durationMs) { rapidFireEndTime = System.currentTimeMillis() + durationMs; }

    public boolean isShieldActive() { return System.currentTimeMillis() < shieldEndTime; }
    public void activateShield(long durationMs) { shieldEndTime = System.currentTimeMillis() + durationMs; }

    public boolean hasBossDamageBonus() { return sniperDoubleBossDamage; }

    public int getLives() { return lives; }
    public void loseLife() { lives--; }
    public void gainLife() { lives = Math.min(MAX_LIVES, lives + 1); }
    public boolean isDead() { return lives <= 0; }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, WIDTH, HEIGHT);
    }

    public void draw(Graphics2D g) {
        if (planeImage != null) {
            g.drawImage(planeImage, (int) x, (int) y, WIDTH, HEIGHT, null);
        } else {
            g.setColor(new Color(80, 180, 250));
            int cx = (int) x + WIDTH / 2;
            int[] xs = {cx, (int) x, (int) x + WIDTH};
            int[] ys = {(int) y, (int) y + HEIGHT, (int) y + HEIGHT};
            g.fillPolygon(xs, ys, 3);
        }

        if (isShieldActive()) {
            g.setColor(new Color(100, 200, 255, 120));
            g.fillOval((int) x - 8, (int) y - 8, WIDTH + 16, HEIGHT + 16);
        }
    }
}
