package chickeninvaders;

import chickeninvaders.db.DatabaseManager;
import chickeninvaders.model.User;
import chickeninvaders.sound.SoundManager;
import chickeninvaders.ui.*;
import chickeninvaders.game.GamePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Top level JFrame. Owns a CardLayout that switches between all screens:
 * MainMenu, LoginPanel, RegisterPanel, HighScorePanel, SettingsPanel,
 * HowToPlayPanel, StorePanel and the GamePanel itself.
 */
public class GameMain extends JFrame {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static final String CARD_MENU = "menu";
    public static final String CARD_LOGIN = "login";
    public static final String CARD_REGISTER = "register";
    public static final String CARD_HIGHSCORES = "highscores";
    public static final String CARD_SETTINGS = "settings";
    public static final String CARD_HOWTO = "howto";
    public static final String CARD_STORE = "store";
    public static final String CARD_GAME = "game";

    private final CardLayout cardLayout;
    private final JPanel cards;

    private final DatabaseManager db;
    private User currentUser; // null until logged in

    private MainMenu mainMenu;
    private GamePanel gamePanel;

    public GameMain() {
        super("Chicken Invaders - منوی اصلی");
        this.db = DatabaseManager.getInstance();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        mainMenu = new MainMenu(this);
        cards.add(mainMenu, CARD_MENU);
        cards.add(new LoginPanel(this), CARD_LOGIN);
        cards.add(new RegisterPanel(this), CARD_REGISTER);
        cards.add(new HighScorePanel(this), CARD_HIGHSCORES);
        cards.add(new SettingsPanel(this), CARD_SETTINGS);
        cards.add(new HowToPlayPanel(this), CARD_HOWTO);
        cards.add(new StorePanel(this), CARD_STORE);

        add(cards);
        pack();
        setLocationRelativeTo(null);

        SoundManager.getInstance().playBackgroundMusic();
    }

    public void showCard(String name) {
        // Refresh dynamic panels right before showing them
        if (name.equals(CARD_HIGHSCORES)) {
            ((HighScorePanel) getCardByName(CARD_HIGHSCORES)).refresh();
        }
        if (name.equals(CARD_STORE)) {
            ((StorePanel) getCardByName(CARD_STORE)).refresh();
        }
        if (name.equals(CARD_SETTINGS)) {
            ((SettingsPanel) getCardByName(CARD_SETTINGS)).loadCurrentSettings();
        }
        cardLayout.show(cards, name);
    }

    private Component getCardByName(String name) {
        for (Component c : cards.getComponents()) {
            switch (name) {
                case CARD_HIGHSCORES: if (c instanceof HighScorePanel) return c; break;
                case CARD_STORE: if (c instanceof StorePanel) return c; break;
                case CARD_SETTINGS: if (c instanceof SettingsPanel) return c; break;
                default: break;
            }
        }
        return null;
    }

    /** Called by NewGame handler. Requires the user to be logged in first. */
    public void startNewGame() {
        if (currentUser == null) {
            showCard(CARD_LOGIN);
            return;
        }
        if (gamePanel != null) {
            cards.remove(gamePanel);
        }
        gamePanel = new GamePanel(this, currentUser);
        cards.add(gamePanel, CARD_GAME);
        showCard(CARD_GAME);
        gamePanel.startGame();
        gamePanel.requestFocusInWindow();
    }

    public void onGameFinished() {
        showCard(CARD_MENU);
    }

    public DatabaseManager getDb() { return db; }

    public User getCurrentUser() { return currentUser; }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            SoundManager.getInstance().applySettings(
                    user.isMusicOn(), user.isShotSoundOn(), user.isCrashSoundOn(), user.isEndSoundOn());
            mainMenu.setLoggedInLabel(user.getUsername());
        } else {
            mainMenu.setLoggedInLabel(null);
        }
    }
}
