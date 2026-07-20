package chickeninvaders.ui;

import chickeninvaders.GameMain;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {

    public RegisterPanel(GameMain frame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(10, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        JLabel title = new JLabel("ثبت‌نام کاربر جدید");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        JLabel userLabel = new JLabel("نام کاربری:");
        userLabel.setForeground(Color.WHITE);
        add(userLabel, gbc);

        gbc.gridx = 1;
        JTextField userField = new JTextField(15);
        add(userField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        JLabel passLabel = new JLabel("رمز عبور:");
        passLabel.setForeground(Color.WHITE);
        add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(15);
        add(passField, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        JLabel confirmLabel = new JLabel("تکرار رمز:");
        confirmLabel.setForeground(Color.WHITE);
        add(confirmLabel, gbc);

        gbc.gridx = 1;
        JPasswordField confirmField = new JPasswordField(15);
        add(confirmField, gbc);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, gbc);

        gbc.gridy = 5;
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton registerBtn = new JButton("ثبت‌نام");
        JButton backBtn = new JButton("بازگشت");
        btnPanel.add(registerBtn);
        btnPanel.add(backBtn);
        add(btnPanel, gbc);

        registerBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("همه فیلدها را پر کنید.");
                return;
            }
            if (!password.equals(confirm)) {
                errorLabel.setText("رمز عبور و تکرار آن یکسان نیستند.");
                return;
            }
            boolean ok = frame.getDb().registerUser(username, password);
            if (!ok) {
                errorLabel.setText("این نام کاربری قبلا استفاده شده است.");
            } else {
                JOptionPane.showMessageDialog(this, "ثبت‌نام با موفقیت انجام شد. اکنون وارد شوید.");
                userField.setText("");
                passField.setText("");
                confirmField.setText("");
                errorLabel.setText(" ");
                frame.showCard(GameMain.CARD_LOGIN);
            }
        });

        backBtn.addActionListener(e -> frame.showCard(GameMain.CARD_MENU));
    }
}
