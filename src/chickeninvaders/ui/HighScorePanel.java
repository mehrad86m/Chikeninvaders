package chickeninvaders.ui;

import chickeninvaders.GameMain;
import chickeninvaders.model.GameRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HighScorePanel extends JPanel {

    private final DefaultTableModel model;
    private final GameMain frame;

    public HighScorePanel(GameMain frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(new Color(10, 10, 40));

        JLabel title = new JLabel("جدول بالاترین امتیازها", SwingConstants.CENTER);
        title.setForeground(Color.YELLOW);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"رتبه", "نام کاربری", "امتیاز", "سطح رسیده", "تاریخ"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton backBtn = new JButton("بازگشت");
        backBtn.addActionListener(e -> frame.showCard(GameMain.CARD_MENU));
        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(backBtn);
        add(south, BorderLayout.SOUTH);
    }

    public void refresh() {
        model.setRowCount(0);
        List<GameRecord> records = frame.getDb().getHighScoreTable();
        int rank = 1;
        for (GameRecord r : records) {
            model.addRow(new Object[]{rank++, r.getUsername(), r.getScore(), r.getLevelReached(), r.getTimestamp()});
        }
    }
}
