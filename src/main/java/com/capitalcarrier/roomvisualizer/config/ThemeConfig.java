package com.capitalcarrier.roomvisualizer.config;

import java.awt.Color;

public class ThemeConfig {
    // Keeping some constants for the redesigned SettingsPanel to function
    public static final Color BACKGROUND = new Color(2, 6, 23); // #020617 approx
    public static final Color NAV_BG = BACKGROUND;
    public static final Color NAV_HEIGHT_PX = new Color(0, 0, 64);
    
    // Brand & Accents
    public static final Color PURPLE = new Color(139, 92, 246); // #8B5CF6
    public static final Color CYAN   = new Color(6, 182, 212); // #06B6D2
    public static final Color PURPLE_LT = new Color(139, 92, 246);
    
    // Text & Surfaces
    public static final Color TXT_PRIMARY = Color.WHITE;
    public static final Color TXT_SECONDARY = new Color(140, 148, 175); // #8C94AF
    public static final Color TXT_MUTED = new Color(90, 98, 130);
    public static final Color SURFACE_DARK = new Color(14, 20, 55);
    public static final Color SURFACE_LIGHT = new Color(30, 35, 65); // #1E2341 (Active nav)
    public static final Color SURFACE_BORDER = new Color(35, 45, 90);

    public static final int NAV_HEIGHT = 64;
    public static final int CARD_ROUNDING = 16;
    public static final int ICON_SIZE = 16;

    // Backward compatibility for existing files
    public static final Color DEEP_NAVY = SURFACE_DARK;
    public static final Color ACCENT_PURPLE = PURPLE_LT;
    public static final Color ACCENT_CYAN = CYAN;
    public static final Color GLASS_BG = new Color(255, 255, 255, 15);
    public static final Color GLASS_BORDER = new Color(255, 255, 255, 40);
    public static final Color GLASS_TEXT = Color.WHITE;
    public static final Color GLASS_SUBTEXT = TXT_SECONDARY;
    public static final Color NAV_BG_SOLID = BACKGROUND;
    public static final int INPUT_ROUNDING = 12;
    public static final int BUTTON_ROUNDING = 10;
    public static final int PILL_ROUNDING = 20;
    public static final Color ACCENT_GLOW = new Color(108, 55, 220, 120);
}
