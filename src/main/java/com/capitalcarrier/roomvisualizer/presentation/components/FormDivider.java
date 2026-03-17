package com.capitalcarrier.roomvisualizer.presentation.components;

import javax.swing.*;
import java.awt.*;

public class FormDivider extends JPanel {
    private String text;
    private Color lineColor = new Color(230, 230, 235);
    private Color textColor = new Color(160, 165, 175);

    public FormDivider(String text) {
        this.text = text;
        setOpaque(false);
        setPreferredSize(new Dimension(300, 30));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(new Font("Inter", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        // Lines
        g2.setColor(lineColor);
        g2.setStroke(new BasicStroke(1.0f));
        int lineY = getHeight() / 2;
        g2.drawLine(0, lineY, textX - 15, lineY);
        g2.drawLine(textX + textWidth + 15, lineY, getWidth(), lineY);

        // Text
        g2.setColor(textColor);
        g2.drawString(text, textX, textY);

        g2.dispose();
    }
}
