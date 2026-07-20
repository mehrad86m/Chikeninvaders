package chickeninvaders.db;

import chickeninvaders.model.GameRecord;
import chickeninvaders.model.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatabaseManager {

    private static final String DB_DIR = "database";
    private static final Path USERS_FILE = Paths.get(DB_DIR, "users.txt");
    private static final Path GAMES_FILE = Paths.get(DB_DIR, "games.txt");

    private static DatabaseManager instance;

    private final Map<String, User> userCache = new LinkedHashMap<>();

    private DatabaseManager() {
        ensureFiles();
        loadUsers();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void ensureFiles() {
        try {
            Files.createDirectories(Paths.get(DB_DIR));
            if (!Files.exists(USERS_FILE)) {
                Files.createFile(USERS_FILE);
            }
            if (!Files.exists(GAMES_FILE)) {
                Files.createFile(GAMES_FILE);
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize database files: " + e.getMessage());
        }
    }

    private void loadUsers() {
        userCache.clear();
        try {
            List<String> lines = Files.readAllLines(USERS_FILE, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length < 9) continue;
                User u = new User(p[0], p[1]);
                u.setHighScore(Integer.parseInt(p[2]));
                u.setLastLevelReached(Integer.parseInt(p[3]));
                u.setMusicOn(Boolean.parseBoolean(p[4]));
                u.setShotSoundOn(Boolean.parseBoolean(p[5]));
                u.setCrashSoundOn(Boolean.parseBoolean(p[6]));
                u.setEndSoundOn(Boolean.parseBoolean(p[7]));
                u.setOwnedPlane(p[8]);
                if (p.length >= 10) {
                    u.setEasyMode(Boolean.parseBoolean(p[9]));
                }
                userCache.put(u.getUsername(), u);
            }
        } catch (IOException e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }
    }

    private synchronized void persistUsers() {
        try (BufferedWriter writer = Files.newBufferedWriter(USERS_FILE, StandardCharsets.UTF_8)) {
            for (User u : userCache.values()) {
                writer.write(userToLine(u));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to persist users: " + e.getMessage());
        }
    }

    private String userToLine(User u) {
        return String.join("|",
                u.getUsername(),
                u.getPassword(),
                String.valueOf(u.getHighScore()),
                String.valueOf(u.getLastLevelReached()),
                String.valueOf(u.isMusicOn()),
                String.valueOf(u.isShotSoundOn()),
                String.valueOf(u.isCrashSoundOn()),
                String.valueOf(u.isEndSoundOn()),
                u.getOwnedPlane(),
                String.valueOf(u.isEasyMode()));
    }

    // ---------------------- User management ----------------------

    public synchronized boolean usernameExists(String username) {
        return userCache.containsKey(username);
    }

    /** Returns true if registration succeeded, false if username already exists. */
    public synchronized boolean registerUser(String username, String password) {
        if (usernameExists(username)) return false;
        User u = new User(username, password);
        userCache.put(username, u);
        persistUsers();
        return true;
    }

    /** Returns the User if credentials match, otherwise null. */
    public synchronized User login(String username, String password) {
        User u = userCache.get(username);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    public synchronized void updateUser(User u) {
        userCache.put(u.getUsername(), u);
        persistUsers();
    }

    // ---------------------- Game records ----------------------

    public synchronized void saveGameResult(User user, int score, int levelReached) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String soundSummary = "M:" + user.isMusicOn() + ",S:" + user.isShotSoundOn()
                + ",C:" + user.isCrashSoundOn() + ",E:" + user.isEndSoundOn();

        GameRecord record = new GameRecord(user.getUsername(), score, levelReached, timestamp, soundSummary);
        try (BufferedWriter writer = Files.newBufferedWriter(GAMES_FILE, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            writer.write(record.toLine());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to save game result: " + e.getMessage());
        }

        user.updateHighScoreIfBetter(score);
        if (levelReached > user.getLastLevelReached()) {
            user.setLastLevelReached(levelReached);
        }
        updateUser(user);
    }

    public synchronized List<GameRecord> loadAllGameRecords() {
        List<GameRecord> records = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(GAMES_FILE, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                GameRecord r = GameRecord.fromLine(line);
                if (r != null) records.add(r);
            }
        } catch (IOException e) {
            System.err.println("Failed to load game records: " + e.getMessage());
        }
        return records;
    }

    public synchronized List<GameRecord> getHighScoreTable() {
        Map<String, GameRecord> bestPerUser = new HashMap<>();
        for (GameRecord r : loadAllGameRecords()) {
            GameRecord existing = bestPerUser.get(r.getUsername());
            if (existing == null || r.getScore() > existing.getScore()) {
                bestPerUser.put(r.getUsername(), r);
            }
        }
        List<GameRecord> result = new ArrayList<>(bestPerUser.values());
        result.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        return result;
    }
}
