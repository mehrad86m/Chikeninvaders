package chickeninvaders.game;

import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PowerUp {
    public double x, y;
    public final int type;
    public static final int SIZE = 30;
    public static final double SPEED = 2.5;
    private boolean alive = true;

    private static final Map<Integer, Image> IMAGE_CACHE = new HashMap<>();

    public PowerUp(double x, double y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void update(int panelHeight) {
        y += SPEED;
        if (y > panelHeight) alive = false;
    }

    public boolean isAlive() { return alive; }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, SIZE, SIZE);
    }

    private static Image loadImage(int type) {
        return IMAGE_CACHE.computeIfAbsent(type, t -> {
            try {
                String filename;
                switch (t) {
                    case 0: filename = "rapid_fire.png"; break;
                    case 1: filename = "freeze_bomb.png"; break;
                    case 2: filename = "extra_life.png"; break;
                    case 3: filename = "sheild.png"; break;
                    case 4: filename = "add_fire.png"; break;
                    default: filename = "rapid_fire.png"; break;
                }
                return ImageIO.read(new File("resources/images/" + filename));
            } catch (Exception e) {
                System.err.println("error :" + t);
                return null;
            }
        });
    }

    public void draw(Graphics2D g) {
        Image img = loadImage(type);

        if (img != null) {
            g.drawImage(img, (int) x, (int) y, SIZE, SIZE, null);
        } else {
            switch (type) {
                case 0: g.setColor(Color.RED); break;
                case 1: g.setColor(Color.CYAN); break;
                case 2: g.setColor(Color.GREEN); break;
                case 3: g.setColor(Color.BLUE); break;
                case 4: g.setColor(Color.MAGENTA); break;
                default: g.setColor(Color.YELLOW);
            }
            g.fillOval((int) x, (int) y, SIZE, SIZE);
            g.setColor(Color.WHITE);
            g.drawOval((int) x, (int) y, SIZE, SIZE);
        }
    }
}