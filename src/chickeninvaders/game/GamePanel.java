package chickeninvaders.game;

import chickeninvaders.GameMain;
import chickeninvaders.enemy.*;
import chickeninvaders.model.User;
import chickeninvaders.sound.SoundManager;

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.imageio.ImageIO;
import java.io.File;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final int PANEL_WIDTH = GameMain.WIDTH;
    private static final int PANEL_HEIGHT = GameMain.HEIGHT;
    private static final int FPS_DELAY_MS = 16;

    private static final int GRID_ROWS = 5;
    private static final int GRID_COLS = 8;
    private static final int CELL_W = 70;
    private static final int CELL_H = 50;
    private static final int GRID_START_X = 90;
    private static final int GRID_START_Y = 60;

    // تعریف ثابتهای عددی پاورآپ ها
    public static final int POWERUP_RAPID_FIRE = 0;
    public static final int POWERUP_FREEZE_BOMB = 1;
    public static final int POWERUP_EXTRA_LIFE = 2;
    public static final int POWERUP_SHIELD = 3;
    public static final int POWERUP_ADD_FIRE = 4;

    private final GameMain frame;
    private final User user;
    private final SoundManager sound = SoundManager.getInstance();

    private Timer timer;
    private final Set<Integer> keysPressed = new HashSet<>();

    private Plane plane;
    private int level;
    private LevelConfig config;

    private List<Cell> cells = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private Boss boss;

    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Egg> eggs = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();

    private int score = 0;
    private boolean paused = false;
    private boolean gameOver = false;
    private boolean won = false;

    private double gridOffsetX;
    private double gridOffsetY;
    private int gridDirection = 1;

    private long levelStartTimeNanos;
    private long lastEggDropTime;
    private long freezeEndTime = 0;

    private boolean easyMode = false;
    private double effectiveGridSpeed;
    private double effectiveEggInterval;

    private final Random rnd = new Random();
    private Image backgroundIm ;

    public GamePanel(GameMain frame, User user) {
        this.frame = frame;
        this.user = user;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        try {
            backgroundIm = ImageIO.read(new File("resources/images/background.jpg"));
        } catch (Exception e) {
            System.err.println("Background image not found: " + e.getMessage());
        }
    }

    public void startGame() {
        score = 0;
        gameOver = false;
        won = false;
        paused = false;
        level = 1;
        easyMode = user.isEasyMode();
        String chosenPlane = user.getOwnedPlane(); // تغییر یافته به String
        plane = new Plane(PANEL_WIDTH, PANEL_HEIGHT, chosenPlane);
        bullets.clear();
        eggs.clear();
        powerUps.clear();
        explosions.clear();
        loadLevel(level);

        if (timer != null) timer.stop();
        timer = new Timer(FPS_DELAY_MS, this);
        timer.start();
        sound.playBackgroundMusic();
    }

    private void loadLevel(int lvl) {
        config = LevelConfig.forLevel(lvl);
        bullets.clear();
        eggs.clear();
        powerUps.clear();
        boss = null;
        cells.clear();
        enemies.clear();
        gridOffsetX = GRID_START_X;
        gridOffsetY = GRID_START_Y;
        gridDirection = 1;
        levelStartTimeNanos = System.nanoTime();
        lastEggDropTime = System.currentTimeMillis();

        effectiveGridSpeed = easyMode ? config.gridSpeed * 0.6 : config.gridSpeed;
        effectiveEggInterval = easyMode ? config.eggIntervalSec * 1.5 : config.eggIntervalSec;

        if (config.isBoss) {
            boss = (lvl == 4) ? new BossLevel4(PANEL_WIDTH) : new BossLevel8(PANEL_WIDTH);
            if (easyMode) boss.applyEasyMode();
        } else {
            for (int row = 0; row < GRID_ROWS; row++) {
                for (int col = 0; col < GRID_COLS; col++) {
                    int type = config.allowedTypes[rnd.nextInt(config.allowedTypes.length)];
                    int counter = config.counterFor(type);
                    if (easyMode) counter = Math.max(1, counter - 1);
                    Cell cell = new Cell(row, col, type, counter);
                    cells.add(cell);
                    double bx = gridOffsetX + col * CELL_W;
                    double by = gridOffsetY + row * CELL_H;
                    enemies.add(createEnemy(cell, bx, by, false));
                }
            }
        }
    }

    private Enemy createEnemy(Cell cell, double x, double y, boolean entering) {
        switch (cell.type) {
            case LevelConfig.ENEMY_FAST: return new FastEnemy(cell, x, y, entering);
            case LevelConfig.ENEMY_ZIGZAG: return new ZigzagEnemy(cell, x, y, entering);
            case LevelConfig.ENEMY_SHOOTER: return new ShooterEnemy(cell, x, y, entering);
            case LevelConfig.ENEMY_NORMAL:
            default: return new NormalEnemy(cell, x, y, entering);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused && !gameOver && !won) {
            handleContinuousInput();
            update();
        }
        repaint();
    }

    private void handleContinuousInput() {
        if (keysPressed.contains(KeyEvent.VK_LEFT) || keysPressed.contains(KeyEvent.VK_A)) plane.moveLeft();
        if (keysPressed.contains(KeyEvent.VK_RIGHT) || keysPressed.contains(KeyEvent.VK_D)) plane.moveRight(PANEL_WIDTH);
        if (keysPressed.contains(KeyEvent.VK_UP) || keysPressed.contains(KeyEvent.VK_W)) plane.moveUp();
        if (keysPressed.contains(KeyEvent.VK_DOWN) || keysPressed.contains(KeyEvent.VK_S)) plane.moveDown(PANEL_HEIGHT);

        // شلیک رگباری مداوم فقط در زمان داشتن پاورآپ مجاز است
        if (keysPressed.contains(KeyEvent.VK_SPACE) && plane.isRapidFireActive()) {
            tryShoot();
        }
    }

    private void tryShoot() {
        if (!plane.canShoot()) return;
        plane.registerShot();
        int n = plane.getSimultaneousBullets();
        double centerX = plane.x + Plane.WIDTH / 2.0;
        double spacing = 15;
        double startX = centerX - (n - 1) * spacing / 2.0;
        for (int i = 0; i < n; i++) {
            bullets.add(new Bullet(startX + i * spacing - Bullet.WIDTH / 2.0, plane.y));
        }
        sound.playShot();
    }

    private boolean isFrozen() {
        return System.currentTimeMillis() < freezeEndTime;
    }

    private void update() {
        double time = (System.nanoTime() - levelStartTimeNanos) / 1_000_000_000.0;
        boolean frozen = isFrozen();

        if (!config.isBoss) {
            if (!frozen) updateGridFormation();
            updateEnemies(time, frozen);
            handleEggDropping(frozen);
        } else if (boss != null && !frozen) {
            boss.update(PANEL_WIDTH, PANEL_HEIGHT);
            boss.tryAttack(eggs);
        }

        updateBullets();
        updateEggs(frozen);
        updatePowerUps();
        updateExplosions();

        resolveCollisions();
        if (gameOver || won) return;
        checkEnemiesReachedBottom();
        checkLevelProgress();
    }

    private void checkEnemiesReachedBottom() {
        if (config.isBoss) return;
        double dangerLine = PANEL_HEIGHT - 70;
        for (Enemy en : enemies) {
            if (en.isAlive() && !en.isEntering() && en.y + Enemy.SIZE >= dangerLine) {
                endGame(false);
                return;
            }
        }
    }

    private void updateGridFormation() {
        gridOffsetX += effectiveGridSpeed * gridDirection;
        int gridWidth = (GRID_COLS - 1) * CELL_W + Enemy.SIZE;
        if (gridOffsetX <= 10 || gridOffsetX + gridWidth >= PANEL_WIDTH - 10) {
            gridDirection *= -1;
            gridOffsetY += config.verticalStep;
        }
    }

    private void updateEnemies(double time, boolean frozen) {
        for (Enemy en : enemies) {
            en.setFrozen(frozen);
            if (frozen) continue;
            double targetX = gridOffsetX + en.getCell().col * CELL_W;
            double targetY = gridOffsetY + en.getCell().row * CELL_H;
            en.update(targetX, targetY, time);

            if (en instanceof ShooterEnemy && ((ShooterEnemy) en).shouldFireNow()) {
                double dirX = (plane.x - en.x) >= 0 ? 1 : -1;
                eggs.add(new Egg(en.x + Enemy.SIZE / 2.0, en.y + Enemy.SIZE / 2.0, dirX * 5.0, 0));
            }
        }
    }

    private void handleEggDropping(boolean frozen) {
        if (frozen) return;
        long now = System.currentTimeMillis();
        if (now - lastEggDropTime >= effectiveEggInterval * 1000) {
            lastEggDropTime = now;
            List<Enemy> candidates = new ArrayList<>();
            for (Enemy en : enemies) {
                if (en.isAlive() && !en.isEntering()) candidates.add(en);
            }
            if (!candidates.isEmpty()) {
                Enemy chosen = candidates.get(rnd.nextInt(candidates.size()));
                eggs.add(new Egg(chosen.x + Enemy.SIZE / 2.0, chosen.y + Enemy.SIZE, 0, 4.0));
            }
        }
    }

    private void updateBullets() {
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update();
            if (!b.isAlive()) it.remove();
        }
    }

    private void updateEggs(boolean frozen) {
        Iterator<Egg> it = eggs.iterator();
        while (it.hasNext()) {
            Egg egg = it.next();
            egg.setFrozen(frozen);
            if (!frozen) egg.update(PANEL_WIDTH, PANEL_HEIGHT);
            if (!egg.isAlive()) it.remove();
        }
    }

    private void updatePowerUps() {
        Iterator<PowerUp> it = powerUps.iterator();
        while (it.hasNext()) {
            PowerUp p = it.next();
            p.update(PANEL_HEIGHT);
            if (!p.isAlive()) it.remove();
        }
    }

    private void updateExplosions() {
        Iterator<Explosion> it = explosions.iterator();
        while (it.hasNext()) {
            Explosion ex = it.next();
            ex.update();
            if (ex.isFinished()) it.remove();
        }
    }

    private void resolveCollisions() {
        Iterator<Bullet> bIt = bullets.iterator();
        while (bIt.hasNext()) {
            Bullet b = bIt.next();
            boolean consumed = false;

            for (Enemy en : enemies) {
                if (en.isAlive() && en.getBounds().intersects(b.getBounds())) {
                    killEnemy(en);
                    consumed = true;
                    break;
                }
            }
            if (!consumed && boss != null && !boss.isDead() && boss.getBounds().intersects(b.getBounds())) {
                int dmg = plane.hasBossDamageBonus() ? 2 : 1;
                boss.takeDamage(dmg);
                explosions.add(new Explosion(b.x, b.y, 14));
                consumed = true;
                if (boss.isDead()) {
                    explosions.add(new Explosion(boss.getCenterX(), boss.getCenterY(), 90));
                    sound.playCrash();
                    score += (level == 4) ? 500 : 1000;
                }
            }
            if (consumed) bIt.remove();
        }
        enemies.removeIf(en -> !en.isAlive());

        Iterator<Egg> eIt = eggs.iterator();
        while (eIt.hasNext()) {
            Egg egg = eIt.next();
            if (egg.getBounds().intersects(plane.getBounds())) {
                eIt.remove();
                if (!plane.isShieldActive()) {
                    plane.loseLife();
                    explosions.add(new Explosion(plane.x + Plane.WIDTH / 2.0, plane.y + Plane.HEIGHT / 2.0));
                    sound.playCrash();
                    if (plane.isDead()) {
                        endGame(false);
                        return;
                    }
                }
            }
        }

        Iterator<PowerUp> pIt = powerUps.iterator();
        while (pIt.hasNext()) {
            PowerUp p = pIt.next();
            if (p.getBounds().intersects(plane.getBounds())) {
                applyPowerUp(p.type);
                pIt.remove();
            }
        }
    }

    private void killEnemy(Enemy en) {
        en.kill();

        int eType = en.getCell().type;
        if (eType == LevelConfig.ENEMY_FAST) score += 20;
        else if (eType == LevelConfig.ENEMY_ZIGZAG) score += 30;
        else if (eType == LevelConfig.ENEMY_SHOOTER) score += 40;
        else score += 10; // Normal Enemy

        explosions.add(new Explosion(en.x + Enemy.SIZE / 2.0, en.y + Enemy.SIZE / 2.0));
        sound.playCrash();

        Cell cell = en.getCell();
        boolean spawnReplacement = cell.onEnemyKilled();
        if (spawnReplacement) {
            boolean fromLeft = rnd.nextBoolean();
            double startX = fromLeft ? -Enemy.SIZE : PANEL_WIDTH + Enemy.SIZE;
            Enemy replacement = createEnemy(cell, startX, -Enemy.SIZE, true);
            cell.setOccupied(true);
            enemies.add(replacement);
        }

        if (rnd.nextDouble() < 0.20) {
            spawnPowerUp(en.x, en.y);
        }
    }

    private void spawnPowerUp(double x, double y) {
        int randomType = rnd.nextInt(5);
        powerUps.add(new PowerUp(x, y, randomType));
    }

    private void applyPowerUp(int type) {
        switch (type) {
            case POWERUP_RAPID_FIRE: plane.activateRapidFire(8000); break;
            case POWERUP_FREEZE_BOMB: freezeEndTime = System.currentTimeMillis() + 3000; break;
            case POWERUP_EXTRA_LIFE: plane.gainLife(); break;
            case POWERUP_SHIELD: plane.activateShield(10000); break;
            case POWERUP_ADD_FIRE: plane.increaseAddFire(); break;
        }
    }

    private void checkLevelProgress() {
        if (gameOver || won) return;

        if (config.isBoss) {
            if (boss != null && boss.isDead()) {
                advanceLevel();
            }
            return;
        }

        boolean allCleared = true;
        for (Cell c : cells) {
            if (!c.isCleared()) { allCleared = false; break; }
        }
        if (allCleared && enemies.isEmpty()) {
            score += 200;
            advanceLevel();
        }
    }

    private void advanceLevel() {
        if (level >= LevelConfig.TOTAL_LEVELS) {
            endGame(true);
            return;
        }
        level++;
        loadLevel(level);
    }

    private void endGame(boolean victory) {
        won = victory;
        gameOver = !victory;
        timer.stop();
        if (victory) {
            sound.playWin();
        } else {
            sound.playGameOver();
        }
        frame.getDb().saveGameResult(user, score, level);

        String message = victory
                ? "شما برنده شدید! امتیاز نهایی: " + score
                : "بازی تمام شد. امتیاز نهایی: " + score;
        JOptionPane.showMessageDialog(this, message);
        frame.onGameFinished();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundIm != null) {
            g2.drawImage(backgroundIm, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, this);
        } else {
            g2.setColor(new Color(5, 5, 25));
            g2.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        }

        for (Enemy en : enemies) en.draw(g2);
        if (boss != null) boss.draw(g2);
        for (Egg egg : eggs) egg.draw(g2);
        for (Bullet b : bullets) b.draw(g2);
        for (PowerUp p : powerUps) p.draw(g2);
        for (Explosion ex : explosions) ex.draw(g2);
        if (plane != null) plane.draw(g2);

        drawHud(g2);

        if (boss != null) boss.drawHealthBar(g2, PANEL_WIDTH);

        if (paused) {
            drawCenteredMessage(g2, "PAUSED - press P to resume");
        }
    }

    private void drawHud(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("امتیاز: " + score, 10, PANEL_HEIGHT - 40);
        g.drawString("مرحله: " + level, 10, PANEL_HEIGHT - 24);
        g.drawString("جان: " + (plane != null ? plane.getLives() : 0), 150, PANEL_HEIGHT - 40);
        g.drawString("کاربر: " + user.getUsername(), 150, PANEL_HEIGHT - 24);
        g.drawString("تیرها: " + (plane != null ? plane.getSimultaneousBullets() : 1), 300, PANEL_HEIGHT - 40);
        StringBuilder activeEffects = new StringBuilder();
        if (plane != null) {
            if (plane.isRapidFireActive()) activeEffects.append("RapidFire ");
            if (plane.isShieldActive()) activeEffects.append("Shield ");
        }
        if (isFrozen()) activeEffects.append("Freeze ");
        g.drawString(activeEffects.toString(), 300, PANEL_HEIGHT - 24);
    }

    private void drawCenteredMessage(Graphics2D g, String msg) {
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.setColor(Color.YELLOW);
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(msg);
        g.drawString(msg, (PANEL_WIDTH - w) / 2, PANEL_HEIGHT / 2);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean alreadyPressed = keysPressed.contains(e.getKeyCode());
        keysPressed.add(e.getKeyCode());

        if (e.getKeyCode() == KeyEvent.VK_SPACE && !alreadyPressed) {
            if (!plane.isRapidFireActive()) {
                tryShoot();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_P) {
            paused = !paused;
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (timer != null) timer.stop();
            frame.getDb().saveGameResult(user, score, level);
            frame.onGameFinished();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) { }
}