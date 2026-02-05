package lk.group40.frdv.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final String loggedInUser;

    public MainFrame(String loggedInUser) {
        super("Furniture Room Design Visualizer");
        this.loggedInUser = loggedInUser;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());

        // Top bar
        root.add(buildTopBar(), BorderLayout.NORTH);

        // Main area
        root.add(buildMainArea(), BorderLayout.CENTER);

        return root;
    }

    private JComponent buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("Furniture Room Design Visualizer");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JLabel userLabel = new JLabel("Logged in as: " + loggedInUser);
        userLabel.setFont(userLabel.getFont().deriveFont(13f));

        bar.add(title, BorderLayout.WEST);
        bar.add(userLabel, BorderLayout.EAST);

        return bar;
    }

    private JComponent buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());

        // Left panel (Furniture)
        JPanel left = new JPanel();
        left.setPreferredSize(new Dimension(200, 0));
        left.setBorder(BorderFactory.createTitledBorder("Furniture"));
        left.add(new JLabel("Furniture list"));

        // Center panel (Canvas)
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Design Canvas"));

        lk.group40.frdv.ui.canvas.DesignCanvas2D canvas2D =
                new lk.group40.frdv.ui.canvas.DesignCanvas2D();

        center.add(canvas2D, BorderLayout.CENTER);


        // Right panel (Properties)
        JPanel right = new JPanel();
        right.setPreferredSize(new Dimension(250, 0));
        right.setBorder(BorderFactory.createTitledBorder("Properties"));
        right.add(new JLabel("Selected item properties"));

        main.add(left, BorderLayout.WEST);
        main.add(center, BorderLayout.CENTER);
        main.add(right, BorderLayout.EAST);

        return main;
    }
}
