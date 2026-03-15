package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminDashboard extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel clockLabel;
    private Timer clockTimer;
    private JToggleButton selectedMenuBtn;

    public AdminDashboard(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("TestNova — Teacher Dashboard");
        setSize(1250, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1050, 700));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_DARK);

        mainPanel.add(createTopBar(), BorderLayout.NORTH);
        mainPanel.add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.BG_MEDIUM);

        contentPanel.add(new AdminOverviewPanel(), "OVERVIEW");
        contentPanel.add(new ManageExamsPanel(currentUser), "EXAMS");
        contentPanel.add(new ManageQuestionsPanel(), "QUESTIONS");
        contentPanel.add(new ResultsPanel(currentUser), "RESULTS");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // Start clock
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clockTimer.stop();
            }
        });
    }

    private void updateClock() {
        if (clockLabel != null) {
            clockLabel.setText(new SimpleDateFormat("hh:mm:ss a").format(new Date()));
        }
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, UITheme.PRIMARY_DARK,
                        getWidth(), 0, new Color(8, 80, 72));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                // Bottom subtle line
                g2d.setColor(new Color(255, 255, 255, 10));
                g2d.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2d.dispose();
            }
        };
        topBar.setPreferredSize(new Dimension(0, 64));
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JLabel titleLabel = new JLabel("📋  TestNova");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        rightPanel.setOpaque(false);

        // Live clock
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        clockLabel.setForeground(new Color(180, 230, 220));
        rightPanel.add(clockLabel);

        // User info
        JLabel userLabel = new JLabel("👤 " + currentUser.getFullName());
        userLabel.setFont(UITheme.FONT_BODY_BOLD);
        userLabel.setForeground(UITheme.TEXT_PRIMARY);
        rightPanel.add(userLabel);

        // Change password
        JButton changePwdBtn = UITheme.createGhostButton("🔑 Password");
        changePwdBtn.setPreferredSize(new Dimension(110, 34));
        changePwdBtn.addActionListener(e -> showChangePasswordDialog());
        rightPanel.add(changePwdBtn);

        // Logout
        JButton logoutBtn = UITheme.createDangerButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(100, 34));
        logoutBtn.addActionListener(e -> {
            clockTimer.stop();
            dispose();
            new LoginScreen().setVisible(true);
        });
        rightPanel.add(logoutBtn);

        topBar.add(rightPanel, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.BG_DARK);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        String[][] menuItems = {
                { "🏠", "Overview", "OVERVIEW" },
                { "📝", "Manage Exams", "EXAMS" },
                { "❓", "Questions", "QUESTIONS" },
                { "📊", "Results", "RESULTS" }
        };

        ButtonGroup menuGroup = new ButtonGroup();
        boolean first = true;

        for (String[] item : menuItems) {
            JToggleButton menuBtn = createMenuButton(item[0] + "  " + item[1]);
            String panelName = item[2];
            menuBtn.addActionListener(e -> {
                cardLayout.show(contentPanel, panelName);
                selectedMenuBtn = menuBtn;
            });
            menuGroup.add(menuBtn);
            sidebar.add(menuBtn);
            sidebar.add(Box.createVerticalStrut(6));

            if (first) {
                menuBtn.setSelected(true);
                selectedMenuBtn = menuBtn;
                first = false;
            }
        }

        sidebar.add(Box.createVerticalGlue());

        // Sidebar footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(50, 50, 70));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        footerPanel.add(sep);
        footerPanel.add(Box.createVerticalStrut(12));

        JLabel versionLabel = new JLabel("TestNova v2.0");
        versionLabel.setFont(UITheme.FONT_SMALL);
        versionLabel.setForeground(UITheme.TEXT_MUTED);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerPanel.add(versionLabel);

        JLabel roleLabel = new JLabel("Teacher Panel");
        roleLabel.setFont(UITheme.FONT_SMALL);
        roleLabel.setForeground(UITheme.PRIMARY);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerPanel.add(roleLabel);

        sidebar.add(footerPanel);
        return sidebar;
    }

    private JToggleButton createMenuButton(String text) {
        JToggleButton btn = new JToggleButton(text) {
            private float hoverProgress = 0;
            private Timer hoverTimer;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (hoverTimer != null)
                            hoverTimer.stop();
                        hoverTimer = new Timer(16, ev -> {
                            hoverProgress = Math.min(1, hoverProgress + 0.12f);
                            repaint();
                            if (hoverProgress >= 1)
                                ((Timer) ev.getSource()).stop();
                        });
                        hoverTimer.start();
                    }

                    public void mouseExited(MouseEvent e) {
                        if (hoverTimer != null)
                            hoverTimer.stop();
                        hoverTimer = new Timer(16, ev -> {
                            hoverProgress = Math.max(0, hoverProgress - 0.12f);
                            repaint();
                            if (hoverProgress <= 0)
                                ((Timer) ev.getSource()).stop();
                        });
                        hoverTimer.start();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isSelected()) {
                    // Selected glow
                    UITheme.paintGlow(g2d, 0, 0, getWidth(), getHeight(), UITheme.PRIMARY, 4);
                    g2d.setColor(new Color(UITheme.PRIMARY_DARK.getRed(), UITheme.PRIMARY_DARK.getGreen(),
                            UITheme.PRIMARY_DARK.getBlue(), 180));
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                    // Active indicator
                    g2d.setColor(UITheme.PRIMARY_LIGHT);
                    g2d.fill(new RoundRectangle2D.Float(0, 6, 4, getHeight() - 12, 4, 4));
                } else if (hoverProgress > 0) {
                    g2d.setColor(new Color(UITheme.BG_LIGHT.getRed(), UITheme.BG_LIGHT.getGreen(),
                            UITheme.BG_LIGHT.getBlue(), (int) (180 * hoverProgress)));
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                }

                g2d.setColor(isSelected() ? UITheme.TEXT_PRIMARY : UITheme.TEXT_SECONDARY);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), 18, y);
                g2d.dispose();
            }
        };
        btn.setFont(UITheme.FONT_BODY_BOLD);
        btn.setForeground(UITheme.TEXT_SECONDARY);
        btn.setPreferredSize(new Dimension(206, 48));
        btn.setMaximumSize(new Dimension(206, 48));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(UITheme.BG_MEDIUM);
        panel.setForeground(UITheme.TEXT_PRIMARY);

        JLabel oldLabel = new JLabel("Current Password:");
        oldLabel.setForeground(UITheme.TEXT_PRIMARY);
        JPasswordField oldField = new JPasswordField();

        JLabel newLabel = new JLabel("New Password:");
        newLabel.setForeground(UITheme.TEXT_PRIMARY);
        JPasswordField newField = new JPasswordField();

        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setForeground(UITheme.TEXT_PRIMARY);
        JPasswordField confirmField = new JPasswordField();

        panel.add(oldLabel);
        panel.add(oldField);
        panel.add(newLabel);
        panel.add(newField);
        panel.add(confirmLabel);
        panel.add(confirmField);

        int result = JOptionPane.showConfirmDialog(this, panel, "🔑 Change Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String oldPwd = new String(oldField.getPassword());
            String newPwd = new String(newField.getPassword());
            String confirmPwd = new String(confirmField.getPassword());

            if (!oldPwd.equals(currentUser.getPassword())) {
                UITheme.showError(this, "Current password is incorrect.");
                return;
            }
            if (newPwd.length() < 4) {
                UITheme.showError(this, "New password must be at least 4 characters.");
                return;
            }
            if (!newPwd.equals(confirmPwd)) {
                UITheme.showError(this, "New passwords do not match.");
                return;
            }

            UserDAO userDAO = new UserDAO();
            if (userDAO.changePassword(currentUser.getId(), newPwd)) {
                currentUser.setPassword(newPwd);
                UITheme.showSuccess(this, "Password changed successfully!");
            } else {
                UITheme.showError(this, "Failed to change password.");
            }
        }
    }
}
