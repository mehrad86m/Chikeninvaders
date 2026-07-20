package chickeninvaders.sound;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static SoundManager instance;

    private final Map<String, Clip> clips = new HashMap<>();
    private Clip backgroundClip;

    private boolean musicOn = true;
    private boolean shotSoundOn = true;
    private boolean crashSoundOn = true;
    private boolean endSoundOn = true;

    private static final String SOUND_DIR = "resources/sounds/";

    private SoundManager() {
        preload("background", "background.wav");
        preload("shoot", "shoot.wav");
        preload("crash", "crash.wav");
        preload("gameover", "gameover.wav");
        preload("win", "win.wav");
    }

    public static synchronized SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    private void preload(String key, String filename) {
        try {
            File file = new File(SOUND_DIR + filename);
            if (!file.exists()) return;
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            clips.put(key, clip);
            if (key.equals("background")) backgroundClip = clip;
        } catch (Exception e) {
            System.err.println("Could not load sound '" + filename + "': " + e.getMessage());
        }
    }

    public void applySettings(boolean music, boolean shot, boolean crash, boolean end) {
        this.musicOn = music;
        this.shotSoundOn = shot;
        this.crashSoundOn = crash;
        this.endSoundOn = end;
        if (!musicOn) stopBackgroundMusic(); else playBackgroundMusic();
    }

    public void playBackgroundMusic() {
        if (!musicOn || backgroundClip == null) return;
        if (!backgroundClip.isRunning()) {
            backgroundClip.setFramePosition(0);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }

    public void playShot() {
        if (shotSoundOn) playClip("shoot");
    }

    public void playCrash() {
        if (crashSoundOn) playClip("crash");
    }

    public void playGameOver() {
        if (endSoundOn) playClip("gameover");
    }

    public void playWin() {
        if (endSoundOn) playClip("win");
    }

    private void playClip(String key) {
        Clip clip = clips.get(key);
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public boolean isMusicOn() { return musicOn; }
    public boolean isShotSoundOn() { return shotSoundOn; }
    public boolean isCrashSoundOn() { return crashSoundOn; }
    public boolean isEndSoundOn() { return endSoundOn; }
}
