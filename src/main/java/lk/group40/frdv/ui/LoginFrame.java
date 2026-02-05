package lk.group40.frdv.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public LoginFrame() {
        super("Furniture Room Design Visualizer - Login");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);
        setResizable(false);

        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Furniture Room Design Visualizer");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JLabel subtitle = new JLabel("Designer Login");
        subtitle.setForeground(new Color(90, 90, 90));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 0, 8, 0);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        gc.gridx = 0; gc.gridy = 0;
        form.add(new JLabel("Email"), gc);

        gc.gridy = 1;
        form.add(emailField, gc);

        gc.gridy = 2;
        form.add(new JLabel("Password"), gc);

        gc.gridy = 3;
        form.add(passwordField, gc);

        JLabel forgot = new JLabel("<html><a href='#'>Forgot Password?</a></html>");
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Password reset is simulated in the prototype.\n" +
                        "In implementation, this can be handled by admin support.",
                        "Forgot Password",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> onLogin());

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.add(forgot, BorderLayout.WEST);
        actions.add(loginBtn, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        return root;
    }

    private void onLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both email and password.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // MVP: Simulated login success (matches HCI prototype stage)
        JOptionPane.showMessageDialog(this,
                "Login successful (simulated)\nUser: " + email,
                "Welcome",
                JOptionPane.INFORMATION_MESSAGE);

        // Next step: open MainFrame and show logged-in user
        // (we’ll implement MainFrame next)
    }
}
