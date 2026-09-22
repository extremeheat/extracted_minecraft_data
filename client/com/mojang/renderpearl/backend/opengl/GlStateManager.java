package com.mojang.renderpearl.backend.opengl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.Plot;
import com.mojang.jtracy.TracyClient;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.IntStream;
import net.minecraft.util.Util;
import org.joml.Vector4fc;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class GlStateManager {
   private static final Plot PLOT_TEXTURES = TracyClient.createPlot("GPU Textures");
   private static int numTextures = 0;
   private static final Plot PLOT_BUFFERS = TracyClient.createPlot("GPU Buffers");
   private static int numBuffers = 0;
   private static final boolean IS_MACOS;
   private final BlendState blend = new BlendState();
   private final boolean[] blendEnable = new boolean[8];
   private final DepthState depth = new DepthState();
   private final CullState cull = new CullState();
   private final PolygonOffsetState polyOffset = new PolygonOffsetState();
   private final ScissorState scissor = new ScissorState();
   private int activeTexture;
   private static final int TEXTURE_COUNT = 12;
   private final TextureState[] TEXTURES = (TextureState[])IntStream.range(0, 12).mapToObj((i) -> new TextureState()).toArray((x$0) -> new TextureState[x$0]);
   private final @ColorTargetState.WriteMask int[] COLOR_MASK = new int[8];
   private int readFbo;
   private int writeFbo;

   public GlStateManager() {
      super();
      Arrays.setAll(this.COLOR_MASK, (var0) -> 15);
      Arrays.fill(this.blendEnable, false);
   }

   public void _disableScissorTest() {
      RenderSystem.assertOnRenderThread();
      this.scissor.mode.disable();
   }

   public void _enableScissorTest() {
      RenderSystem.assertOnRenderThread();
      this.scissor.mode.enable();
   }

   public void _disableDepthTest() {
      RenderSystem.assertOnRenderThread();
      this.depth.mode.disable();
   }

   public void _enableDepthTest() {
      RenderSystem.assertOnRenderThread();
      this.depth.mode.enable();
   }

   public void _depthFunc(final int func) {
      RenderSystem.assertOnRenderThread();
      if (func != this.depth.func) {
         this.depth.func = func;
         GL33C.glDepthFunc(func);
      }

   }

   public void _depthMask(final boolean mask) {
      RenderSystem.assertOnRenderThread();
      if (mask != this.depth.mask) {
         this.depth.mask = mask;
         GL33C.glDepthMask(mask);
      }

   }

   public void _disableBlend(final int index) {
      RenderSystem.assertOnRenderThread();
      if (this.blendEnable[index]) {
         this.blendEnable[index] = false;
         GL33C.glDisablei(3042, index);
      }
   }

   public void _enableBlend(final int index) {
      RenderSystem.assertOnRenderThread();
      if (!this.blendEnable[index]) {
         this.blendEnable[index] = true;
         GL33C.glEnablei(3042, index);
      }
   }

   public void _blendFuncSeparate(final int srcRgb, final int dstRgb, final int srcAlpha, final int dstAlpha) {
      RenderSystem.assertOnRenderThread();
      if (srcRgb != this.blend.srcRgb || dstRgb != this.blend.dstRgb || srcAlpha != this.blend.srcAlpha || dstAlpha != this.blend.dstAlpha) {
         this.blend.srcRgb = srcRgb;
         this.blend.dstRgb = dstRgb;
         this.blend.srcAlpha = srcAlpha;
         this.blend.dstAlpha = dstAlpha;
         GL33C.glBlendFuncSeparate(srcRgb, dstRgb, srcAlpha, dstAlpha);
      }

   }

   public void _blendEquationSeparate(final int modeRgb, final int modeAlpha) {
      RenderSystem.assertOnRenderThread();
      if (modeRgb != this.blend.modeRgb || modeAlpha != this.blend.modeAlpha) {
         this.blend.modeRgb = modeRgb;
         this.blend.modeAlpha = modeAlpha;
         GL33C.glBlendEquationSeparate(modeRgb, modeAlpha);
      }

   }

   public void glShaderSource(final int shader, final String source) {
      RenderSystem.assertOnRenderThread();
      byte[] encoded = source.getBytes(StandardCharsets.UTF_8);
      ByteBuffer buffer = MemoryUtil.memAlloc(encoded.length + 1);
      buffer.put(encoded);
      buffer.put((byte)0);
      buffer.flip();

      try {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            PointerBuffer pointers = stack.mallocPointer(1);
            pointers.put(buffer);
            GL33C.nglShaderSource(shader, 1, pointers.address0(), 0L);
         } catch (Throwable var13) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (stack != null) {
            stack.close();
         }
      } finally {
         MemoryUtil.memFree(buffer);
      }

   }

   static void incrementTrackedBuffers() {
      ++numBuffers;
      PLOT_BUFFERS.setValue((double)numBuffers);
   }

   public static int _glGenBuffers() {
      RenderSystem.assertOnRenderThread();
      incrementTrackedBuffers();
      return GL33C.glGenBuffers();
   }

   public static void _glDeleteBuffers(final int buffer) {
      RenderSystem.assertOnRenderThread();
      --numBuffers;
      PLOT_BUFFERS.setValue((double)numBuffers);
      GL33C.glDeleteBuffers(buffer);
   }

   public void _glBindFramebuffer(final int target, final int framebuffer) {
      if ((target == 36008 || target == 36160) && this.readFbo != framebuffer) {
         GL33C.glBindFramebuffer(36008, framebuffer);
         this.readFbo = framebuffer;
      }

      if ((target == 36009 || target == 36160) && this.writeFbo != framebuffer) {
         GL33C.glBindFramebuffer(36009, framebuffer);
         this.writeFbo = framebuffer;
      }

   }

   public int getFrameBuffer(final int target) {
      if (target == 36008) {
         return this.readFbo;
      } else {
         return target == 36009 ? this.writeFbo : 0;
      }
   }

   public void _glDeleteFramebuffers(final int framebuffer) {
      RenderSystem.assertOnRenderThread();
      GL33C.glDeleteFramebuffers(framebuffer);
      if (this.readFbo == framebuffer) {
         this.readFbo = 0;
      }

      if (this.writeFbo == framebuffer) {
         this.writeFbo = 0;
      }

   }

   public void _enableCull() {
      RenderSystem.assertOnRenderThread();
      this.cull.enable.enable();
   }

   public void _disableCull() {
      RenderSystem.assertOnRenderThread();
      this.cull.enable.disable();
   }

   public void _enablePolygonOffset() {
      RenderSystem.assertOnRenderThread();
      this.polyOffset.fill.enable();
   }

   public void _disablePolygonOffset() {
      RenderSystem.assertOnRenderThread();
      this.polyOffset.fill.disable();
   }

   public void _polygonOffset(final float factor, final float units) {
      RenderSystem.assertOnRenderThread();
      if (factor != this.polyOffset.factor || units != this.polyOffset.units) {
         this.polyOffset.factor = factor;
         this.polyOffset.units = units;
         GL33C.glPolygonOffset(factor, units);
      }

   }

   public void _activeTexture(final int texture) {
      RenderSystem.assertOnRenderThread();
      if (this.activeTexture != texture - '\u84c0') {
         this.activeTexture = texture - '\u84c0';
         GL33C.glActiveTexture(texture);
      }

   }

   public int _genTexture() {
      RenderSystem.assertOnRenderThread();
      ++numTextures;
      PLOT_TEXTURES.setValue((double)numTextures);
      return GL33C.glGenTextures();
   }

   public void _deleteTexture(final int id) {
      RenderSystem.assertOnRenderThread();
      GL33C.glDeleteTextures(id);

      for(TextureState state : this.TEXTURES) {
         if (state.binding == id) {
            state.binding = -1;
         }
      }

      --numTextures;
      PLOT_TEXTURES.setValue((double)numTextures);
   }

   public void _bindTexture(final int id) {
      RenderSystem.assertOnRenderThread();
      if (id != this.TEXTURES[this.activeTexture].binding) {
         this.TEXTURES[this.activeTexture].binding = id;
         GL33C.glBindTexture(3553, id);
      }

   }

   public void _colorMask(final @ColorTargetState.WriteMask int writeMask) {
      RenderSystem.assertOnRenderThread();

      for(int i = 0; i < this.COLOR_MASK.length; ++i) {
         if (writeMask != this.COLOR_MASK[i]) {
            this.COLOR_MASK[i] = writeMask;
            GL33C.glColorMaski(i, (writeMask & 1) != 0, (writeMask & 2) != 0, (writeMask & 4) != 0, (writeMask & 8) != 0);
         }
      }

   }

   public void _colorMask(final int index, final @ColorTargetState.WriteMask int writeMask) {
      RenderSystem.assertOnRenderThread();
      if (writeMask != this.COLOR_MASK[index]) {
         this.COLOR_MASK[index] = writeMask;
         GL33C.glColorMaski(index, (writeMask & 1) != 0, (writeMask & 2) != 0, (writeMask & 4) != 0, (writeMask & 8) != 0);
      }

   }

   public static void _clear(final int mask) {
      RenderSystem.assertOnRenderThread();
      GL33C.glClear(mask);
      if (IS_MACOS) {
         GL33C.glGetError();
      }

   }

   public static void _clearBuffer(final int index, final Vector4fc clearColor) {
      RenderSystem.assertOnRenderThread();
      GL33C.glClearBufferfv(6144, index, new float[]{clearColor.x(), clearColor.y(), clearColor.z(), clearColor.w()});
      if (IS_MACOS) {
         GL33C.glGetError();
      }

   }

   public static void _clearBuffer(final double clearDepth) {
      RenderSystem.assertOnRenderThread();
      GL33C.glClearBufferfv(6145, 0, new float[]{(float)clearDepth});
      if (IS_MACOS) {
         GL33C.glGetError();
      }

   }

   public static void clearGlErrors() {
      RenderSystem.assertOnRenderThread();

      while(GL33C.glGetError() != 0) {
      }

   }

   static {
      IS_MACOS = Util.getPlatform() == Util.OS.OSX;
   }

   private static class TextureState {
      public int binding;

      private TextureState() {
         super();
      }
   }

   private static class BlendState {
      public int srcRgb = 1;
      public int dstRgb = 0;
      public int modeRgb = 32774;
      public int srcAlpha = 1;
      public int dstAlpha = 0;
      public int modeAlpha = 32774;

      private BlendState() {
         super();
      }
   }

   private static class DepthState {
      public final BooleanState mode = new BooleanState(2929);
      public boolean mask = true;
      public int func = 513;

      private DepthState() {
         super();
      }
   }

   private static class CullState {
      public final BooleanState enable = new BooleanState(2884);

      private CullState() {
         super();
      }
   }

   private static class PolygonOffsetState {
      public final BooleanState fill = new BooleanState(32823);
      public float factor;
      public float units;

      private PolygonOffsetState() {
         super();
      }
   }

   private static class ScissorState {
      public final BooleanState mode = new BooleanState(3089);

      private ScissorState() {
         super();
      }
   }

   private static class BooleanState {
      private final int state;
      private boolean enabled;

      public BooleanState(final int state) {
         super();
         this.state = state;
      }

      public void disable() {
         this.setEnabled(false);
      }

      public void enable() {
         this.setEnabled(true);
      }

      public void setEnabled(final boolean enabled) {
         RenderSystem.assertOnRenderThread();
         if (enabled != this.enabled) {
            this.enabled = enabled;
            if (enabled) {
               GL33C.glEnable(this.state);
            } else {
               GL33C.glDisable(this.state);
            }
         }

      }
   }
}
