package me.zziger.obsoverlay.api.impl;

import me.zziger.obsoverlay.OverlayFramebufferType;
import me.zziger.obsoverlay.OverlayRenderer;
import me.zziger.obsoverlay.OverlayUtils;
import me.zziger.obsoverlay.api.IOverlayAPI;
import me.zziger.obsoverlay.component.IOverlayComponent;

public class NormalOverlayAPI implements IOverlayAPI {
    private final OverlayRenderer renderer;

    public NormalOverlayAPI(OverlayRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void beginDraw(IOverlayComponent component) {
        this.renderer.beginDraw(component);
    }

    @Override
    public void endDraw(IOverlayComponent component) {
        this.renderer.endDraw(component);
    }

    @Override
    public void beginDraw(OverlayFramebufferType type) {
        this.renderer.beginDraw(type);
    }

    @Override
    public void endDraw(OverlayFramebufferType type) {
        this.renderer.endDraw();
    }

    @Override
    public void backupDepth(boolean overrideDepth) {
        this.renderer.backupDepth(overrideDepth);
    }
}
