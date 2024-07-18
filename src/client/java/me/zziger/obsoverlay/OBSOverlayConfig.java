package me.zziger.obsoverlay;

import eu.midnightdust.lib.config.MidnightConfig;

public class OBSOverlayConfig extends MidnightConfig {
    @Comment() public static Comment comment1;
    @Entry() public static boolean overlayDebugMenu = true;
    @Entry() public static boolean overlayChat = false;
    @Entry() public static boolean overlayChatBar = false;
    @Entry() public static boolean overlayPlayerList = false;
    @Entry() public static boolean overlaySubtitles = false;
    @Entry() public static boolean overlayScoreboards = false;
    @Entry() public static boolean overlayActionbar = false;
    @Entry() public static boolean overlayTitleSubtitle = false;
    @Entry() public static boolean overlayEffects = false;
    @Entry() public static boolean overlayMainHud = false;
    @Comment() public static Comment comment4;
    @Entry() public static boolean showTestIcon = false;

    @Comment(category = "additional") public static Comment comment2;
    @Comment(category = "additional") public static Comment comment3;
    @Entry(category = "additional") public static boolean autoHideDebugMenu = true;
    @Entry(category = "additional") public static boolean autoHideChat = true;
    @Entry(category = "additional") public static boolean autoHideSubtitles = true;
    @Entry(category = "additional") public static boolean autoHideScoreboards = true;
    @Entry(category = "additional") public static boolean autoHideActionbar = true;
    @Entry(category = "additional") public static boolean autoHideTitleSubtitle = true;
    @Entry(category = "additional") public static boolean autoHideEffects = true;
    @Entry(category = "additional") public static boolean autoHideMainHud = true;


    public static void init() {
        MidnightConfig.init(OBSOverlayClient.MOD_ID, OBSOverlayConfig.class);
    }
}
