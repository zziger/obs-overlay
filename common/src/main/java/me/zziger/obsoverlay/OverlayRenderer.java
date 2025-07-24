package me.zziger.obsoverlay;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import me.zziger.obsoverlay.modules.Kernel32;
import me.zziger.obsoverlay.modules.MinHook;
import me.zziger.obsoverlay.registry.OverlayComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.*;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.minecraft.client.MinecraftClient.IS_SYSTEM_MAC;
import static net.minecraft.client.gl.ShaderProgramKeys.*;
import static net.minecraft.client.render.RenderPhase.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

public class OverlayRenderer {
    private static PointerByReference reference;
    private static boolean framebufferDirty = false;
    private static int lastFramebuffer = 0;
    private static Framebuffer overlayFramebuffer = null;
    private static boolean framebufferOverridden = false;

    public static boolean isFramebufferOverridden() {
        return framebufferOverridden;
    }

    public static void markFramebufferDirty() {
        framebufferDirty = true;
    }

    public static void beginDraw() {
        if (overlayFramebuffer == null) return;
        int boundFramebuffer = GlStateManager.getBoundFramebuffer();
        if (boundFramebuffer != overlayFramebuffer.fbo && boundFramebuffer != 0) lastFramebuffer = boundFramebuffer;

        GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, overlayFramebuffer.fbo);
        framebufferDirty = true;
        framebufferOverridden = true;
    }

    public static void beginEmptyDraw() {
        int boundFramebuffer = GlStateManager.getBoundFramebuffer();
        if (boundFramebuffer != overlayFramebuffer.fbo && boundFramebuffer != 0) lastFramebuffer = boundFramebuffer;

        GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        framebufferOverridden = true;
    }

    public static void beginDraw(OverlayComponent component) {
        if (!component.isOverlayEnabled()) return;
        component.beforeBeginDraw();
        if (component.isHidden()) beginEmptyDraw();
        else beginDraw();
    }

    public static void endDraw() {
        if (overlayFramebuffer == null) return;
        framebufferOverridden = false;

        if (lastFramebuffer != 0)
            GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, lastFramebuffer);

        framebufferDirty = true;
    }

    public static void endDraw(OverlayComponent component) {
        if (!component.isOverlayEnabled()) return;
        component.beforeEndDraw();
        endDraw();
    }

    public static void onResolutionChanged(MinecraftClient client) {
        if (overlayFramebuffer == null) return;
        overlayFramebuffer.resize(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());
    }

    public static void beginFrame() {
        if (overlayFramebuffer == null) return;
        overlayFramebuffer.setClearColor(0, 0, 0, 0);
        overlayFramebuffer.clear();
    }


    public static void renderFrame() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (overlayFramebuffer != null && framebufferDirty) {

            ShaderProgram shaderProgram;

            try {
                shaderProgram = client.getShaderLoader().getProgramToLoad(POSITION_TEX);
            } catch(ShaderLoader.LoadException e) {
                // Shader is not ready yet!
                return;
            }

            framebufferDirty = false;

            GlStateManager._disableDepthTest();
            GlStateManager._enableBlend();
            GlStateManager._disableCull();
            GlStateManager._blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager._viewport(0, 0, client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());
            GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);

            shaderProgram.initializeUniforms(VertexFormat.DrawMode.QUADS, new Matrix4f().identity(), new Matrix4f().identity(), client.getWindow());
            shaderProgram.addSamplerTexture("Sampler0", overlayFramebuffer.getColorAttachment());
            shaderProgram.bind();

            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(-1.0f, -1.0f, 0.0F).texture(0, 0);
            bufferBuilder.vertex(1.0f, -1.0f, 0.0F).texture(1, 0);
            bufferBuilder.vertex(1.0f, 1.0f, 0.0F).texture(1, 1);
            bufferBuilder.vertex(-1.0f, 1.0f, 0.0F).texture(0, 1);
            BufferRenderer.draw(bufferBuilder.end());

            shaderProgram.unbind();
        }
    }

    public static void init(MinecraftClient client) {
        if (!OBSOverlay.libraryInitialized) return;

        overlayFramebuffer = new SimpleFramebuffer(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), true);
        RenderSystem.clearColor(0, 0, 0, 0);
        overlayFramebuffer.setClearColor(0, 0, 0, 0);
        overlayFramebuffer.clear();

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
        } catch (Exception e) {
            OBSOverlay.LOGGER.error("Failed to initialize MinHook");
            OBSOverlay.libraryInitialized = false;
            overlayFramebuffer = null;
        }
    }
}
