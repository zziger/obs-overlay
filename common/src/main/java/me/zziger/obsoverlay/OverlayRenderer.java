package me.zziger.obsoverlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import me.zziger.obsoverlay.modules.Kernel32;
import me.zziger.obsoverlay.modules.MinHook;
import me.zziger.obsoverlay.registry.OverlayComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.*;

import java.util.Objects;

import static net.minecraft.client.MinecraftClient.IS_SYSTEM_MAC;
import static org.lwjgl.opengl.GL11.*;

public class OverlayRenderer {
    private static PointerByReference reference;
    private static boolean framebufferDirty = false;
    private static Framebuffer overlayFramebuffer = null;

    public static void markFramebufferDirty() {
        framebufferDirty = true;
    }

    public static void beginDraw() {
        if (overlayFramebuffer == null) return;
        overlayFramebuffer.beginWrite(false);
        framebufferDirty = true;
    }

    public static void beginEmptyDraw() {
        MinecraftClient.getInstance().getFramebuffer().endWrite();
    }

    public static void beginDraw(OverlayComponent component) {
        if (!component.isOverlayEnabled()) return;
        if (component.isHidden()) beginEmptyDraw();
        else beginDraw();
    }

    public static void endDraw() {
        if (overlayFramebuffer == null) return;
        MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
        framebufferDirty = true;
    }

    public static void endDraw(OverlayComponent component) {
        if (!component.isOverlayEnabled()) return;
        endDraw();
    }

    public static void onResolutionChanged(MinecraftClient client) {
        if (overlayFramebuffer == null) return;
        overlayFramebuffer.resize(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), IS_SYSTEM_MAC);
    }

    public static void beginFrame() {
        if (overlayFramebuffer == null) return;
        overlayFramebuffer.setClearColor(0, 0, 0, 0);
        overlayFramebuffer.clear(IS_SYSTEM_MAC);
    }

    private static void renderFrame() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (overlayFramebuffer != null && framebufferDirty) {
            framebufferDirty = false;

            GlStateManager._disableDepthTest();
            GlStateManager._enableBlend();
            GlStateManager._blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager._viewport(0, 0, client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());

            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            ShaderProgram shaderProgram = (ShaderProgram) Objects.requireNonNull(minecraftClient.gameRenderer.blitScreenProgram, "Blit shader not loaded");
            shaderProgram.addSampler("DiffuseSampler", overlayFramebuffer.getColorAttachment());
            shaderProgram.bind();
            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.BLIT_SCREEN);
            bufferBuilder.vertex(0.0F, 0.0F, 0.0F);
            bufferBuilder.vertex(1.0F, 0.0F, 0.0F);
            bufferBuilder.vertex(1.0F, 1.0F, 0.0F);
            bufferBuilder.vertex(0.0F, 1.0F, 0.0F);
            BufferRenderer.draw(bufferBuilder.end());
            shaderProgram.unbind();
        }
    }

    public static void init(MinecraftClient client) {
        if (!OBSOverlay.libraryInitialized) return;

        overlayFramebuffer = new SimpleFramebuffer(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), false, IS_SYSTEM_MAC);
        RenderSystem.clearColor(0, 0, 0, 0);
        overlayFramebuffer.setClearColor(0, 0, 0, 0);
        overlayFramebuffer.clear(IS_SYSTEM_MAC);

        Pointer module = Kernel32.INSTANCE.GetModuleHandleA("opengl32.dll");
        Pointer proc = Kernel32.INSTANCE.GetProcAddress(module, "wglSwapBuffers");

        try {
            MinHook minhook = MinHookManager.GetInstance();
            minhook.MH_Initialize();
            reference = new PointerByReference();

            minhook.MH_CreateHook(proc, hDc -> {
                renderFrame();
                Function origFunction = Function.getFunction(reference.getValue(), Function.ALT_CONVENTION);
                return (boolean) origFunction.invoke(Boolean.class, new Object[]{hDc});
            }, reference);
            minhook.MH_EnableHook(proc);
        } catch(Exception e) {
            OBSOverlay.LOGGER.error("Failed to initialize MinHook");
            OBSOverlay.libraryInitialized = false;
            overlayFramebuffer = null;
        }
    }
}
