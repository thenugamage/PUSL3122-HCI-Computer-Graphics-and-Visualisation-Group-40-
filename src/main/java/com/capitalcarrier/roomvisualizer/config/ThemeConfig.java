package com.capitalcarrier.roomvisualizer.config;

import javafx.scene.paint.Color;

public class ThemeConfig {
    // Brand & Accents
    public static final Color BRAND_PURPLE = Color.web("#8B5CF6");
    public static final Color BRAND_NAVY   = Color.web("#091C61");
    
    public static final Color BACKGROUND = BRAND_NAVY;
    public static final Color NAV_BG = Color.web("#0F1437");
    
    // Legacy Accents (Updated for harmonized look)
    public static final Color PURPLE = BRAND_PURPLE;
    public static final Color CYAN   = Color.web("#06B6D2");
    public static final Color PURPLE_LT = Color.web("#A78BFA");
    
    // Text & Surfaces
    public static final Color TXT_PRIMARY = Color.WHITE;
    public static final Color TXT_SECONDARY = Color.web("#8C94AF");
    public static final Color TXT_MUTED = Color.web("#5A6282");
    public static final Color SURFACE_DARK = Color.web("#0E1437");
    public static final Color SURFACE_LIGHT = Color.web("#1E2341");
    public static final Color SURFACE_BORDER = Color.web("#232D5A");

    public static final int NAV_HEIGHT = 64;
    public static final int CARD_ROUNDING = 28;
    public static final int ICON_SIZE = 16;
    public static final int INPUT_ROUNDING = 12;
    public static final int BUTTON_ROUNDING = 10;
    public static final int PILL_ROUNDING = 20;

    // Compatibility
    public static final Color DEEP_NAVY = BRAND_NAVY;
    public static final Color ACCENT_PURPLE = BRAND_PURPLE;
    public static final Color ACCENT_CYAN = CYAN;
    public static final Color GLASS_BG = Color.rgb(255, 255, 255, 0.05);
    public static final Color GLASS_BORDER = Color.rgb(255, 255, 255, 0.15);
    public static final Color GLASS_TEXT = Color.WHITE;
    public static final Color GLASS_SUBTEXT = TXT_SECONDARY;
}
