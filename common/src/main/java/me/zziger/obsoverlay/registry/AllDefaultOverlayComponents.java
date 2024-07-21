package me.zziger.obsoverlay.registry;

public class AllDefaultOverlayComponents {
    public static OverlayComponent debugMenu =      new DefaultOverlayComponent("debug_menu", true, true);
    public static OverlayComponent chat =           new DefaultOverlayComponent("chat", false, true);
    public static OverlayComponent chatBar =        new DefaultOverlayComponent("chat_bar", false, false);
    public static OverlayComponent playerList =     new DefaultOverlayComponent("player_list", false, false);
    public static OverlayComponent subtitles =      new DefaultOverlayComponent("subtitles", false, true);
    public static OverlayComponent scoreboards =    new DefaultOverlayComponent("scoreboards", false, true);
    public static OverlayComponent actionbar =      new DefaultOverlayComponent("actionbar", false, true);
    public static OverlayComponent titleSubtitle =  new DefaultOverlayComponent("title_subtitle", false, true);
    public static OverlayComponent effects =        new DefaultOverlayComponent("effects", false, true);
    public static OverlayComponent mainHud =        new DefaultOverlayComponent("main_hud", false, true);

    public static void init() {
        OverlayComponentRegistry.registerComponents(debugMenu, chat, chatBar, playerList, subtitles, scoreboards, actionbar, titleSubtitle, effects, mainHud);
    }
}
