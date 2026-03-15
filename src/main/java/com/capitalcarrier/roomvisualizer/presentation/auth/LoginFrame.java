package com.capitalcarrier.roomvisualizer.presentation.auth;

import com.capitalcarrier.roomvisualizer.application.auth.AuthService;
import com.capitalcarrier.roomvisualizer.domain.model.User;
import com.capitalcarrier.roomvisualizer.presentation.components.*;
import com.capitalcarrier.roomvisualizer.presentation.dashboard.DashboardFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Room Visualizer - Login");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel background = new BackgroundPanel();
        background.setLayout(new GridBagLayout());
        setContentPane(background);

        // Floating Card
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                // Subtle shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(460, 580));
        card.setMaximumSize(new Dimension(460, 580));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(45, 50, 45, 50));

        // Header
        JLabel headerLabel = new JLabel("Sign in Account");
        headerLabel.setFont(new Font("Inter", Font.BOLD, 26));
        headerLabel.setForeground(new Color(30, 35, 45));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subHeaderLabel = new JLabel("Sign in to start shopping");
        subHeaderLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        subHeaderLabel.setForeground(new Color(130, 135, 145));
        subHeaderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        FormInput emailField = new FormInput("Enter your email", FormInput.IconType.ENVELOPE);
        FormPasswordInput passwordField = new FormPasswordInput("Password");

        PrimaryButton signInButton = new PrimaryButton("Sign in Account");
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signInButton.addActionListener(e -> {
            try {
                User user = AuthService.loginLocalUser(emailField.getText(), new String(passwordField.getPassword()));
                openDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        FormDivider divider = new FormDivider("or continue with");
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton googleButton = createGoogleButton();
        googleButton.addActionListener(e -> {
            try {
                User user = AuthService.loginWithGoogle();
                openDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not sign in with Google: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        bottomPanel.setOpaque(false);
        JLabel noAccountLabel = new JLabel("Already have an account?");
        noAccountLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        noAccountLabel.setForeground(new Color(130, 135, 145));
        
        JLabel createAccountLink = new JLabel("Create Account");
        createAccountLink.setFont(new Font("Inter", Font.BOLD, 13));
        createAccountLink.setForeground(new Color(175, 45, 255));
        createAccountLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccountLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegisterFrame().setVisible(true);
                dispose();
            }
        });
        bottomPanel.add(noAccountLabel);
        bottomPanel.add(createAccountLink);

        // Building Form
        card.add(headerLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(subHeaderLabel);
        card.add(Box.createVerticalStrut(35));
        card.add(createFieldWrapper("Email Address", emailField));
        card.add(Box.createVerticalStrut(15));
        card.add(createFieldWrapper("Password", passwordField));
        card.add(Box.createVerticalStrut(25));
        card.add(signInButton);
        card.add(Box.createVerticalStrut(25));
        card.add(divider);
        card.add(Box.createVerticalStrut(20));
        card.add(googleButton);
        card.add(Box.createVerticalGlue());
        card.add(bottomPanel);

        background.add(card);
    }

    private JPanel createFieldWrapper(String labelText, JComponent field) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Inter", Font.BOLD, 12));
        label.setForeground(new Color(60, 65, 75));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setPreferredSize(new Dimension(360, 48));
        field.setMaximumSize(new Dimension(360, 48));
        wrapper.add(label);
        wrapper.add(Box.createVerticalStrut(6));
        wrapper.add(field);
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        return wrapper;
    }

    private JButton createGoogleButton() {
        JButton btn = new JButton("Sign in with Google") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(220, 220, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Inter", Font.BOLD, 14));
        btn.setForeground(new Color(60, 65, 75));
        btn.setPreferredSize(new Dimension(360, 48));
        btn.setMaximumSize(new Dimension(360, 48));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBorder(new EmptyBorder(0, 0, 0, 0));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setIcon(new ImageIcon(createGoogleLogo()));
        btn.setIconTextGap(10);
        return btn;
    }

    private Image createGoogleLogo() {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(20, 20, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Colors from Google logo
        g2.setColor(new Color(234, 67, 53)); // Red
        g2.drawArc(2, 2, 16, 16, 60, 120);
        g2.setColor(new Color(251, 188, 5)); // Yellow
        g2.drawArc(2, 2, 16, 16, 180, 60);
        g2.setColor(new Color(52, 168, 83)); // Green
        g2.drawArc(2, 2, 16, 16, 240, 80);
        g2.setColor(new Color(66, 133, 244)); // Blue
        g2.drawArc(2, 2, 16, 16, 280, 80);
        g2.drawLine(10, 10, 18, 10);
        g2.dispose();
        return img;
    }

    private void openDashboard() {
        SwingUtilities.invokeLater(() -> {
            try {
                new DashboardFrame().setVisible(true);
                dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Deep Navy Right Side
            g2.setColor(new Color(15, 25, 60));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Bright Purple Left Side - Using a large arc/circle from the left as seen in mockup
            g2.setColor(new Color(153, 51, 255));
            g2.fillOval((int)(-getWidth() * 0.4), (int)(-getHeight() * 0.1), (int)(getWidth() * 1.1), (int)(getHeight() * 1.2));
            
            // Draw a second subtle circle for depth like the mockup
            g2.setColor(new Color(255, 255, 255, 15));
            g2.fillOval((int)(-getWidth() * 0.1), (int)(-getHeight() * 0.2), (int)(getWidth() * 0.6), (int)(getWidth() * 0.6));

            // Title Text - Moved HIGHER up so it acts as a header
            g2.setColor(Color.WHITE);
            // Using a nice Serif font with a subtle shadow
            g2.setFont(new Font("Serif", Font.BOLD, 42));
            String title = "Furniture Room Design Visualizers";
            FontMetrics fm = g2.getFontMetrics();
            int titleX = (getWidth() - fm.stringWidth(title)) / 2;
            int titleY = 100; // Positioned at the top
            
            // Draw shadow for title
            g2.setColor(new Color(0, 0, 0, 80));
            g2.drawString(title, titleX + 2, titleY + 2);
            g2.setColor(Color.WHITE);
            g2.drawString(title, titleX, titleY);

            // Subtitle
            g2.setFont(new Font("Inter", Font.PLAIN, 15));
            g2.setColor(new Color(255, 255, 255, 180));
            String sub = "Professional Interior Room Designer";
            fm = g2.getFontMetrics();
            g2.drawString(sub, (getWidth() - fm.stringWidth(sub)) / 2, titleY + 30);

            g2.dispose();
        }
    }
}
