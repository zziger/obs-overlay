package me.zziger.obsoverlay.component;

import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.component.type.HUDOverlayComponent;
import me.zziger.obsoverlay.component.type.InGameOverlayComponent;

public class AllDefaultOverlayComponents {
    public static IOverlayComponent debugMenu =     new HUDOverlayComponent(OBSOverlay.id("debug_menu"), true, true);
    public static IOverlayComponent chat =          new HUDOverlayComponent(OBSOverlay.id("chat"), false, true);
    public static IOverlayComponent chatBar =       new HUDOverlayComponent(OBSOverlay.id("chat_bar"), false, false);
    public static IOverlayComponent playerList =    new HUDOverlayComponent(OBSOverlay.id("player_list"), false, false);
    public static IOverlayComponent subtitles =     new HUDOverlayComponent(OBSOverlay.id("subtitles"), false, true);
    public static IOverlayComponent scoreboards =   new HUDOverlayComponent(OBSOverlay.id("scoreboards"), false, true);
    public static IOverlayComponent actionbar =     new HUDOverlayComponent(OBSOverlay.id("actionbar"), false, true);
    public static IOverlayComponent titleSubtitle = new HUDOverlayComponent(OBSOverlay.id("title_subtitle"), false, true);
    public static IOverlayComponent effects =       new HUDOverlayComponent(OBSOverlay.id("effects"), false, true);
    public static IOverlayComponent mainHud =       new HUDOverlayComponent(OBSOverlay.id("main_hud"), false, true);
    public static IOverlayComponent nameTag =       new HUDOverlayComponent(OBSOverlay.id("name_tag"), false, true);

    public static IOverlayComponent nameTagSneaking =   new InGameOverlayComponent(OBSOverlay.id("name_tag_sneaking"), false, true);
    public static IOverlayComponent signText =          new InGameOverlayComponent(OBSOverlay.id("sign_text"), false, true);
    public static IOverlayComponent chest =             new InGameOverlayComponent(OBSOverlay.id("chest"), false, true);
    public static IOverlayComponent itemFrameMap =      new InGameOverlayComponent(OBSOverlay.id("item_frame_map"), false, true);
    public static IOverlayComponent bannerCanvas =      new InGameOverlayComponent(OBSOverlay.id("banner_canvas"), false, true);
    public static IOverlayComponent beaconBeam =        new InGameOverlayComponent(OBSOverlay.id("beacon_beam"), false, true);

    public static void init() {
        OverlayComponentRegistry.registerComponents(debugMenu, chat, chatBar, playerList, subtitles, scoreboards, actionbar,
                titleSubtitle, effects, mainHud, nameTag, nameTagSneaking, signText, chest, itemFrameMap, bannerCanvas, beaconBeam);
    }
}
