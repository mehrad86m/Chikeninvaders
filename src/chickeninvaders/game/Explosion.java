package chickeninvaders.game;

import java.awt.*;

//effect terekidan
public class Explosion {
    private double x, y;
    private int life = 0;
    private static final int MAX_LIFE = 20; // frames
    private final int maxRadius;

    public Explosion(double x, double y) {
        this(x, y, 30);
    }

    public Explosion(double x, double y, int maxRadius) {
        this.x = x;
        this.y = y;
        this.maxRadius = maxRadius;
    }

    public void update() {
        life++;
    }

    public boolean isFinished() {
        return life >= MAX_LIFE;
    }

    public void draw(Graphics2D g) {
        float progress = life / (float) MAX_LIFE;
        int radius = (int) (maxRadius * progress);
        float alpha = Math.max(0f, 1f - progress);
        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(Color.ORANGE);
        g.fillOval((int) (x - radius / 2.0), (int) (y - radius / 2.0), radius, radius);
        g.setColor(Color.RED);
        g.drawOval((int) (x - radius / 2.0), (int) (y - radius / 2.0), radius, radius);
        g.setComposite(old);
    }
}
