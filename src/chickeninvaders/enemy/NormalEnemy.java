package chickeninvaders.enemy;

import chickeninvaders.game.Cell;

import java.awt.*;

public class NormalEnemy extends Enemy {

    public NormalEnemy(Cell cell, double startX, double startY, boolean entering) {
        super(cell, startX, startY, entering);
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

    @Override
    public Color getColor() {
        return new Color(255, 220, 80);
    }
}
