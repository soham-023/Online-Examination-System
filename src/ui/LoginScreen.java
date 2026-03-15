package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private UserDAO userDAO;

    // Particle system for animated background
    private java.util.List<float[]> particles = new ArrayList<>();
    private Timer animTimer;
    private float gradientPhase = 0;

    public LoginScreen() {
        userDAO = new UserDAO();
        initParticles();
        initializeUI();
    }

    private void initParticles() {
        Random rand = new Random();
        for (int i = 0; i < 40; i++) {
            particles.add(new float[] {
                    rand.nextFloat() * 1200, rand.nextFloat() * 800, // x, y
                    rand.nextFloat() * 2 - 1, rand.nextFloat() * 2 - 1, // vx, vy
                    rand.nextFloat() * 3 + 1, // size
                    rand.nextFloat() * 0.3f + 0.05f // alpha
            });
        }
    }

    private void initializeUI() {
        setTitle("TestNova — Online Examination System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        // Main animated panel
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();

                // Animated gradient background
                float phase = gradientPhase;
                Color c1 = new Color(8, 12, 20);
                Color c2 = new Color(
                        (int) (10 + 8 * Math.sin(phase)),
                        (int) (20 + 15 * Math.sin(phase + 1)),
                        (int) (35 + 10 * Math.sin(phase + 2)));
                GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);

                // Subtle gradient orbs
                float orbX = (float) (w * 0.3 + 80 * Math.sin(phase * 0.7));
                float orbY = (float) (h * 0.4 + 60 * Math.cos(phase * 0.5));
                RadialGradientPaint orb1 = new RadialGradientPaint(
                        orbX, orbY, 250,
                        new float[] { 0, 0.5f, 1 },
                        new Color[] {
                                new Color(13, 148, 136, 25),
                                new Color(13, 148, 136, 8),
                                new Color(0, 0, 0, 0) });
                g2d.setPaint(orb1);
                g2d.fillRect(0, 0, w, h);

                float orb2X = (float) (w * 0.7 + 60 * Math.cos(phase * 0.6));
                float orb2Y = (float) (h * 0.6 + 80 * Math.sin(phase * 0.4));
                RadialGradientPaint orb2 = new RadialGradientPaint(
                        orb2X, orb2Y, 200,
                        new float[] { 0, 0.5f, 1 },
                        new Color[] {
                                new Color(249, 115, 22, 18),
                                new Color(249, 115, 22, 5),
                                new Color(0, 0, 0, 0) });
                g2d.setPaint(orb2);
                g2d.fillRect(0, 0, w, h);

                // Particles
                for (float[] p : particles) {
                    g2d.setColor(new Color(200, 220, 235, (int) (p[5] * 255)));
                    g2d.fill(new Ellipse2D.Float(p[0], p[1], p[4], p[4]));
                }

                g2d.dispose();
            }
        };

        // Animation timer
        animTimer = new Timer(30, e -> {
            gradientPhase += 0.02f;
            for (float[] p : particles) {
                p[0] += p[2] * 0.5f;
                p[1] += p[3] * 0.5f;
                if (p[0] < -10)
                    p[0] = mainPanel.getWidth() + 10;
                if (p[0] > mainPanel.getWidth() + 10)
                    p[0] = -10;
                if (p[1] < -10)
                    p[1] = mainPanel.getHeight() + 10;
                if (p[1] > mainPanel.getHeight() + 10)
                    p[1] = -10;
            }
            mainPanel.repaint();
        });
        animTimer.start();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Split: branding left, form right
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        splitPanel.setOpaque(false);
        splitPanel.setPreferredSize(new Dimension(900, 550));

        splitPanel.add(createBrandingPanel());
        splitPanel.add(createLoginFormPanel());

        mainPanel.add(splitPanel, gbc);
        setContentPane(mainPanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                animTimer.stop();
            }
        });
    }

    private JPanel createBrandingPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Transparent — shows the animated background through
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 20));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // App logo
        JLabel logoLabel = new JLabel() {
            private BufferedImage logoImg;
            {
                try {
                    String basePath = System.getProperty("user.dir");
                    logoImg = ImageIO.read(new File(basePath + "/src/resources/testnova_logo.png"));
                } catch (Exception ex) {
                    // fallback if logo not found
                }
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                // Glow behind logo
                g2d.setColor(new Color(13, 148, 136, 30));
                g2d.fill(new Ellipse2D.Float(5, 5, 70, 70));
                if (logoImg != null) {
                    g2d.drawImage(logoImg, 0, 0, 80, 80, null);
                }
                g2d.dispose();
            }
            @Override
            public Dimension getPreferredSize() { return new Dimension(80, 80); }
            @Override
            public Dimension getMinimumSize() { return getPreferredSize(); }
        };
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(logoLabel);
        content.add(Box.createVerticalStrut(20));

        JLabel titleLabel = new JLabel("TestNova");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(8));

        JLabel tagline = new JLabel("Online Examination System");
        tagline.setFont(UITheme.FONT_SUBTITLE);
        tagline.setForeground(UITheme.PRIMARY_LIGHT);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(tagline);
        content.add(Box.createVerticalStrut(30));

        // Feature highlights
        String[][] features = {
                { "⚡", "Fast & Secure", "Timed exams with auto-submit" },
                { "📊", "Instant Results", "Scores & analytics right away" },
                { "🎯", "Easy Management", "Create exams in minutes" }
        };

        for (String[] feat : features) {
            JPanel featPanel = new JPanel(new BorderLayout(12, 0));
            featPanel.setOpaque(false);
            featPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            featPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            featPanel.setMaximumSize(new Dimension(350, 50));

            JLabel fi = new JLabel(feat[0]);
            fi.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            fi.setPreferredSize(new Dimension(36, 36));
            featPanel.add(fi, BorderLayout.WEST);

            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.setOpaque(false);
            JLabel ft = new JLabel(feat[1]);
            ft.setFont(UITheme.FONT_BODY_BOLD);
            ft.setForeground(UITheme.TEXT_PRIMARY);
            textPanel.add(ft);
            JLabel fd = new JLabel(feat[2]);
            fd.setFont(UITheme.FONT_SMALL);
            fd.setForeground(UITheme.TEXT_MUTED);
            textPanel.add(fd);
            featPanel.add(textPanel, BorderLayout.CENTER);

            content.add(featPanel);
        }

        panel.add(content);
        return panel;
    }

    private JPanel createLoginFormPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        // Glass card
        JPanel card = UITheme.createGlassPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(380, 460));

        JLabel formTitle = new JLabel("Welcome Back");
        formTitle.setFont(UITheme.FONT_SUBTITLE);
        formTitle.setForeground(UITheme.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(formTitle);

        JLabel formSubtitle = new JLabel("Sign in to continue");
        formSubtitle.setFont(UITheme.FONT_BODY);
        formSubtitle.setForeground(UITheme.TEXT_MUTED);
        formSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(formSubtitle);
        card.add(Box.createVerticalStrut(25));

        // Username
        JLabel userLabel = UITheme.createLabel("Username");
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(userLabel);
        card.add(Box.createVerticalStrut(6));

        usernameField = UITheme.createTextField("Enter your username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(usernameField);
        card.add(Box.createVerticalStrut(16));

        // Password
        JLabel passLabel = UITheme.createLabel("Password");
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));

        passwordField = UITheme.createPasswordField("Enter your password");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passwordField);
        card.add(Box.createVerticalStrut(16));

        // Role selector
        JLabel roleLabel = UITheme.createLabel("Login as");
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(roleLabel);
        card.add(Box.createVerticalStrut(6));

        roleCombo = UITheme.createComboBox();
        roleCombo.addItem("STUDENT");
        roleCombo.addItem("TEACHER");
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(roleCombo);
        card.add(Box.createVerticalStrut(25));

        // Login button
        JButton loginBtn = UITheme.createPrimaryButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(12));

        // Register button
        JButton registerBtn = UITheme.createOutlineButton("Create Account");
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.addActionListener(e -> handleRegister());
        card.add(registerBtn);

        // Enter key binding
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    handleLogin();
            }
        });

        wrapper.add(card);
        return wrapper;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            UITheme.showError(this, "Please fill in all fields.");
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            String selectedRole = (String) roleCombo.getSelectedItem();
            if (!user.getRole().equals(selectedRole)) {
                UITheme.showError(this, "You are not registered as a " + selectedRole + ".");
                return;
            }

            animTimer.stop();
            dispose();

            if (user.isTeacher()) {
                new AdminDashboard(user).setVisible(true);
            } else {
                new StudentDashboard(user).setVisible(true);
            }
        } else {
            UITheme.showError(this, "Invalid username or password.");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleCombo.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            UITheme.showError(this, "Please enter a username and password to register.");
            return;
        }

        if (username.length() < 3) {
            UITheme.showError(this, "Username must be at least 3 characters.");
            return;
        }

        if (password.length() < 4) {
            UITheme.showError(this, "Password must be at least 4 characters.");
            return;
        }

        if (userDAO.usernameExists(username)) {
            UITheme.showError(this, "Username '" + username + "' is already taken.");
            return;
        }

        String fullName = JOptionPane.showInputDialog(this,
                "Enter your full name:", "Registration",
                JOptionPane.PLAIN_MESSAGE);

        if (fullName == null || fullName.trim().isEmpty()) {
            UITheme.showError(this, "Full name is required.");
            return;
        }

        User newUser = new User(username, password, fullName.trim(), role);
        if (userDAO.registerUser(newUser)) {
            UITheme.showSuccess(this, "Registration successful! You can now sign in.");
        } else {
            UITheme.showError(this, "Registration failed. Please try again.");
        }
    }
}
