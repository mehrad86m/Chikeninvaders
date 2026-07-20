package chickeninvaders.game;

import java.util.HashMap;
import java.util.Map;

public class LevelConfig {

    public static final int ENEMY_NORMAL = 0;
    public static final int ENEMY_FAST = 1;
    public static final int ENEMY_ZIGZAG = 2;
    public static final int ENEMY_SHOOTER = 3;

    public final int level;
    public final boolean isBoss;
    public final int[] allowedTypes;
    public final double gridSpeed;
    public final int verticalStep;
    public final double eggIntervalSec;
    private final Map<Integer, Integer> counters;

    private LevelConfig(int level, boolean isBoss, int[] allowedTypes,
                        double gridSpeed, int verticalStep, double eggIntervalSec,
                        Map<Integer, Integer> counters) {
        this.level = level;
        this.isBoss = isBoss;
        this.allowedTypes = allowedTypes;
        this.gridSpeed = gridSpeed;
        this.verticalStep = verticalStep;
        this.eggIntervalSec = eggIntervalSec;
        this.counters = counters;
    }

    public int counterFor(int type) {
        return counters.getOrDefault(type, 2);
    }

    private static Map<Integer, Integer> group1to3() {
        Map<Integer, Integer> m = new HashMap<>();
        m.put(ENEMY_NORMAL, 2);
        m.put(ENEMY_FAST, 1);
        m.put(ENEMY_ZIGZAG, 2);
        m.put(ENEMY_SHOOTER, 2);
        return m;
    }

    private static Map<Integer, Integer> group5to7() {
        Map<Integer, Integer> m = new HashMap<>();
        m.put(ENEMY_NORMAL, 3);
        m.put(ENEMY_FAST, 2);
        m.put(ENEMY_ZIGZAG, 3);
        m.put(ENEMY_SHOOTER, 3);
        return m;
    }

    public static LevelConfig forLevel(int level) {
        switch (level) {
            case 1: return new LevelConfig(1, false, new int[]{ENEMY_NORMAL}, 1.0, 20, 3.0, group1to3());
            case 2: return new LevelConfig(2, false, new int[]{ENEMY_NORMAL, ENEMY_FAST}, 1.5, 20, 2.0, group1to3());
            case 3: return new LevelConfig(3, false, new int[]{ENEMY_NORMAL, ENEMY_ZIGZAG}, 2.0, 25, 1.5, group1to3());
            case 4: return new LevelConfig(4, true, new int[]{}, 1.5, 0, 1.5, group1to3());
            case 5: return new LevelConfig(5, false, new int[]{ENEMY_SHOOTER, ENEMY_FAST}, 2.5, 25, 1.0, group5to7());
            case 6: return new LevelConfig(6, false, new int[]{ENEMY_ZIGZAG, ENEMY_SHOOTER}, 3.0, 30, 0.8, group5to7());
            case 7: return new LevelConfig(7, false, new int[]{ENEMY_NORMAL, ENEMY_FAST, ENEMY_ZIGZAG, ENEMY_SHOOTER}, 3.5, 30, 0.7, group5to7());
            case 8: return new LevelConfig(8, true, new int[]{}, 2.0, 0, 1.0, group5to7());
            default: throw new IllegalArgumentException("Invalid level: " + level);
        }
    }

    public static final int TOTAL_LEVELS = 8;
}