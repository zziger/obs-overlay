package me.zziger.obsoverlay.registry;

public class AllDefaultOverlayComponents {
    public static OverlayComponent debugMenu =      new HUDOverlayComponent("debug_menu", true, true);
    public static OverlayComponent chat =           new HUDOverlayComponent("chat", false, true);
    public static OverlayComponent chatBar =        new HUDOverlayComponent("chat_bar", false, false);
    public static OverlayComponent playerList =     new HUDOverlayComponent("player_list", false, false);
    public static OverlayComponent subtitles =      new HUDOverlayComponent("subtitles", false, true);
    public static OverlayComponent scoreboards =    new HUDOverlayComponent("scoreboards", false, true);
    public static OverlayComponent actionbar =      new HUDOverlayComponent("actionbar", false, true);
    public static OverlayComponent titleSubtitle =  new HUDOverlayComponent("title_subtitle", false, true);
    public static OverlayComponent effects =        new HUDOverlayComponent("effects", false, true);
    public static OverlayComponent mainHud =        new HUDOverlayComponent("main_hud", false, true);

    public static void init() {
        OverlayComponentRegistry.registerComponents(debugMenu, chat, chatBar, playerList, subtitles, scoreboards, actionbar, titleSubtitle, effects, mainHud);
    }
}
