package lk.group40.frdv.ui.panels;

import lk.group40.frdv.ui.canvas.DesignCanvas2D;

import javax.swing.*;
import java.awt.*;

public class FurniturePanel extends JPanel {

    public FurniturePanel(DesignCanvas2D canvas) {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Furniture"));

        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("Chair - Dining");
        model.addElement("Chair - Office");
        model.addElement("Table - Dining");
        model.addElement("Table - Side");

        JList<String> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);

        JScrollPane scrollPane = new JScrollPane(list);

        JButton addBtn = new JButton("Add to Canvas");
        addBtn.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null) {
                canvas.addFurniture(selected);
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(addBtn, BorderLayout.SOUTH);
    }
}
