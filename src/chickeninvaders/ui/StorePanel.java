package chickeninvaders.ui;

import chickeninvaders.GameMain;
import chickeninvaders.model.User;

import javax.swing.*;
import java.awt.*;

public class StorePanel extends JPanel {

    private final GameMain frame;
    private final JLabel infoLabel;
    private final JPanel planesPanel;

    // شبیه سازی ساختار عددی و متنی مشخصات هواپیماها بدون نیاز به enum
    private static class PlaneSpec {
        String displayName;
        int cost;
        int speed;
        int fireRateMs;
        int startingLives;
        boolean doubleBossDamage;

        PlaneSpec(String name, int cost, int speed, int fireRate, int lives, boolean bossDmg) {
            this.displayName = name;
            this.cost = cost;
            this.speed = speed;
            this.fireRateMs = fireRate;
            this.startingLives = lives;
            this.doubleBossDamage = bossDmg;
        }
    }

    // تعریف لیست هواپیماهای موجود در فروشگاه
    private static final PlaneSpec[] AVAILABLE_PLANES = {
            new PlaneSpec("Default", 0, 6, 300, 3, false),
            new PlaneSpec("Advanced", 1000, 8, 200, 4, false),
            new PlaneSpec("Sniper", 2500, 5, 400, 3, true)
    };

    public StorePanel(GameMain frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(new Color(10, 10, 40));

        JLabel title = new JLabel("فروشگاه هواپیما", SwingConstants.CENTER);
        title.setForeground(Color.YELLOW);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        infoLabel = new JLabel("", SwingConstants.CENTER);
        infoLabel.setForeground(Color.WHITE);

        planesPanel = new JPanel();
        planesPanel.setOpaque(false);
        planesPanel.setLayout(new GridLayout(AVAILABLE_PLANES.length, 1, 5, 5));

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(infoLabel, BorderLayout.NORTH);
        center.add(planesPanel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JButton backBtn = new JButton("بازگشت");
        backBtn.addActionListener(e -> frame.showCard(GameMain.CARD_MENU));
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(backBtn);
        add(south, BorderLayout.SOUTH);
    }

    public void refresh() {
        planesPanel.removeAll();
        User u = frame.getCurrentUser();
        if (u == null) {
            infoLabel.setText("برای استفاده از فروشگاه ابتدا وارد شوید.");
            revalidate();
            repaint();
            return;
        }
        infoLabel.setText("امتیاز شما: " + u.getHighScore() + "   |   هواپیمای فعلی: " + u.getOwnedPlane());

        for (PlaneSpec type : AVAILABLE_PLANES) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setOpaque(false);
            String label = String.format("%s | هزینه: %d | سرعت: %d | نرخ شلیک: %dms | جان: %d%s",
                    type.displayName, type.cost, type.speed, type.fireRateMs, type.startingLives,
                    type.doubleBossDamage ? " | آسیب مضاعف به غول" : "");
            JLabel l = new JLabel(label);
            l.setForeground(Color.WHITE);
            row.add(l);

            JButton buyBtn = new JButton(type.displayName.equals(u.getOwnedPlane()) ? "فعال" : "خرید/فعال‌سازی");
            buyBtn.setEnabled(!type.displayName.equals(u.getOwnedPlane()));
            buyBtn.addActionListener(e -> {
                if (u.getHighScore() < type.cost) {
                    JOptionPane.showMessageDialog(this, "امتیاز کافی ندارید.");
                    return;
                }
                u.setOwnedPlane(type.displayName);
                frame.getDb().updateUser(u);
                refresh();
            });
            row.add(buyBtn);
            planesPanel.add(row);
        }
        revalidate();
        repaint();
    }
}
