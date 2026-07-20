package chickeninvaders.model;

public class GameRecord {
    private String username;
    private int score;
    private int levelReached;
    private String timestamp;
    private String soundSettingsSummary;

    public GameRecord(String username, int score, int levelReached,
                       String timestamp, String soundSettingsSummary) {
        this.username = username;
        this.score = score;
        this.levelReached = levelReached;
        this.timestamp = timestamp;
        this.soundSettingsSummary = soundSettingsSummary;
    }

    public String getUsername() { return username; }
    public int getScore() { return score; }
    public int getLevelReached() { return levelReached; }
    public String getTimestamp() { return timestamp; }
    public String getSoundSettingsSummary() { return soundSettingsSummary; }

    public String toLine() {
        return username + "|" + score + "|" + levelReached + "|" + timestamp + "|" + soundSettingsSummary;
    }

    public static GameRecord fromLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) return null;
        return new GameRecord(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), parts[3], parts[4]);
    }
}
