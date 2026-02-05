package lk.group40.frdv.ui.panels;

import lk.group40.frdv.model.FurnitureItem;
import lk.group40.frdv.ui.canvas.DesignCanvas2D;

import javax.swing.*;
import java.awt.*;

public class PropertiesPanel extends JPanel {

    private final JLabel selectedName = new JLabel("No item selected");

    private final JTextField xField = new JTextField();
    private final JTextField yField = new JTextField();
    private final JTextField wField = new JTextField();
    private final JTextField hField = new JTextField();

    private final JButton colorBtn = new JButton("Pick Color");
    private final JButton applyBtn = new JButton("Apply");
    private final JButton deleteBtn = new JButton("Delete");

    private FurnitureItem current = null;

    public PropertiesPanel(DesignCanvas2D canvas) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Properties"));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        // Title / selected item
        gc.gridx = 0; gc.gridy = 0;
        selectedName.setFont(selectedName.getFont().deriveFont(Font.BOLD, 13f));
        add(selectedName, gc);

        gc.gridy++;
        add(new JSeparator(), gc);

        // X
        gc.gridy++;
        add(new JLabel("X"), gc);
        gc.gridy++;
        add(xField, gc);

        // Y
        gc.gridy++;
        add(new JLabel("Y"), gc);
        gc.gridy++;
        add(yField, gc);

        // Width
        gc.gridy++;
        add(new JLabel("Width"), gc);
        gc.gridy++;
        add(wField, gc);

        // Height
        gc.gridy++;
        add(new JLabel("Height"), gc);
        gc.gridy++;
        add(hField, gc);

        // Color
        gc.gridy++;
        add(colorBtn, gc);

        // Apply + Delete row
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.add(applyBtn);
        btnRow.add(deleteBtn);

        gc.gridy++;
        add(btnRow, gc);

        // Add a vertical glue spacer
        gc.gridy++;
        gc.weighty = 1;
        add(Box.createVerticalGlue(), gc);

        // Wire actions
        setEnabledAll(false);

        colorBtn.addActionListener(e -> {
            if (current == null) return;
            Color chosen = JColorChooser.showDialog(this, "Pick furniture color", current.getColor());
            if (chosen != null) {
                current.setColor(chosen);
                canvas.repaint();
            }
        });

        applyBtn.addActionListener(e -> {
            if (current == null) return;
            try {
                int x = Integer.parseInt(xField.getText().trim());
                int y = Integer.parseInt(yField.getText().trim());
                int w = Integer.parseInt(wField.getText().trim());
                int h = Integer.parseInt(hField.getText().trim());

                current.setX(x);
                current.setY(y);
                current.setWidth(Math.max(10, w));
                current.setHeight(Math.max(10, h));

                canvas.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers for X, Y, Width, Height.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            if (current == null) return;
            int res = JOptionPane.showConfirmDialog(this,
                    "Delete selected item?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                canvas.deleteSelected();
            }
        });
    }

    public void setSelectedItem(FurnitureItem item) {
        this.current = item;

        if (item == null) {
            selectedName.setText("No item selected");
            xField.setText("");
            yField.setText("");
            wField.setText("");
            hField.setText("");
            setEnabledAll(false);
            return;
        }

        selectedName.setText(item.getName());
        xField.setText(String.valueOf(item.getX()));
        yField.setText(String.valueOf(item.getY()));
        wField.setText(String.valueOf(item.getWidth()));
        hField.setText(String.valueOf(item.getHeight()));
        setEnabledAll(true);
    }

    private void setEnabledAll(boolean enabled) {
        xField.setEnabled(enabled);
        yField.setEnabled(enabled);
        wField.setEnabled(enabled);
        hField.setEnabled(enabled);
        colorBtn.setEnabled(enabled);
        applyBtn.setEnabled(enabled);
        deleteBtn.setEnabled(enabled);
    }
}
