package chickeninvaders.ui;

import chickeninvaders.GameMain;
import chickeninvaders.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    public LoginPanel(GameMain frame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(10, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        JLabel title = new JLabel("login");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        JLabel userLabel = new JLabel("username");
        userLabel.setForeground(Color.WHITE);
        add(userLabel, gbc);

        gbc.gridx = 1;
        JTextField userField = new JTextField(15);
        add(userField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        JLabel passLabel = new JLabel("password");
        passLabel.setForeground(Color.WHITE);
        add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(15);
        add(passField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, gbc);

        gbc.gridy = 4;
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton loginBtn = new JButton("ورود");
        JButton registerBtn = new JButton("ثبت‌نام");
        JButton backBtn = new JButton("بازگشت");
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        btnPanel.add(backBtn);
        add(btnPanel, gbc);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("نام کاربری و رمز عبور را وارد کنید.");
                return;
            }
            User user = frame.getDb().login(username, password);
            if (user == null) {
                errorLabel.setText("نام کاربری یا رمز عبور اشتباه است.");
            } else {
                frame.setCurrentUser(user);
                userField.setText("");
                passField.setText("");
                errorLabel.setText(" ");
                frame.showCard(GameMain.CARD_MENU);
            }
        });

        registerBtn.addActionListener(e -> frame.showCard(GameMain.CARD_REGISTER));
        backBtn.addActionListener(e -> frame.showCard(GameMain.CARD_MENU));
    }
}
