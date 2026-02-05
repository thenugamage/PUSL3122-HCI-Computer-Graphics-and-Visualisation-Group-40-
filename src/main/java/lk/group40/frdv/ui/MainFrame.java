package lk.group40.frdv.ui;

import lk.group40.frdv.ui.canvas.DesignCanvas2D;
import lk.group40.frdv.ui.canvas.Viewer3DPanel;
import lk.group40.frdv.ui.panels.FurniturePanel;
import lk.group40.frdv.ui.panels.PropertiesPanel;
import lk.group40.frdv.ui.panels.RoomConfigPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final String loggedInUser;

    // Shared canvas + views
    private DesignCanvas2D canvas2D;
    private Viewer3DPanel view3D;

    // Card switching
    private CardLayout cardLayout;
    private JPanel viewCards;

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

        // Create shared canvas ONCE (so all panels use same data)
        canvas2D = new DesignCanvas2D();
        view3D = new Viewer3DPanel(canvas2D);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildMainArea(), BorderLayout.CENTER);

        return root;
    }

    private JComponent buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("Furniture Room Design Visualizer");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        // View toggle buttons
        JToggleButton btn2D = new JToggleButton("2D", true);
        JToggleButton btn3D = new JToggleButton("3D");

        ButtonGroup group = new ButtonGroup();
        group.add(btn2D);
        group.add(btn3D);

        JPanel toggle = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        toggle.add(new JLabel("View:"));
        toggle.add(btn2D);
        toggle.add(btn3D);

        btn2D.addActionListener(e -> showView("2D"));
        btn3D.addActionListener(e -> showView("3D"));

        JLabel userLabel = new JLabel("Logged in as: " + loggedInUser);
        userLabel.setFont(userLabel.getFont().deriveFont(13f));

        bar.add(title, BorderLayout.WEST);
        bar.add(toggle, BorderLayout.CENTER);
        bar.add(userLabel, BorderLayout.EAST);

        return bar;
    }

    private void showView(String key) {
        if (cardLayout != null && viewCards != null) {
            cardLayout.show(viewCards, key);

            // If switching to 3D, repaint so it updates immediately
            if ("3D".equals(key)) {
                view3D.repaint();
            }
        }
    }

    private JComponent buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());

        // ===== Left: Room config + Furniture panel =====
        JPanel leftContainer = new JPanel();
        leftContainer.setPreferredSize(new Dimension(260, 0));
        leftContainer.setLayout(new BoxLayout(leftContainer, BoxLayout.Y_AXIS));

        RoomConfigPanel roomConfig = new RoomConfigPanel(canvas2D);
        roomConfig.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        FurniturePanel furniturePanel = new FurniturePanel(canvas2D);

        leftContainer.add(roomConfig);
        leftContainer.add(Box.createVerticalStrut(10));
        leftContainer.add(furniturePanel);

        // ===== Center: 2D / 3D cards =====
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Design Canvas"));

        cardLayout = new CardLayout();
        viewCards = new JPanel(cardLayout);

        viewCards.add(canvas2D, "2D");
        viewCards.add(view3D, "3D");

        center.add(viewCards, BorderLayout.CENTER);

        // ===== Right: Properties panel =====
        PropertiesPanel props = new PropertiesPanel(canvas2D);
        canvas2D.setSelectionListener(props::setSelectedItem);

        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(300, 0));
        right.add(props, BorderLayout.CENTER);

        main.add(leftContainer, BorderLayout.WEST);
        main.add(center, BorderLayout.CENTER);
        main.add(right, BorderLayout.EAST);

        return main;
    }
}
