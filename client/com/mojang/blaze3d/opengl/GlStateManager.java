package com.mojang.blaze3d.opengl;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.platform.MacosUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.Plot;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@DontObfuscate
public class GlStateManager {
   private static final boolean ON_LINUX;
   private static final Plot PLOT_TEXTURES;
   private static int numTextures;
   private static final Plot PLOT_BUFFERS;
   private static int numBuffers;
   private static final BlendState BLEND;
   private static final DepthState DEPTH;
   private static final CullState CULL;
   private static final PolygonOffsetState POLY_OFFSET;
   private static final ColorLogicState COLOR_LOGIC;
   private static final ScissorState SCISSOR;
   private static int activeTexture;
   private static final TextureState[] TEXTURES;
   private static final ColorMask COLOR_MASK;
   private static int readFbo;
   private static int writeFbo;

   public GlStateManager() {
      super();
   }

   public static void _disableScissorTest() {
      RenderSystem.assertOnRenderThread();
      SCISSOR.mode.disable();
   }

   public static void _enableScissorTest() {
      RenderSystem.assertOnRenderThread();
      SCISSOR.mode.enable();
   }

   public static void _scissorBox(int var0, int var1, int var2, int var3) {
      RenderSystem.assertOnRenderThread();
      GL20.glScissor(var0, var1, var2, var3);
   }

   public static void _disableDepthTest() {
      RenderSystem.assertOnRenderThread();
      DEPTH.mode.disable();
   }

   public static void _enableDepthTest() {
      RenderSystem.assertOnRenderThread();
      DEPTH.mode.enable();
   }

   public static void _depthFunc(int var0) {
      RenderSystem.assertOnRenderThread();
      if (var0 != DEPTH.func) {
         DEPTH.func = var0;
         GL11.glDepthFunc(var0);
      }

   }

   public static void _depthMask(boolean var0) {
      RenderSystem.assertOnRenderThread();
      if (var0 != DEPTH.mask) {
         DEPTH.mask = var0;
         GL11.glDepthMask(var0);
      }

   }

   public static void _disableBlend() {
      RenderSystem.assertOnRenderThread();
      BLEND.mode.disable();
   }

   public static void _enableBlend() {
      RenderSystem.assertOnRenderThread();
      BLEND.mode.enable();
   }

   public static void _blendFuncSeparate(int var0, int var1, int var2, int var3) {
      RenderSystem.assertOnRenderThread();
      if (var0 != BLEND.srcRgb || var1 != BLEND.dstRgb || var2 != BLEND.srcAlpha || var3 != BLEND.dstAlpha) {
         BLEND.srcRgb = var0;
         BLEND.dstRgb = var1;
         BLEND.srcAlpha = var2;
         BLEND.dstAlpha = var3;
         glBlendFuncSeparate(var0, var1, var2, var3);
      }

   }

