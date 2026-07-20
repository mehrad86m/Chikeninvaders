package chickeninvaders;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameMain gameMain = new GameMain();
            gameMain.setVisible(true);
        });
    }
}
