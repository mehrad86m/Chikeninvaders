package chickeninvaders.ui;

import chickeninvaders.GameMain;

import javax.swing.*;
import java.awt.*;

public class HowToPlayPanel extends JPanel {

    public HowToPlayPanel(GameMain frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(10, 10, 40));

        JLabel title = new JLabel("راهنمای بازی", SwingConstants.CENTER);
        title.setForeground(Color.YELLOW);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        String text =
            "controls:\n" +
            "go right with :D and → \n" +
            "go down with :A and ←\n" +
            "go up with :w and ↑\n" +
            "go down with :s and ↓ \n" +
            "shoot with :Space \n" +
            "pause and resume with :p\n" +
            "got back to menu with :Esc\n\n" ;

        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setOpaque(false);
        area.setForeground(Color.WHITE);
        area.setFont(new Font("SansSerif", Font.PLAIN, 16));
        area.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        add(area, BorderLayout.CENTER);

        JButton backBtn = new JButton("بازگشت");
        backBtn.addActionListener(e -> frame.showCard(GameMain.CARD_MENU));
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(backBtn);
        add(south, BorderLayout.SOUTH);
    }
}
