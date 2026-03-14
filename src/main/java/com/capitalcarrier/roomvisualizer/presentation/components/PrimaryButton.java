package com.capitalcarrier.roomvisualizer.presentation.components;

import javax.swing.*;
import java.awt.*;

public class PrimaryButton extends JButton {

    public PrimaryButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Inter", Font.BOLD, 15));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(360, 48));
        setMaximumSize(new Dimension(360, 48));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color color1 = new Color(153, 51, 255); // #9933FF
        Color color2 = new Color(125, 30, 255); // #7D1EFF

        if (getModel().isPressed()) {
            g2.setPaint(new GradientPaint(0, 0, color2, 0, getHeight(), color1));
        } else if (getModel().isRollover()) {
            g2.setPaint(new GradientPaint(0, 0, color1.brighter(), 0, getHeight(), color2.brighter()));
        } else {
            g2.setPaint(new GradientPaint(0, 0, color1, 0, getHeight(), color2));
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        // Subtle shadow
        g2.setColor(new Color(0, 0, 0, 20));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

        super.paintComponent(g);
        g2.dispose();
    }
}