   public static int glGetProgrami(int var0, int var1) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetProgrami(var0, var1);
   }

   public static void glAttachShader(int var0, int var1) {
      RenderSystem.assertOnRenderThread();
      GL20.glAttachShader(var0, var1);
   }

   public static void glDeleteShader(int var0) {
      RenderSystem.assertOnRenderThread();
      GL20.glDeleteShader(var0);
   }

   public static int glCreateShader(int var0) {
      RenderSystem.assertOnRenderThread();
      return GL20.glCreateShader(var0);
   }

   public static void glShaderSource(int var0, String var1) {
      RenderSystem.assertOnRenderThread();
      byte[] var2 = var1.getBytes(Charsets.UTF_8);
      ByteBuffer var3 = MemoryUtil.memAlloc(var2.length + 1);
      var3.put(var2);
      var3.put((byte)0);
      var3.flip();

      try {
         MemoryStack var4 = MemoryStack.stackPush();

         try {
            PointerBuffer var5 = var4.mallocPointer(1);
            var5.put(var3);
            GL20C.nglShaderSource(var0, 1, var5.address0(), 0L);
         } catch (Throwable var12) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (var4 != null) {
            var4.close();
         }
      } finally {
         MemoryUtil.memFree(var3);
      }

   }

   public static void glCompileShader(int var0) {
      RenderSystem.assertOnRenderThread();
      GL20.glCompileShader(var0);
   }

   public static int glGetShaderi(int var0, int var1) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetShaderi(var0, var1);
   }

   public static void _glUseProgram(int var0) {
      RenderSystem.assertOnRenderThread();
      GL20.glUseProgram(var0);
   }

   public static int glCreateProgram() {
      RenderSystem.assertOnRenderThread();
      return GL20.glCreateProgram();
   }

   public static void glDeleteProgram(int var0) {
      RenderSystem.assertOnRenderThread();
      GL20.glDeleteProgram(var0);
   }

   public static void glLinkProgram(int var0) {
      RenderSystem.assertOnRenderThread();
      GL20.glLinkProgram(var0);
   }

   public static int _glGetUniformLocation(int var0, CharSequence var1) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetUniformLocation(var0, var1);
   }

   public static void _glUniform1(int var0, IntBuffer var1) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform1iv(var0, var1);
   }

   public static void _glUniform1i(int var0, int var1) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform1i(var0, var1);
   }

   public static void _glUniform1(int var0, FloatBuffer var1) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform1fv(var0, var1);
   }

   public static void _glUniform2(int var0, FloatBuffer var1) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform2fv(var0, var1);
   }

   public static void _glUniform3(int var0, IntBuffer var1) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform3iv(var0, var1);
   }

   public static void _glUniform3(int var0, FloatBuffer var1) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform3fv(var0, var1);
   }

   public static void _glUniform4(int var0, FloatBuffer var1) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform4fv(var0, var1);
   }

   public static void _glUniformMatrix4(int var0, FloatBuffer var1) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniformMatrix4fv(var0, false, var1);
   }

   public static void _glBindAttribLocation(int var0, int var1, CharSequence var2) {
      RenderSystem.assertOnRenderThread();
      GL20.glBindAttribLocation(var0, var1, var2);
   }

   public static int _glGenBuffers() {
      RenderSystem.assertOnRenderThread();
      ++numBuffers;
      PLOT_BUFFERS.setValue((double)numBuffers);
      return GL15.glGenBuffers();
   }

   public static int _glGenVertexArrays() {
      RenderSystem.assertOnRenderThread();
      return GL30.glGenVertexArrays();
   }

   public static void _glBindBuffer(int var0, int var1) {
      RenderSystem.assertOnRenderThread();
      GL15.glBindBuffer(var0, var1);
   }

   public static void _glBindVertexArray(int var0) {
      RenderSystem.assertOnRenderThread();
      GL30.glBindVertexArray(var0);
   }

   public static void _glBufferData(int var0, ByteBuffer var1, int var2) {
      RenderSystem.assertOnRenderThread();
      GL15.glBufferData(var0, var1, var2);
   }

   public static void _glBufferSubData(int var0, int var1, ByteBuffer var2) {
      RenderSystem.assertOnRenderThread();
      GL15.glBufferSubData(var0, (long)var1, var2);
   }

   public static void _glBufferData(int var0, long var1, int var3) {
      RenderSystem.assertOnRenderThread();
      GL15.glBufferData(var0, var1, var3);
   }

   @Nullable
   public static ByteBuffer _glMapBufferRange(int var0, int var1, int var2, int var3) {
      RenderSystem.assertOnRenderThread();
      return GL30.glMapBufferRange(var0, (long)var1, (long)var2, var3);
   }

   public static void _glUnmapBuffer(int var0) {
      RenderSystem.assertOnRenderThread();
      GL15.glUnmapBuffer(var0);
   }

   public static void _glDeleteBuffers(int var0) {
      RenderSystem.assertOnRenderThread();
      if (ON_LINUX) {
         GL32C.glBindBuffer(34962, var0);
         GL32C.glBufferData(34962, 0L, 35048);
         GL32C.glBindBuffer(34962, 0);
      }

      --numBuffers;
      PLOT_BUFFERS.setValue((double)numBuffers);
      GL15.glDeleteBuffers(var0);
   }

   public static void _glBindFramebuffer(int var0, int var1) {
      if ((var0 == 36008 || var0 == 36160) && readFbo != var1) {
         GL30.glBindFramebuffer(36008, var1);
         readFbo = var1;
      }

      if ((var0 == 36009 || var0 == 36160) && writeFbo != var1) {
         GL30.glBindFramebuffer(36009, var1);
         writeFbo = var1;
      }

   }

   public static int getFrameBuffer(int var0) {
      if (var0 == 36008) {
         return readFbo;
      } else {
         return var0 == 36009 ? writeFbo : 0;
      }
   }

   public static void _glBlitFrameBuffer(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      RenderSystem.assertOnRenderThread();
      GL30.glBlitFramebuffer(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public static void _glDeleteFramebuffers(int var0) {
      RenderSystem.assertOnRenderThread();
      GL30.glDeleteFramebuffers(var0);
   }

   public static int glGenFramebuffers() {
      RenderSystem.assertOnRenderThread();
      return GL30.glGenFramebuffers();
   }

   public static void _glFramebufferTexture2D(int var0, int var1, int var2, int var3, int var4) {
      RenderSystem.assertOnRenderThread();
      GL30.glFramebufferTexture2D(var0, var1, var2, var3, var4);
   }

   public static void glActiveTexture(int var0) {
      RenderSystem.assertOnRenderThread();
      GL13.glActiveTexture(var0);
   }

   public static void glBlendFuncSeparate(int var0, int var1, int var2, int var3) {
      RenderSystem.assertOnRenderThread();
      GL14.glBlendFuncSeparate(var0, var1, var2, var3);
   }

   public static String glGetShaderInfoLog(int var0, int var1) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetShaderInfoLog(var0, var1);
   }

   public static String glGetProgramInfoLog(int var0, int var1) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetProgramInfoLog(var0, var1);
   }

   public static void _enableCull() {
      RenderSystem.assertOnRenderThread();
      CULL.enable.enable();
   }

   public static void _disableCull() {
      RenderSystem.assertOnRenderThread();
      CULL.enable.disable();
   }

   public static void _polygonMode(int var0, int var1) {
      RenderSystem.assertOnRenderThread();
      GL11.glPolygonMode(var0, var1);
   }

   public static void _enablePolygonOffset() {
      RenderSystem.assertOnRenderThread();
      POLY_OFFSET.fill.enable();
   }

   public static void _disablePolygonOffset() {
      RenderSystem.assertOnRenderThread();
      POLY_OFFSET.fill.disable();
   }

   public static void _polygonOffset(float var0, float var1) {
      RenderSystem.assertOnRenderThread();
      if (var0 != POLY_OFFSET.factor || var1 != POLY_OFFSET.units) {
         POLY_OFFSET.factor = var0;
         POLY_OFFSET.units = var1;
         GL11.glPolygonOffset(var0, var1);
      }

   }

   public static void _enableColorLogicOp() {
      RenderSystem.assertOnRenderThread();
      COLOR_LOGIC.enable.enable();
   }

   public static void _disableColorLogicOp() {
      RenderSystem.assertOnRenderThread();
      COLOR_LOGIC.enable.disable();
   }

   public static void _logicOp(int var0) {
      RenderSystem.assertOnRenderThread();
      if (var0 != COLOR_LOGIC.op) {
         COLOR_LOGIC.op = var0;
         GL11.glLogicOp(var0);
      }

   }

   public static void _activeTexture(int var0) {
      RenderSystem.assertOnRenderThread();
      if (activeTexture != var0 - '\u84c0') {
         activeTexture = var0 - '\u84c0';
         glActiveTexture(var0);
      }

   }

   public static void _texParameter(int var0, int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      GL11.glTexParameteri(var0, var1, var2);
   }

   public static int _getTexLevelParameter(int var0, int var1, int var2) {
      return GL11.glGetTexLevelParameteri(var0, var1, var2);
   }

   public static int _genTexture() {
      RenderSystem.assertOnRenderThread();
      ++numTextures;
      PLOT_TEXTURES.setValue((double)numTextures);
      return GL11.glGenTextures();
   }

   public static void _deleteTexture(int var0) {
      RenderSystem.assertOnRenderThread();
      GL11.glDeleteTextures(var0);

      for(TextureState var4 : TEXTURES) {
         if (var4.binding == var0) {
            var4.binding = -1;
         }
      }

      --numTextures;
      PLOT_TEXTURES.setValue((double)numTextures);
   }

   public static void _bindTexture(int var0) {
      RenderSystem.assertOnRenderThread();
      if (var0 != TEXTURES[activeTexture].binding) {
         TEXTURES[activeTexture].binding = var0;
         GL11.glBindTexture(3553, var0);
      }

   }

   public static int _getActiveTexture() {
      return activeTexture + '\u84c0';
   }

   public static void _texImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, @Nullable IntBuffer var8) {
      RenderSystem.assertOnRenderThread();
      GL11.glTexImage2D(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void _texSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8) {
      RenderSystem.assertOnRenderThread();
      GL11.glTexSubImage2D(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void _texSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, IntBuffer var8) {
      RenderSystem.assertOnRenderThread();
      GL11.glTexSubImage2D(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void _viewport(int var0, int var1, int var2, int var3) {
      GL11.glViewport(var0, var1, var2, var3);
   }

   public static void _colorMask(boolean var0, boolean var1, boolean var2, boolean var3) {
      RenderSystem.assertOnRenderThread();
      if (var0 != COLOR_MASK.red || var1 != COLOR_MASK.green || var2 != COLOR_MASK.blue || var3 != COLOR_MASK.alpha) {
         COLOR_MASK.red = var0;
         COLOR_MASK.green = var1;
         COLOR_MASK.blue = var2;
         COLOR_MASK.alpha = var3;
         GL11.glColorMask(var0, var1, var2, var3);
      }

   }

   public static void _clear(int var0) {
      RenderSystem.assertOnRenderThread();
      GL11.glClear(var0);
      if (MacosUtil.IS_MACOS) {
         _getError();
      }

   }

   public static void _vertexAttribPointer(int var0, int var1, int var2, boolean var3, int var4, long var5) {
      RenderSystem.assertOnRenderThread();
      GL20.glVertexAttribPointer(var0, var1, var2, var3, var4, var5);
   }

   public static void _vertexAttribIPointer(int var0, int var1, int var2, int var3, long var4) {
      RenderSystem.assertOnRenderThread();
      GL30.glVertexAttribIPointer(var0, var1, var2, var3, var4);
   }

   public static void _enableVertexAttribArray(int var0) {
      RenderSystem.assertOnRenderThread();
      GL20.glEnableVertexAttribArray(var0);
   }

   public static void _drawElements(int var0, int var1, int var2, long var3) {
      RenderSystem.assertOnRenderThread();
      GL11.glDrawElements(var0, var1, var2, var3);
   }

   public static void _drawArrays(int var0, int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      GL11.glDrawArrays(var0, var1, var2);
   }

   public static void _pixelStore(int var0, int var1) {
      RenderSystem.assertOnRenderThread();
      GL11.glPixelStorei(var0, var1);
   }

   public static void _readPixels(int var0, int var1, int var2, int var3, int var4, int var5, long var6) {
      RenderSystem.assertOnRenderThread();
      GL11.glReadPixels(var0, var1, var2, var3, var4, var5, var6);
   }

   public static int _getError() {
      RenderSystem.assertOnRenderThread();
      return GL11.glGetError();
   }

   public static void clearGlErrors() {
      RenderSystem.assertOnRenderThread();

      while(GL11.glGetError() != 0) {
      }

   }

   public static String _getString(int var0) {
      RenderSystem.assertOnRenderThread();
      return GL11.glGetString(var0);
   }

   public static int _getInteger(int var0) {
      RenderSystem.assertOnRenderThread();
      return GL11.glGetInteger(var0);
   }

   public static long _glFenceSync(int var0, int var1) {
      RenderSystem.assertOnRenderThread();
      return GL32.glFenceSync(var0, var1);
   }

   public static int _glClientWaitSync(long var0, int var2, long var3) {
      RenderSystem.assertOnRenderThread();
      return GL32.glClientWaitSync(var0, var2, var3);
   }

   public static void _glDeleteSync(long var0) {
      RenderSystem.assertOnRenderThread();
      GL32.glDeleteSync(var0);
   }

   static {
      ON_LINUX = Util.getPlatform() == Util.OS.LINUX;
      PLOT_TEXTURES = TracyClient.createPlot("GPU Textures");
      numTextures = 0;
      PLOT_BUFFERS = TracyClient.createPlot("GPU Buffers");
      numBuffers = 0;
      BLEND = new BlendState();
      DEPTH = new DepthState();
      CULL = new CullState();
      POLY_OFFSET = new PolygonOffsetState();
      COLOR_LOGIC = new ColorLogicState();
      SCISSOR = new ScissorState();
      TEXTURES = (TextureState[])IntStream.range(0, 12).mapToObj((var0) -> new TextureState()).toArray((var0) -> new TextureState[var0]);
      COLOR_MASK = new ColorMask();
   }

   static class TextureState {
      public int binding;

      TextureState() {
         super();
      }
   }

   static class BlendState {
      public final BooleanState mode = new BooleanState(3042);
      public int srcRgb = 1;
      public int dstRgb = 0;
      public int srcAlpha = 1;
      public int dstAlpha = 0;

      BlendState() {
         super();
      }
   }

   static class DepthState {
      public final BooleanState mode = new BooleanState(2929);
      public boolean mask = true;
      public int func = 513;

      DepthState() {
         super();
      }
   }

   static class CullState {
      public final BooleanState enable = new BooleanState(2884);

      CullState() {
         super();
      }
   }

   static class PolygonOffsetState {
      public final BooleanState fill = new BooleanState(32823);
      public float factor;
      public float units;

      PolygonOffsetState() {
         super();
      }
   }

   static class ColorLogicState {
      public final BooleanState enable = new BooleanState(3058);
      public int op = 5379;

      ColorLogicState() {
         super();
      }
   }

   static class ScissorState {
      public final BooleanState mode = new BooleanState(3089);

      ScissorState() {
         super();
      }
   }

   static class ColorMask {
      public boolean red = true;
      public boolean green = true;
      public boolean blue = true;
      public boolean alpha = true;

      ColorMask() {
         super();
      }
   }

   static class BooleanState {
      private final int state;
      private boolean enabled;

      public BooleanState(int var1) {
         super();
         this.state = var1;
      }

      public void disable() {
         this.setEnabled(false);
      }

      public void enable() {
         this.setEnabled(true);
      }

      public void setEnabled(boolean var1) {
         RenderSystem.assertOnRenderThread();
         if (var1 != this.enabled) {
            this.enabled = var1;
            if (var1) {
               GL11.glEnable(this.state);
            } else {
               GL11.glDisable(this.state);
            }
         }

      }
   }
}
