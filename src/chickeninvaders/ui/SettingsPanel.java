package chickeninvaders.ui;

import chickeninvaders.GameMain;
import chickeninvaders.model.User;
import chickeninvaders.sound.SoundManager;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final JCheckBox musicBox;
    private final JCheckBox shotBox;
    private final JCheckBox crashBox;
    private final JCheckBox endBox;
    private final JCheckBox easyModeBox;
    private final GameMain frame;

    public SettingsPanel(GameMain frame) {
        this.frame = frame;
        setLayout(new GridBagLayout());
        setBackground(new Color(10, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel title = new JLabel("تنظیمات صدا");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        add(title, gbc);

        musicBox = makeCheckbox("موسیقی زمینه");
        shotBox = makeCheckbox("افکت شلیک");
        crashBox = makeCheckbox("افکت برخورد / انفجار");
        endBox = makeCheckbox("صدای پایان بازی");

        gbc.gridy = 1; add(musicBox, gbc);
        gbc.gridy = 2; add(shotBox, gbc);
        gbc.gridy = 3; add(crashBox, gbc);
        gbc.gridy = 4; add(endBox, gbc);

        easyModeBox = new JCheckBox("حالت آسان (تمرین) - کاهش سرعت و تعداد دشمنان", false);
        easyModeBox.setForeground(Color.ORANGE);
        easyModeBox.setOpaque(false);
        easyModeBox.setFont(new Font("SansSerif", Font.PLAIN, 15));
        gbc.gridy = 5; add(easyModeBox, gbc);

        JLabel note = new JLabel("توجه: حالت آسان فقط برای تمرین است و تنظیمات اصلی بازی را تغییر نمی‌دهد.");
        note.setForeground(Color.LIGHT_GRAY);
        note.setFont(new Font("SansSerif", Font.ITALIC, 11));
        gbc.gridy = 6; add(note, gbc);

        gbc.gridy = 7;
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton saveBtn = new JButton("ذخیره");
        JButton backBtn = new JButton("بازگشت");
        btnPanel.add(saveBtn);
        btnPanel.add(backBtn);
        add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            SoundManager sm = SoundManager.getInstance();
            sm.applySettings(musicBox.isSelected(), shotBox.isSelected(), crashBox.isSelected(), endBox.isSelected());
            User u = frame.getCurrentUser();
            if (u != null) {
                u.setMusicOn(musicBox.isSelected());
                u.setShotSoundOn(shotBox.isSelected());
                u.setCrashSoundOn(crashBox.isSelected());
                u.setEndSoundOn(endBox.isSelected());
                u.setEasyMode(easyModeBox.isSelected());
                frame.getDb().updateUser(u);
            }
            JOptionPane.showMessageDialog(this, "تنظیمات ذخیره شد.");
        });
        backBtn.addActionListener(e -> frame.showCard(GameMain.CARD_MENU));
    }

    private JCheckBox makeCheckbox(String text) {
        JCheckBox box = new JCheckBox(text, true);
        box.setForeground(Color.WHITE);
        box.setOpaque(false);
        box.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return box;
    }

    /** Call before showing this panel to reflect the current user's saved preferences. */
    public void loadCurrentSettings() {
        User u = frame.getCurrentUser();
        SoundManager sm = SoundManager.getInstance();
        boolean music = u != null ? u.isMusicOn() : sm.isMusicOn();
        boolean shot = u != null ? u.isShotSoundOn() : sm.isShotSoundOn();
        boolean crash = u != null ? u.isCrashSoundOn() : sm.isCrashSoundOn();
        boolean end = u != null ? u.isEndSoundOn() : sm.isEndSoundOn();
        musicBox.setSelected(music);
        shotBox.setSelected(shot);
        crashBox.setSelected(crash);
        endBox.setSelected(end);
        easyModeBox.setSelected(u != null && u.isEasyMode());
    }
}
