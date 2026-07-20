package chickeninvaders.model;

public class User {
    private String username;
    private String password;
    private int highScore;
    private int lastLevelReached;

    // Sound settings
    private boolean musicOn = true;
    private boolean shotSoundOn = true;
    private boolean crashSoundOn = true;
    private boolean endSoundOn = true;

    // Store (optional section)
    private String ownedPlane = "Default";

    // Optional practice toggle - does NOT change the default/spec gameplay unless enabled
    private boolean easyMode = false;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.highScore = 0;
        this.lastLevelReached = 1;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getHighScore() { return highScore; }
    public void setHighScore(int highScore) { this.highScore = highScore; }
    public int getLastLevelReached() { return lastLevelReached; }
    public void setLastLevelReached(int lastLevelReached) { this.lastLevelReached = lastLevelReached; }

    public boolean isMusicOn() { return musicOn; }
    public void setMusicOn(boolean musicOn) { this.musicOn = musicOn; }
    public boolean isShotSoundOn() { return shotSoundOn; }
    public void setShotSoundOn(boolean shotSoundOn) { this.shotSoundOn = shotSoundOn; }
    public boolean isCrashSoundOn() { return crashSoundOn; }
    public void setCrashSoundOn(boolean crashSoundOn) { this.crashSoundOn = crashSoundOn; }
    public boolean isEndSoundOn() { return endSoundOn; }
    public void setEndSoundOn(boolean endSoundOn) { this.endSoundOn = endSoundOn; }

    public String getOwnedPlane() { return ownedPlane; }
    public void setOwnedPlane(String ownedPlane) { this.ownedPlane = ownedPlane; }

    public boolean isEasyMode() { return easyMode; }
    public void setEasyMode(boolean easyMode) { this.easyMode = easyMode; }

    public void updateHighScoreIfBetter(int score) {
        if (score > this.highScore) {
            this.highScore = score;
        }
    }
}
