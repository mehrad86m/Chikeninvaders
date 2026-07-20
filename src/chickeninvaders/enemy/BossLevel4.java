package chickeninvaders.enemy;

import chickeninvaders.game.Egg;

import java.awt.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.File;

public class BossLevel4 extends Boss {

    private double vSpeed = 0.5;
    private int vDirection = 1;
    private final double baseY;
    private static Image bossImage;
    static {
        try {
            bossImage = ImageIO.read(new File("resources/images/boss1.png"));
        } catch (Exception e) {
            bossImage = null;
        }
    }

    public BossLevel4(int panelWidth) {
        super(panelWidth / 2.0 - 60, 40, 120, 90, 50, 1.5, 1500, 4.0);
        this.baseY = 40;
    }

    @Override
    public void update(int panelWidth, int panelHeight) {
        x += hSpeed * hDirection;
        if (x <= 0 || x + width >= panelWidth) hDirection *= -1;

        y += vSpeed * vDirection;
        if (y < baseY - 20 || y > baseY + 20) vDirection *= -1;
    }

    @Override
    public void tryAttack(List<Egg> eggsOut) {
        long now = System.currentTimeMillis();
        if (now - lastAttackTime < attackIntervalMs) return;
        lastAttackTime = now;
        double cx = getCenterX();
        double cy = getCenterY();
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] d : dirs) {
            eggsOut.add(new Egg(cx, cy, d[0] * eggSpeed, d[1] * eggSpeed));
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
        g.setColor(new Color(200, 60, 60));
        g.setColor(new Color(200, 60, 60));
        g.fillOval((int) x, (int) y, width, height);
        g.setColor(Color.BLACK);
        g.drawOval((int) x, (int) y, width, height);
        g.setColor(Color.YELLOW);
        g.fillOval((int) (x + width * 0.25), (int) (y + height * 0.3), 14, 14);
        g.fillOval((int) (x + width * 0.65), (int) (y + height * 0.3), 14, 14);
    }
}
