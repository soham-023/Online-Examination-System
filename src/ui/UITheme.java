package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.FileWriter;
import java.io.IOException;

public class UITheme {

    // ========== COLOR PALETTE ==========
    // Primary - Teal / Emerald
    public static final Color PRIMARY = new Color(13, 148, 136);
    public static final Color PRIMARY_DARK = new Color(5, 102, 93);
    public static final Color PRIMARY_LIGHT = new Color(20, 184, 166);
    public static final Color PRIMARY_GLOW = new Color(13, 148, 136, 60);

    // Accent - Coral / Warm Orange
    public static final Color ACCENT = new Color(249, 115, 22);
    public static final Color ACCENT_LIGHT = new Color(251, 146, 60);
    public static final Color ACCENT_DARK = new Color(234, 88, 12);
    public static final Color ACCENT_GLOW = new Color(249, 115, 22, 50);

    // Background - Dark Charcoal
    public static final Color BG_DARK = new Color(15, 15, 25);
    public static final Color BG_MEDIUM = new Color(22, 22, 35);
    public static final Color BG_LIGHT = new Color(35, 35, 52);
    public static final Color BG_CARD = new Color(42, 42, 62);
    public static final Color BG_HOVER = new Color(50, 50, 72);
    public static final Color BG_GLASS = new Color(255, 255, 255, 8);

    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(240, 240, 235);
    public static final Color TEXT_SECONDARY = new Color(170, 170, 185);
    public static final Color TEXT_MUTED = new Color(110, 110, 130);

    // Status Colors
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color SUCCESS_GLOW = new Color(34, 197, 94, 40);
    public static final Color CLR_ERROR = new Color(239, 68, 68);
    public static final Color CLR_ERROR_DARK = new Color(167, 47, 47);
    public static final Color CLR_ERROR_LIGHT = new Color(255, 97, 97);
    public static final Color WARNING = new Color(250, 204, 21);
    public static final Color INFO = new Color(56, 189, 248);

    // Grade Colors
    public static final Color GRADE_A = new Color(34, 197, 94);
    public static final Color GRADE_B = new Color(56, 189, 248);
    public static final Color GRADE_C = new Color(250, 204, 21);
    public static final Color GRADE_D = new Color(249, 115, 22);
    public static final Color GRADE_F = new Color(239, 68, 68);

