package me.zziger.obsoverlay;

import net.minecraft.client.gl.Framebuffer;

public class OverlayFramebuffer {
    public Framebuffer object;
    public boolean dirty;

    OverlayFramebuffer(Framebuffer object) {
        this.object = object;
        this.dirty = false;
    }
}
