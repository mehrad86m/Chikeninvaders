package chickeninvaders.enemy;

import chickeninvaders.game.Cell;

import java.awt.*;

public class ZigzagEnemy extends Enemy {

    private final double phase;

    public ZigzagEnemy(Cell cell, double startX, double startY, boolean entering) {
        super(cell, startX, startY, entering);
        this.phase = Math.random() * Math.PI * 2;
    }

    @Override
    public void update(double targetX, double targetY, double time) {
        if (isFrozen()) return;
        double zigzagOffset = Math.sin(time * 3 + phase) * 15;
        if (entering) {
            updateEntering(targetX + zigzagOffset, targetY);
        } else {
            x = targetX + zigzagOffset;
            y = targetY;
        }
    }

    @Override
    public Color getColor() {
        return new Color(150, 255, 120);
    }
}