    // ========== FONTS ==========
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TIMER = new Font("Consolas", Font.BOLD, 32);
    public static final Font FONT_STAT_VALUE = new Font("Segoe UI", Font.BOLD, 36);
    public static final Font FONT_STAT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);

    // ========== SHADOWS ==========
    public static void paintShadow(Graphics2D g2d, int x, int y, int w, int h, int radius, int spread) {
        for (int i = spread; i > 0; i--) {
            float alpha = 0.08f * (1.0f - ((float) i / spread));
            g2d.setColor(new Color(0, 0, 0, (int) (alpha * 255)));
            g2d.fill(new RoundRectangle2D.Float(x - i, y - i, w + 2 * i, h + 2 * i, radius + i, radius + i));
        }
    }

    public static void paintGlow(Graphics2D g2d, int x, int y, int w, int h, Color glowColor, int spread) {
        for (int i = spread; i > 0; i--) {
            float alpha = 0.15f * (1.0f - ((float) i / spread));
            g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                    (int) (alpha * 255)));
            g2d.fill(new RoundRectangle2D.Float(x - i, y - i, w + 2 * i, h + 2 * i, 16 + i, 16 + i));
        }
    }

    // ========== GRADIENT PAINTING ==========
    public static void paintGradientBackground(Graphics2D g2d, int w, int h) {
        GradientPaint gp = new GradientPaint(0, 0, BG_DARK, w, h, new Color(10, 25, 30));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
    }

    // ========== FACTORY METHODS ==========

    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text) {
            private float hoverProgress = 0;
            private Timer hoverTimer;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (hoverTimer != null)
                            hoverTimer.stop();
                        hoverTimer = new Timer(16, ev -> {
                            hoverProgress = Math.min(1, hoverProgress + 0.1f);
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
                            hoverProgress = Math.max(0, hoverProgress - 0.1f);
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

                if (hoverProgress > 0) {
                    paintGlow(g2d, 0, 0, getWidth(), getHeight(), PRIMARY, (int) (8 * hoverProgress));
                }

                Color bgColor;
                if (getModel().isPressed()) {
                    bgColor = PRIMARY_DARK;
                } else {
                    int r = (int) (PRIMARY.getRed() + (PRIMARY_LIGHT.getRed() - PRIMARY.getRed()) * hoverProgress);
                    int gv = (int) (PRIMARY.getGreen()
                            + (PRIMARY_LIGHT.getGreen() - PRIMARY.getGreen()) * hoverProgress);
                    int b = (int) (PRIMARY.getBlue() + (PRIMARY_LIGHT.getBlue() - PRIMARY.getBlue()) * hoverProgress);
                    bgColor = new Color(r, gv, b);
                }
                g2d.setColor(bgColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));

                g2d.setColor(TEXT_PRIMARY);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        button.setFont(FONT_BUTTON);
        button.setForeground(TEXT_PRIMARY);
        button.setPreferredSize(new Dimension(160, 44));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton createAccentButton(String text) {
        JButton button = new JButton(text) {
            private float hoverProgress = 0;
            private Timer hoverTimer;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (hoverTimer != null)
                            hoverTimer.stop();
                        hoverTimer = new Timer(16, ev -> {
                            hoverProgress = Math.min(1, hoverProgress + 0.1f);
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
                            hoverProgress = Math.max(0, hoverProgress - 0.1f);
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

                if (hoverProgress > 0) {
                    paintGlow(g2d, 0, 0, getWidth(), getHeight(), ACCENT, (int) (8 * hoverProgress));
                }

                Color bgColor;
                if (getModel().isPressed()) {
                    bgColor = ACCENT_DARK;
                } else {
                    int r = (int) (ACCENT.getRed() + (ACCENT_LIGHT.getRed() - ACCENT.getRed()) * hoverProgress);
                    int gv = (int) (ACCENT.getGreen() + (ACCENT_LIGHT.getGreen() - ACCENT.getGreen()) * hoverProgress);
                    int b = (int) (ACCENT.getBlue() + (ACCENT_LIGHT.getBlue() - ACCENT.getBlue()) * hoverProgress);
                    bgColor = new Color(r, gv, b);
                }
                g2d.setColor(bgColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));

                g2d.setColor(TEXT_PRIMARY);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        button.setFont(FONT_BUTTON);
        button.setForeground(TEXT_PRIMARY);
        button.setPreferredSize(new Dimension(160, 44));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(CLR_ERROR_DARK);
                } else if (getModel().isRollover()) {
                    g2d.setColor(CLR_ERROR_LIGHT);
                } else {
                    g2d.setColor(CLR_ERROR);
                }
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));

                g2d.setColor(TEXT_PRIMARY);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        button.setFont(FONT_BUTTON);
        button.setForeground(TEXT_PRIMARY);
        button.setPreferredSize(new Dimension(160, 44));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton createOutlineButton(String text) {
        JButton button = new JButton(text) {
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

                if (hoverProgress > 0) {
                    g2d.setColor(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(),
                            (int) (40 * hoverProgress)));
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                }

                g2d.setColor(PRIMARY);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 14, 14));

                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        button.setFont(FONT_BUTTON);
        button.setForeground(PRIMARY);
        button.setPreferredSize(new Dimension(160, 44));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton createGhostButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2d.setColor(BG_HOVER);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                }

                g2d.setColor(getModel().isRollover() ? TEXT_PRIMARY : TEXT_SECONDARY);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        button.setFont(FONT_BODY);
        button.setForeground(TEXT_SECONDARY);
        button.setPreferredSize(new Dimension(120, 36));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // ========== INPUT FIELDS ==========

    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField() {
            private boolean focused = false;
            private float focusProgress = 0;
            private Timer focusTimer;
            {
                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        focused = true;
                        animateFocus(true);
                    }

                    public void focusLost(FocusEvent e) {
                        focused = false;
                        animateFocus(false);
                    }
                });
            }

            private void animateFocus(boolean in) {
                if (focusTimer != null)
                    focusTimer.stop();
                focusTimer = new Timer(16, ev -> {
                    focusProgress = in ? Math.min(1, focusProgress + 0.12f) : Math.max(0, focusProgress - 0.12f);
                    repaint();
                    if ((in && focusProgress >= 1) || (!in && focusProgress <= 0))
                        ((Timer) ev.getSource()).stop();
                });
                focusTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2d.setColor(BG_LIGHT);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

                // Focus border glow
                if (focusProgress > 0) {
                    g2d.setColor(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(),
                            (int) (80 * focusProgress)));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
                } else {
                    g2d.setColor(BG_CARD);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
                }
                g2d.dispose();

                super.paintComponent(g);

                // Placeholder
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) getGraphics().create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(FONT_BODY);
                    Insets insets = getInsets();
                    g2.drawString(placeholder, insets.left + 4,
                            getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_LIGHT);
        field.setCaretColor(PRIMARY);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(10, 16, 10, 16));
        field.setPreferredSize(new Dimension(300, 44));
        return field;
    }

    public static JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            private float focusProgress = 0;
            private Timer focusTimer;
            {
                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        animateFocus(true);
                    }

                    public void focusLost(FocusEvent e) {
                        animateFocus(false);
                    }
                });
            }

            private void animateFocus(boolean in) {
                if (focusTimer != null)
                    focusTimer.stop();
                focusTimer = new Timer(16, ev -> {
                    focusProgress = in ? Math.min(1, focusProgress + 0.12f) : Math.max(0, focusProgress - 0.12f);
                    repaint();
                    if ((in && focusProgress >= 1) || (!in && focusProgress <= 0))
                        ((Timer) ev.getSource()).stop();
                });
                focusTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BG_LIGHT);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                if (focusProgress > 0) {
                    g2d.setColor(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(),
                            (int) (80 * focusProgress)));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
                } else {
                    g2d.setColor(BG_CARD);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
                }
                g2d.dispose();
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) getGraphics().create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(FONT_BODY);
                    Insets insets = getInsets();
                    g2.drawString(placeholder, insets.left + 4,
                            getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_LIGHT);
        field.setCaretColor(PRIMARY);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(10, 16, 10, 16));
        field.setPreferredSize(new Dimension(300, 44));
        return field;
    }

    public static JTextField createSearchField(String placeholder) {
        JTextField field = createTextField("🔍  " + placeholder);
        field.setPreferredSize(new Dimension(250, 38));
        return field;
    }

    public static JTextArea createTextArea(String placeholder, int rows) {
        JTextArea area = new JTextArea(rows, 30) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(TEXT_MUTED);
                    g2d.setFont(FONT_BODY);
                    Insets insets = getInsets();
                    g2d.drawString(placeholder, insets.left + 4, insets.top + g2d.getFontMetrics().getAscent());
                    g2d.dispose();
                }
            }
        };
        area.setFont(FONT_BODY);
        area.setForeground(TEXT_PRIMARY);
        area.setBackground(BG_LIGHT);
        area.setCaretColor(PRIMARY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new CompoundBorder(
                new LineBorder(BG_CARD, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        return area;
    }

    // ========== LABELS ==========

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    public static JLabel createHeadingLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADING);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    // ========== STAT CARD ==========

    public static JPanel createStatCard(String icon, String value, String label, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                paintShadow(g2d, 0, 2, getWidth(), getHeight(), 16, 8);
                g2d.setColor(BG_LIGHT);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                // Top accent bar
                g2d.setColor(accentColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 4, 4, 4));
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 20, 14, 20));
        card.setPreferredSize(new Dimension(200, 110));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        card.add(iconLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(FONT_STAT_VALUE);
        valueLabel.setForeground(TEXT_PRIMARY);
        card.add(valueLabel, BorderLayout.CENTER);

        JLabel descLabel = new JLabel(label);
        descLabel.setFont(FONT_STAT_LABEL);
        descLabel.setForeground(TEXT_MUTED);
        card.add(descLabel, BorderLayout.SOUTH);

        return card;
    }

    // ========== PANELS ==========

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                paintShadow(g2d, 0, 2, getWidth(), getHeight(), 16, 6);
                g2d.setColor(BG_LIGHT);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return panel;
    }

    public static JPanel createGlassPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                paintShadow(g2d, 0, 4, getWidth(), getHeight(), 20, 12);
                // Glass background
                g2d.setColor(new Color(30, 30, 48, 200));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                // Glass border
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 20, 20));
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        return panel;
    }

    // ========== TABLE STYLING ==========

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_LIGHT);
        table.setGridColor(new Color(50, 50, 70));
        table.setSelectionBackground(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 60));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setBorder(null);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setForeground(PRIMARY_LIGHT);
        header.setBackground(BG_MEDIUM);
        header.setBorder(new LineBorder(new Color(50, 50, 70)));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 48));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setBackground(BG_MEDIUM);
        headerRenderer.setForeground(PRIMARY_LIGHT);
        headerRenderer.setFont(FONT_BODY_BOLD);
        headerRenderer.setBorder(new EmptyBorder(0, 10, 0, 10));
        header.setDefaultRenderer(headerRenderer);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BG_LIGHT : new Color(28, 28, 42));
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return c;
            }
        };
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    // ========== SCROLL PANE ==========

    public static JScrollPane createScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BG_MEDIUM);
        scrollPane.getViewport().setBackground(BG_MEDIUM);

        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = BG_CARD;
                this.trackColor = BG_MEDIUM;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                return btn;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BG_CARD);
                g2d.fill(new RoundRectangle2D.Float(thumbBounds.x + 2, thumbBounds.y,
                        thumbBounds.width - 4, thumbBounds.height, 8, 8));
                g2d.dispose();
            }
        });
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        return scrollPane;
    }

    // ========== COMBO BOX ==========

    public static JComboBox<String> createComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(FONT_BODY);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(BG_LIGHT);
        combo.setBorder(new CompoundBorder(
                new LineBorder(BG_CARD, 1, true),
                new EmptyBorder(4, 8, 4, 8)));
        combo.setPreferredSize(new Dimension(300, 44));
        return combo;
    }

    // ========== DIALOGS ==========

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "✅ Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "❌ Error", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "⚠️ Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    // ========== PROGRESS BAR ==========

    public static JPanel createProgressBar(double progress, Color color) {
        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Track
                g2d.setColor(BG_CARD);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                // Fill
                int fillWidth = (int) (getWidth() * Math.min(1, progress));
                if (fillWidth > 0) {
                    GradientPaint gp = new GradientPaint(0, 0, color,
                            fillWidth, 0, new Color(
                            Math.min(255, color.getRed() + 30),
                            Math.min(255, color.getGreen() + 30),
                            Math.min(255, color.getBlue() + 30)));
                    g2d.setPaint(gp);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, fillWidth, getHeight(), 8, 8));
                }
                g2d.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(200, 8));
        return bar;
    }

    // ========== GRADE HELPERS ==========

    public static String getGrade(double percentage) {
        if (percentage >= 90)
            return "A+";
        if (percentage >= 80)
            return "A";
        if (percentage >= 70)
            return "B";
        if (percentage >= 60)
            return "C";
        if (percentage >= 50)
            return "D";
        return "F";
    }

    public static Color getGradeColor(double percentage) {
        if (percentage >= 80)
            return GRADE_A;
        if (percentage >= 70)
            return GRADE_B;
        if (percentage >= 60)
            return GRADE_C;
        if (percentage >= 50)
            return GRADE_D;
        return GRADE_F;
    }

    // ========== CSV EXPORT ==========

    public static void exportTableToCSV(JTable table, Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Results to CSV");
        chooser.setSelectedFile(new java.io.File("results_export.csv"));
        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                // Header
                for (int i = 0; i < table.getColumnCount(); i++) {
                    fw.write(table.getColumnName(i));
                    if (i < table.getColumnCount() - 1)
                        fw.write(",");
                }
                fw.write("\n");
                // Rows
                for (int r = 0; r < table.getRowCount(); r++) {
                    for (int c = 0; c < table.getColumnCount(); c++) {
                        Object val = table.getValueAt(r, c);
                        fw.write(val != null ? "\"" + val.toString().replace("\"", "\"\"") + "\"" : "");
                        if (c < table.getColumnCount() - 1)
                            fw.write(",");
                    }
                    fw.write("\n");
                }
                showSuccess(parent,
                        "Results exported successfully to:\n" + chooser.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                showError(parent, "Failed to export: " + ex.getMessage());
            }
        }
    }
}
