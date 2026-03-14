package com.capitalcarrier.roomvisualizer.presentation.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class FormPasswordInput extends JPasswordField {
    private String placeholder;
    private Color borderColor = new Color(220, 220, 230);
    private Color focusBorderColor = new Color(175, 45, 255);
    private Color bgColor = new Color(248, 249, 251);

    public FormPasswordInput(String placeholder) {
        this.placeholder = placeholder;
        setOpaque(false);
        // Margin for the lock icon
        setBorder(new EmptyBorder(12, 45, 12, 18));
        setFont(new Font("Inter", Font.PLAIN, 14));
        setBackground(bgColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));

        // Draw border
        if (hasFocus()) {
            g2.setColor(focusBorderColor);
            g2.setStroke(new BasicStroke(1.5f));
        } else {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));

        // Draw Lock Icon
        g2.setColor(new Color(170, 175, 185));
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int x = 18;
        int y = (getHeight() - 16) / 2;
        g2.drawRect(x + 2, y + 6, 12, 10);
        g2.drawArc(x + 4, y, 8, 12, 0, 180);

        super.paintComponent(g);
        
        // Draw placeholder text if empty
        if (getPassword().length == 0 && !hasFocus()) {
            g2.setColor(new Color(170, 175, 185));
            g2.setFont(getFont());
            FontMetrics metrics = g2.getFontMetrics();
            int py = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
            g2.drawString(placeholder, getInsets().left, py);
        }
        
        g2.dispose();
    }
}
