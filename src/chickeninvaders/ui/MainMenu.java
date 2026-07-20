package chickeninvaders.ui;

import chickeninvaders.GameMain;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;

public class MainMenu extends JPanel {

    private final JLabel loggedInLabel;
    private Image backgroundImage;

    public MainMenu(GameMain frame) {
        try {
            backgroundImage = ImageIO.read(new File("resources/images/background2.jpg"));
        } catch (Exception e) {
            System.err.println("Menu background not found: " + e.getMessage());
        }
        setLayout(new BorderLayout());
        setBackground(new Color(10, 10, 40));

        JLabel title = new JLabel("Chicken Invaders", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(Color.YELLOW);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        loggedInLabel = new JLabel("vared nashodi", SwingConstants.CENTER);
        loggedInLabel.setForeground(Color.LIGHT_GRAY);
        loggedInLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton newGameBtn = makeButton("New Game");
        JButton highScoreBtn = makeButton("High Scores");
        JButton settingsBtn = makeButton("Settings");
        JButton howToBtn = makeButton("How to Play");
        JButton storeBtn = makeButton("Store");
        JButton exitBtn = makeButton("Exit");

        newGameBtn.addActionListener(e -> frame.startNewGame());
        highScoreBtn.addActionListener(e -> frame.showCard(GameMain.CARD_HIGHSCORES));
        settingsBtn.addActionListener(e -> frame.showCard(GameMain.CARD_SETTINGS));
        howToBtn.addActionListener(e -> frame.showCard(GameMain.CARD_HOWTO));
        storeBtn.addActionListener(e -> frame.showCard(GameMain.CARD_STORE));
        exitBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(Box.createVerticalGlue());
        for (JButton b : new JButton[]{newGameBtn, highScoreBtn, settingsBtn, howToBtn, storeBtn, exitBtn}) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonPanel.add(b);
            buttonPanel.add(Box.createVerticalStrut(12));
        }
        buttonPanel.add(Box.createVerticalGlue());

        add(buttonPanel, BorderLayout.CENTER);
        add(loggedInLabel, BorderLayout.SOUTH);
    }

    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setPreferredSize(new Dimension(220, 40));
        btn.setFocusPainted(false);
        return btn;
    }

    public void setLoggedInLabel(String username) {
        if (username == null) {
            loggedInLabel.setText("vared nashodi hanu");
        } else {
            loggedInLabel.setText("salam " + username);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
