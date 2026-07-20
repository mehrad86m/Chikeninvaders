package chickeninvaders.enemy;

import chickeninvaders.game.Egg;

import java.awt.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.File;

public class BossLevel8 extends Boss {

    private double vSpeed = 0;
    private final double baseY;
    private double vAccel = 0.02;
    private int vDirection = 1;
    private long lastDirectionChange = 0;
    private static Image bossImage;
    static {
        try {
            bossImage = ImageIO.read(new File("resources/images/boss2.png"));
        } catch (Exception e) {
            bossImage = null;
        }
    }

    public BossLevel8(int panelWidth) {
        super(panelWidth / 2.0 - 80, 30, 160, 120, 100, 2.0, 1000, 5.0);
        this.baseY = 30;
    }

    @Override
    public void update(int panelWidth, int panelHeight) {
        x += hSpeed * hDirection;
        if (x <= 0 || x + width >= panelWidth) hDirection *= -1;

        long now = System.currentTimeMillis();
        if (now - lastDirectionChange > 4000 && Math.random() < 0.02) {
            hDirection *= -1;
            lastDirectionChange = now;
        }

        vSpeed += vAccel * vDirection;
        if (vSpeed > 1.5 || vSpeed < -1.5) vDirection *= -1;
        y += vSpeed;
        if (y < baseY - 50) y = baseY - 50;
        if (y > baseY + 50) y = baseY + 50;
    }

    @Override
    public void tryAttack(List<Egg> eggsOut) {
        long now = System.currentTimeMillis();
        if (now - lastAttackTime < attackIntervalMs) return;
        lastAttackTime = now;
        double cx = getCenterX();
        double cy = getCenterY();
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(45 * i);
            eggsOut.add(new Egg(cx, cy, Math.cos(angle) * eggSpeed, Math.sin(angle) * eggSpeed));
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (bossImage != null) {
            int imgWidth = (int) (width * 2.0 / 3.0);
            int drawX = (int) (x + (width - imgWidth) / 2.0);
            g.drawImage(bossImage, drawX, (int) y, imgWidth, height, null);
            return;
        }
        g.setColor(new Color(90, 20, 120));
        g.setColor(new Color(90, 20, 120));
        g.fillOval((int) x, (int) y, width, height);
        g.setColor(Color.BLACK);
        g.drawOval((int) x, (int) y, width, height);
        g.setColor(Color.RED);
        g.fillOval((int) (x + width * 0.25), (int) (y + height * 0.3), 18, 18);
        g.fillOval((int) (x + width * 0.6), (int) (y + height * 0.3), 18, 18);
        g.setColor(Color.YELLOW);
        g.drawOval((int) (x + width * 0.25), (int) (y + height * 0.3), 18, 18);
        g.drawOval((int) (x + width * 0.6), (int) (y + height * 0.3), 18, 18);
    }
}
