package me.zziger.obsoverlay.api;

import me.zziger.obsoverlay.OverlayFramebufferType;
import me.zziger.obsoverlay.component.IOverlayComponent;

public interface IOverlayAPI {
    /**
     * Should be called before drawing a specific component
     * This will hide component only if it is enabled in settings
     * @param component Component that is being drawn
     */
    default void beginDraw(IOverlayComponent component) {}

    /**
     * Should be called after drawing a specific component, if you used beginDraw(IOverlayComponent component)
     * @param component Component that was drawn
     */
    default void endDraw(IOverlayComponent component) {}

    /**
     * Should be called before rendering elements, that you want hidden from stream
     * This method does not check settings
     * @param type Type of rendered elements. Specify DEPTH for content that needs depth testing (e.g. 3D elements),
     *             or NORMAL for 2D elements (e.g. GUI, HUD, etc.)
     */
    default void beginDraw(OverlayFramebufferType type) {}

    /**
     * Should be called before rendering elements, if you used beginDraw(OverlayFramebufferType type)
     * This method does not check settings
     * @param type Type of rendered elements. Pass the same value as in beginDraw(OverlayFramebufferType type)
     */
    default void endDraw(OverlayFramebufferType type) {}

    /**
     * This method backups current depth state to be later used for overlay rendering
     * It is useful for mods that add their own in-world rendering and clear depth buffer
     * @param overrideDepth Set to true to force all depth to be on top
     *                      (useful for e.g. player hand, so that it does not go through blocks)
     */
    default void backupDepth(boolean overrideDepth) {}
}
