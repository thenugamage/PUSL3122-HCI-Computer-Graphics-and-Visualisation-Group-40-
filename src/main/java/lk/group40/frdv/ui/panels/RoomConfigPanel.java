package lk.group40.frdv.ui.panels;

import lk.group40.frdv.ui.canvas.DesignCanvas2D;

import javax.swing.*;
import java.awt.*;

public class RoomConfigPanel extends JPanel {

    public RoomConfigPanel(DesignCanvas2D canvas) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Room Config"));

        JTextField widthField = new JTextField("500");
        JTextField heightField = new JTextField("350");
        JButton applyBtn = new JButton("Apply");

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        gc.gridx = 0; gc.gridy = 0;
        add(new JLabel("Width"), gc);
        gc.gridy = 1;
        add(widthField, gc);

        gc.gridy = 2;
        add(new JLabel("Height"), gc);
        gc.gridy = 3;
        add(heightField, gc);

        gc.gridy = 4;
        add(applyBtn, gc);

        applyBtn.addActionListener(e -> {
            try {
                int w = Integer.parseInt(widthField.getText().trim());
                int h = Integer.parseInt(heightField.getText().trim());
                canvas.setRoomSize(w, h);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers for width and height.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
