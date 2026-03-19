package com.capitalcarrier.roomvisualizer.presentation.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class FormInput extends JTextField {
    public enum IconType { NONE, USER, ENVELOPE, LOCK }
    
    private String placeholder;
    private IconType iconType;
    private Color borderColor = new Color(220, 220, 230);
    private Color focusBorderColor = new Color(175, 45, 255);
    private Color bgColor = new Color(248, 249, 251);
    
    public FormInput(String placeholder, IconType iconType) {
        this.placeholder = placeholder;
        this.iconType = iconType;
        setOpaque(false);
        int leftPadding = (iconType == IconType.NONE) ? 18 : 45;
        setBorder(new EmptyBorder(12, leftPadding, 12, 18));
        setFont(new Font("Inter", Font.PLAIN, 14)); 
        setBackground(bgColor);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));

        if (hasFocus()) {
            g2.setColor(focusBorderColor);
            g2.setStroke(new BasicStroke(1.5f));
        } else {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));

        if (iconType != IconType.NONE) {
            drawIcon(g2);
        }

        super.paintComponent(g);
        
        if (getText().isEmpty() && !hasFocus()) {
            g2.setColor(new Color(170, 175, 185));
            g2.setFont(getFont());
            FontMetrics metrics = g2.getFontMetrics();
            int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
            g2.drawString(placeholder, getInsets().left, y);
        }
        
        g2.dispose();
    }

    private void drawIcon(Graphics2D g2) {
        g2.setColor(new Color(170, 175, 185));
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int x = 18;
        int y = (getHeight() - 16) / 2;

        switch (iconType) {
            case USER:
                g2.drawOval(x + 4, y, 8, 8);
                g2.drawArc(x, y + 10, 16, 12, 0, 180);
                break;
            case ENVELOPE:
                g2.drawRect(x, y + 2, 16, 12);
                g2.drawLine(x, y + 2, x + 8, y + 8);
                g2.drawLine(x + 16, y + 2, x + 8, y + 8);
                break;
            case LOCK:
                g2.drawRect(x + 2, y + 6, 12, 10);
                g2.drawArc(x + 4, y, 8, 12, 0, 180);
                break;
            case NONE:
                break;
        }
    }
}
