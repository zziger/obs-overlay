package me.zziger.obsoverlay;

import com.mojang.blaze3d.platform.GlStateManager;
import me.zziger.obsoverlay.component.IOverlayComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.*;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class OverlayRenderer implements Closeable {
    static ShaderProgramKey SHADER = new ShaderProgramKey(Identifier.of("obs_overlay", "core/overlay"), VertexFormats.POSITION_TEXTURE, Defines.EMPTY);

    public boolean renderingHands = false;
    private int lastFramebuffer = 0;
    private Framebuffer depthBackupFramebuffer = null;
    private boolean framebufferOverridden = false;
    private final HashMap<OverlayFramebufferType, OverlayFramebuffer> framebuffers = new HashMap<>();

    OverlayRenderer() {
        OverlayHook.init();
        OverlayHook.subscribe(this::renderFrame);
        initializeFramebuffers();
    }

    public void close() {
        OverlayHook.unsubscribe(this::renderFrame);
    }

    private void initializeFramebuffers() {
        MinecraftClient client = MinecraftClient.getInstance();

        depthBackupFramebuffer = new SimpleFramebuffer(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), true);
        depthBackupFramebuffer.setClearColor(0, 0, 0, 0);
        depthBackupFramebuffer.clear();

        Framebuffer depthFramebuffer = new SimpleFramebuffer(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), true);
        depthFramebuffer.setClearColor(0, 0, 0, 0);
        depthFramebuffer.clear();

        Framebuffer normalFramebuffer = new SimpleFramebuffer(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), true);
        normalFramebuffer.setClearColor(0, 0, 0, 0);
        normalFramebuffer.clear();

        framebuffers.put(OverlayFramebufferType.DEPTH, new OverlayFramebuffer(depthFramebuffer));
        framebuffers.put(OverlayFramebufferType.NORMAL, new OverlayFramebuffer(normalFramebuffer));
    }

    public boolean isFramebufferOverridden() {
        return framebufferOverridden;
    }

    private void markOverlayDirty(OverlayFramebufferType type) {
        OverlayFramebuffer framebuffer = framebuffers.getOrDefault(type, null);
        if (framebuffer == null) return;
        framebuffer.dirty = true;
    }

    public void backupDepth(boolean fullDepth) {
        MinecraftClient client = MinecraftClient.getInstance();
        int fb = GlStateManager.getBoundFramebuffer();
        GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, depthBackupFramebuffer.fbo);
        renderQuad(false, true, fullDepth, client.getFramebuffer());
        GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fb);
    }

    private void backupFramebuffer() {
        if (framebuffers.isEmpty()) return;
        int boundFramebuffer = GlStateManager.getBoundFramebuffer();

        if (boundFramebuffer != framebuffers.get(OverlayFramebufferType.NORMAL).object.fbo
                && boundFramebuffer != framebuffers.get(OverlayFramebufferType.DEPTH).object.fbo
                && boundFramebuffer != 0) {
            lastFramebuffer = boundFramebuffer;
        }
    }

    private void restoreFramebuffer() {
        if (lastFramebuffer != 0)
            GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, lastFramebuffer);
    }

    public void beginDraw(OverlayFramebufferType type) {
        if (framebuffers.isEmpty()) return;
        backupFramebuffer();

        OverlayFramebuffer framebuffer = framebuffers.getOrDefault(type, null);
        if (framebuffer == null) return;

        GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, framebuffer.object.fbo);
        GlStateManager._enableDepthTest();

        markOverlayDirty(type);
        framebufferOverridden = true;
    }

    public void beginEmptyDraw() {
        if (framebuffers.isEmpty()) return;
        backupFramebuffer();

        GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        framebufferOverridden = true;
    }

    public void beginDraw(IOverlayComponent component) {
        if (!component.isOverlayEnabled()) return;
        component.beforeBeginDraw();
        if (component.isHidden()) beginEmptyDraw();
        else beginDraw(component.getFramebufferType());
    }

    public void endDraw() {
        if (framebuffers.isEmpty()) return;
        restoreFramebuffer();
        framebufferOverridden = false;
    }

    public void endDraw(IOverlayComponent component) {
        if (!component.isOverlayEnabled()) return;
        component.beforeEndDraw();
        endDraw();
    }

    public void onResolutionChanged(MinecraftClient client) {
        int width = client.getWindow().getFramebufferWidth();
        int height = client.getWindow().getFramebufferHeight();

        framebuffers.forEach((type, framebuffer) -> {
            if (framebuffer.object != null) {
                framebuffer.object.resize(width, height);
            }
        });
        if (depthBackupFramebuffer != null) {
            depthBackupFramebuffer.resize(width, height);
        }
    }

    private void renderQuad(boolean writeDepth, boolean depthTest, boolean overrideDepth, Framebuffer framebuffer) {
        MinecraftClient client = MinecraftClient.getInstance();
        ShaderProgram shaderProgram;

        try {
            shaderProgram = client.getShaderLoader().getProgramToLoad(SHADER);
        } catch (ShaderLoader.LoadException e) {
            return;
        }

        if (depthTest) GlStateManager._enableDepthTest();
        else GlStateManager._disableDepthTest();
        GlStateManager._depthMask(true);
        GlStateManager._enableBlend();
        GlStateManager._disableCull();
        GlStateManager._blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager._viewport(0, 0, client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());

        if (writeDepth) {
            GlStateManager._glBindFramebuffer(GL_READ_FRAMEBUFFER, depthBackupFramebuffer.fbo);
            GlStateManager._glBlitFrameBuffer(0, 0, client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(),
                    0, 0, client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(),
                    GL_DEPTH_BUFFER_BIT, GL_NEAREST);
            GlStateManager._glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
        }

        shaderProgram.initializeUniforms(VertexFormat.DrawMode.QUADS, new Matrix4f().identity(), new Matrix4f().identity(), client.getWindow());
        shaderProgram.addSamplerTexture("Sampler0", framebuffer.getColorAttachment());
        shaderProgram.addSamplerTexture("Sampler1", framebuffer.getDepthAttachment());
        Objects.requireNonNull(shaderProgram.getUniform("OverrideDepth")).set(overrideDepth ? 1 : 0);
        shaderProgram.bind();

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(-1.0f, -1.0f, 0.0F).texture(0, 0);
        bufferBuilder.vertex(1.0f, -1.0f, 0.0F).texture(1, 0);
        bufferBuilder.vertex(1.0f, 1.0f, 0.0F).texture(1, 1);
        bufferBuilder.vertex(-1.0f, 1.0f, 0.0F).texture(0, 1);
        BufferRenderer.draw(bufferBuilder.end());

        shaderProgram.unbind();
    }

    public void beginFrame() {
        framebuffers.forEach((type, framebuffer) -> {
            if (framebuffer.object != null) {
                framebuffer.object.setClearColor(0, 0, 0, 0);
                framebuffer.object.clear();
            }
        });

        if (depthBackupFramebuffer != null)
            depthBackupFramebuffer.clear();
    }

    private void renderFramebuffer(OverlayFramebufferType type) {
        OverlayFramebuffer framebuffer = framebuffers.getOrDefault(type, null);
        if (framebuffer == null || framebuffer.object == null || !framebuffer.dirty) return;


        framebuffer.dirty = false;
        GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        renderQuad(type == OverlayFramebufferType.DEPTH, type == OverlayFramebufferType.DEPTH, false, framebuffer.object);

    }

    public void renderFrame() {
        renderFramebuffer(OverlayFramebufferType.DEPTH);
        renderFramebuffer(OverlayFramebufferType.NORMAL);
    }
}
