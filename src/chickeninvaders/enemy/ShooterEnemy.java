package chickeninvaders.enemy;

import chickeninvaders.game.Cell;

import java.awt.*;

public class ShooterEnemy extends Enemy {

    private long nextShotTime;

    public ShooterEnemy(Cell cell, double startX, double startY, boolean entering) {
        super(cell, startX, startY, entering);
        scheduleNextShot();
    }

    private void scheduleNextShot() {
        nextShotTime = System.currentTimeMillis() + 2000 + (long) (Math.random() * 3000);
    }

    @Override
    public void update(double targetX, double targetY, double time) {
        if (isFrozen()) return;
        if (entering) {
            updateEntering(targetX, targetY);
        } else {
            x = targetX;
            y = targetY;
        }
    }

    public boolean shouldFireNow() {
        if (entering || isFrozen()) return false;
        if (System.currentTimeMillis() >= nextShotTime) {
            scheduleNextShot();
            return true;
        }
        return false;
    }

    @Override
    public Color getColor() {
        return new Color(255, 90, 200);
    }
}
