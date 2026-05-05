package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.platform.MacosUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.Plot;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class GlStateManager {
   private static final Plot PLOT_TEXTURES = TracyClient.createPlot("GPU Textures");
   private static int numTextures = 0;
   private static final Plot PLOT_BUFFERS = TracyClient.createPlot("GPU Buffers");
   private static int numBuffers = 0;
   private static final BlendState[] BLEND = new BlendState[8];
   private static final DepthState DEPTH = new DepthState();
   private static final CullState CULL = new CullState();
   private static final PolygonOffsetState POLY_OFFSET = new PolygonOffsetState();
   private static final ColorLogicState COLOR_LOGIC = new ColorLogicState();
   private static final ScissorState SCISSOR = new ScissorState();
   private static int activeTexture;
   private static final int TEXTURE_COUNT = 12;
   private static final TextureState[] TEXTURES = (TextureState[])IntStream.range(0, 12).mapToObj((i) -> new TextureState()).toArray((x$0) -> new TextureState[x$0]);
   private static final @ColorTargetState.WriteMask int[] COLOR_MASK = new int[8];
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

   public static void _scissorBox(final int x, final int y, final int width, final int height) {
      RenderSystem.assertOnRenderThread();
      GL20.glScissor(x, y, width, height);
   }

   public static void _disableDepthTest() {
      RenderSystem.assertOnRenderThread();
      DEPTH.mode.disable();
   }

   public static void _enableDepthTest() {
      RenderSystem.assertOnRenderThread();
      DEPTH.mode.enable();
   }

   public static void _depthFunc(final int func) {
      RenderSystem.assertOnRenderThread();
      if (func != DEPTH.func) {
         DEPTH.func = func;
         GL11.glDepthFunc(func);
      }

   }

   public static void _depthMask(final boolean mask) {
      RenderSystem.assertOnRenderThread();
      if (mask != DEPTH.mask) {
         DEPTH.mask = mask;
         GL11.glDepthMask(mask);
      }

   }

   public static void _disableBlend(int index) {
      RenderSystem.assertOnRenderThread();
      BLEND[index].mode.disable();
   }

   public static void _enableBlend(int index) {
      RenderSystem.assertOnRenderThread();
      BLEND[index].mode.enable();
   }

   public static void _blendFuncSeparate(final int srcRgb, final int dstRgb, final int srcAlpha, final int dstAlpha) {
      RenderSystem.assertOnRenderThread();
      BlendState firstBlend = BLEND[0];
      if (srcRgb != firstBlend.srcRgb || dstRgb != firstBlend.dstRgb || srcAlpha != firstBlend.srcAlpha || dstAlpha != firstBlend.dstAlpha) {
         firstBlend.srcRgb = srcRgb;
         firstBlend.dstRgb = dstRgb;
         firstBlend.srcAlpha = srcAlpha;
         firstBlend.dstAlpha = dstAlpha;
         glBlendFuncSeparate(srcRgb, dstRgb, srcAlpha, dstAlpha);
      }

   }

   public static void _blendEquationSeparate(final int modeRgb, final int modeAlpha) {
      RenderSystem.assertOnRenderThread();
      BlendState firstBlend = BLEND[0];
      if (modeRgb != firstBlend.modeRgb || modeAlpha != firstBlend.modeAlpha) {
         firstBlend.modeRgb = modeRgb;
         firstBlend.modeAlpha = modeAlpha;
         glBlendEquationSeparate(modeRgb, modeAlpha);
      }

   }

   public static int glGetProgrami(final int program, final int pname) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetProgrami(program, pname);
   }

   public static void glAttachShader(final int program, final int shader) {
      RenderSystem.assertOnRenderThread();
      GL20.glAttachShader(program, shader);
   }

   public static void glDeleteShader(final int shader) {
      RenderSystem.assertOnRenderThread();
      GL20.glDeleteShader(shader);
   }

   public static int glCreateShader(final int type) {
      RenderSystem.assertOnRenderThread();
      return GL20.glCreateShader(type);
   }

   public static void glShaderSource(final int shader, final String source) {
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
            GL20C.nglShaderSource(shader, 1, pointers.address0(), 0L);
         } catch (Throwable var12) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (stack != null) {
            stack.close();
         }
      } finally {
         MemoryUtil.memFree(buffer);
      }

   }

   public static void glCompileShader(final int shader) {
      RenderSystem.assertOnRenderThread();
      GL20.glCompileShader(shader);
   }

   public static int glGetShaderi(final int shader, final int pname) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetShaderi(shader, pname);
   }

   public static void _glUseProgram(final int program) {
      RenderSystem.assertOnRenderThread();
      GL20.glUseProgram(program);
   }

   public static int glCreateProgram() {
      RenderSystem.assertOnRenderThread();
      return GL20.glCreateProgram();
   }

   public static void glDeleteProgram(final int program) {
      RenderSystem.assertOnRenderThread();
      GL20.glDeleteProgram(program);
   }

   public static void glLinkProgram(final int program) {
      RenderSystem.assertOnRenderThread();
      GL20.glLinkProgram(program);
   }

   public static int _glGetUniformLocation(final int program, final CharSequence name) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetUniformLocation(program, name);
   }

   public static void _glUniform1i(final int location, final int v0) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform1i(location, v0);
   }

   public static void _glBindAttribLocation(final int program, final int location, final CharSequence name) {
      RenderSystem.assertOnRenderThread();
      GL20.glBindAttribLocation(program, location, name);
   }

   static void incrementTrackedBuffers() {
      ++numBuffers;
      PLOT_BUFFERS.setValue((double)numBuffers);
   }

   public static int _glGenBuffers() {
      RenderSystem.assertOnRenderThread();
      incrementTrackedBuffers();
      return GL15.glGenBuffers();
   }

   public static int _glGenVertexArrays() {
      RenderSystem.assertOnRenderThread();
      return GL30.glGenVertexArrays();
   }

   public static void _glBindBuffer(final int target, final int buffer) {
      RenderSystem.assertOnRenderThread();
      GL15.glBindBuffer(target, buffer);
   }

   public static void _glBindVertexArray(final int arrayId) {
      RenderSystem.assertOnRenderThread();
      GL30.glBindVertexArray(arrayId);
   }

   public static void _glBufferData(final int target, final ByteBuffer data, final int usage) {
      RenderSystem.assertOnRenderThread();
      GL15.glBufferData(target, data, usage);
   }

   public static void _glBufferSubData(final int target, final long offset, final ByteBuffer data) {
      RenderSystem.assertOnRenderThread();
      GL15.glBufferSubData(target, offset, data);
   }

   public static void _glBufferData(final int target, final long size, final int usage) {
      RenderSystem.assertOnRenderThread();
      GL15.glBufferData(target, size, usage);
   }

   public static @Nullable ByteBuffer _glMapBufferRange(final int target, final long offset, final long length, final int access) {
      RenderSystem.assertOnRenderThread();
      return GL30.glMapBufferRange(target, offset, length, access);
   }

   public static void _glUnmapBuffer(final int target) {
      RenderSystem.assertOnRenderThread();
      GL15.glUnmapBuffer(target);
   }

   public static void _glDeleteBuffers(final int buffer) {
      RenderSystem.assertOnRenderThread();
      --numBuffers;
      PLOT_BUFFERS.setValue((double)numBuffers);
      GL15.glDeleteBuffers(buffer);
   }

   public static void _glBindFramebuffer(final int target, final int framebuffer) {
      if ((target == 36008 || target == 36160) && readFbo != framebuffer) {
         GL30.glBindFramebuffer(36008, framebuffer);
         readFbo = framebuffer;
      }

      if ((target == 36009 || target == 36160) && writeFbo != framebuffer) {
         GL30.glBindFramebuffer(36009, framebuffer);
         writeFbo = framebuffer;
      }

   }

   public static int getFrameBuffer(final int target) {
      if (target == 36008) {
         return readFbo;
      } else {
         return target == 36009 ? writeFbo : 0;
      }
   }

   public static void _glBlitFrameBuffer(final int srcX0, final int srcY0, final int srcX1, final int srcY1, final int dstX0, final int dstY0, final int dstX1, final int dstY1, final int mask, final int filter) {
      RenderSystem.assertOnRenderThread();
      GL30.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
   }

   public static void _glDeleteFramebuffers(final int framebuffer) {
      RenderSystem.assertOnRenderThread();
      GL30.glDeleteFramebuffers(framebuffer);
      if (readFbo == framebuffer) {
         readFbo = 0;
      }

      if (writeFbo == framebuffer) {
         writeFbo = 0;
      }

   }

   public static int glGenFramebuffers() {
      RenderSystem.assertOnRenderThread();
      return GL30.glGenFramebuffers();
   }

   public static void _glFramebufferTexture2D(final int target, final int attachment, final int textarget, final int texture, final int level) {
      RenderSystem.assertOnRenderThread();
      GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
   }

   public static void glBlendFuncSeparate(final int srcColor, final int dstColor, final int srcAlpha, final int dstAlpha) {
      RenderSystem.assertOnRenderThread();
      GL14.glBlendFuncSeparate(srcColor, dstColor, srcAlpha, dstAlpha);
   }

   public static void glBlendEquationSeparate(final int modeRgb, final int modeAlpha) {
      RenderSystem.assertOnRenderThread();
      GL20C.glBlendEquationSeparate(modeRgb, modeAlpha);
   }

   public static String glGetShaderInfoLog(final int shader, final int maxLength) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetShaderInfoLog(shader, maxLength);
   }

   public static String glGetProgramInfoLog(final int program, final int maxLength) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetProgramInfoLog(program, maxLength);
   }

   public static void _enableCull() {
      RenderSystem.assertOnRenderThread();
      CULL.enable.enable();
   }

   public static void _disableCull() {
      RenderSystem.assertOnRenderThread();
      CULL.enable.disable();
   }

   public static void _polygonMode(final int face, final int mode) {
      RenderSystem.assertOnRenderThread();
      GL11.glPolygonMode(face, mode);
   }

   public static void _enablePolygonOffset() {
      RenderSystem.assertOnRenderThread();
      POLY_OFFSET.fill.enable();
   }

   public static void _disablePolygonOffset() {
      RenderSystem.assertOnRenderThread();
      POLY_OFFSET.fill.disable();
   }

   public static void _polygonOffset(final float factor, final float units) {
      RenderSystem.assertOnRenderThread();
      if (factor != POLY_OFFSET.factor || units != POLY_OFFSET.units) {
         POLY_OFFSET.factor = factor;
         POLY_OFFSET.units = units;
         GL11.glPolygonOffset(factor, units);
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

   public static void _logicOp(final int op) {
      RenderSystem.assertOnRenderThread();
      if (op != COLOR_LOGIC.op) {
         COLOR_LOGIC.op = op;
         GL11.glLogicOp(op);
      }

   }

   public static void _activeTexture(final int texture) {
      RenderSystem.assertOnRenderThread();
      if (activeTexture != texture - '\u84c0') {
         activeTexture = texture - '\u84c0';
         GL13.glActiveTexture(texture);
      }

   }

   public static void _texParameter(final int target, final int name, final int value) {
      RenderSystem.assertOnRenderThread();
      GL11.glTexParameteri(target, name, value);
   }

   public static int _getTexLevelParameter(final int target, final int level, final int name) {
      return GL11.glGetTexLevelParameteri(target, level, name);
   }

   public static int _genTexture() {
      RenderSystem.assertOnRenderThread();
      ++numTextures;
      PLOT_TEXTURES.setValue((double)numTextures);
      return GL11.glGenTextures();
   }

   public static void _deleteTexture(final int id) {
      RenderSystem.assertOnRenderThread();
      GL11.glDeleteTextures(id);

      for(TextureState state : TEXTURES) {
         if (state.binding == id) {
            state.binding = -1;
         }
      }

      --numTextures;
      PLOT_TEXTURES.setValue((double)numTextures);
   }

   public static void _bindTexture(final int id) {
      RenderSystem.assertOnRenderThread();
      if (id != TEXTURES[activeTexture].binding) {
         TEXTURES[activeTexture].binding = id;
         GL11.glBindTexture(3553, id);
      }

   }

   public static void _texImage2D(final int target, final int level, final int internalformat, final int width, final int height, final int border, final int format, final int type, final @Nullable ByteBuffer pixels) {
      RenderSystem.assertOnRenderThread();
      GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
   }

   public static void _texSubImage2D(final int target, final int level, final int xoffset, final int yoffset, final int width, final int height, final int format, final int type, final long pixels) {
      RenderSystem.assertOnRenderThread();
      GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
   }

   public static void _texSubImage2D(final int target, final int level, final int xoffset, final int yoffset, final int width, final int height, final int format, final int type, final ByteBuffer pixels) {
      RenderSystem.assertOnRenderThread();
      GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
   }

   public static void _viewport(final int x, final int y, final int width, final int height) {
      GL11.glViewport(x, y, width, height);
   }

   public static void _colorMask(final @ColorTargetState.WriteMask int writeMask) {
      RenderSystem.assertOnRenderThread();

      for(int i = 0; i < COLOR_MASK.length; ++i) {
         if (writeMask != COLOR_MASK[i]) {
            COLOR_MASK[i] = writeMask;
            GL30.glColorMaski(i, (writeMask & 1) != 0, (writeMask & 2) != 0, (writeMask & 4) != 0, (writeMask & 8) != 0);
         }
      }

   }

   public static void _colorMask(final int index, final @ColorTargetState.WriteMask int writeMask) {
      RenderSystem.assertOnRenderThread();
      if (writeMask != COLOR_MASK[index]) {
         COLOR_MASK[index] = writeMask;
         GL30.glColorMaski(index, (writeMask & 1) != 0, (writeMask & 2) != 0, (writeMask & 4) != 0, (writeMask & 8) != 0);
      }

   }

   public static void _clear(final int mask) {
      RenderSystem.assertOnRenderThread();
      GL11.glClear(mask);
      if (MacosUtil.IS_MACOS) {
         _getError();
      }

   }

   public static void _clearBuffer(final int index, final Vector4fc clearColor) {
      RenderSystem.assertOnRenderThread();
      GL30.glClearBufferfv(6144, index, new float[]{clearColor.x(), clearColor.y(), clearColor.z(), clearColor.w()});
      if (MacosUtil.IS_MACOS) {
         _getError();
      }

   }

   public static void _clearBuffer(final double clearDepth) {
      RenderSystem.assertOnRenderThread();
      GL30.glClearBufferfv(6145, 0, new float[]{(float)clearDepth});
      if (MacosUtil.IS_MACOS) {
         _getError();
      }

   }

   public static void _vertexAttribPointer(final int index, final int size, final int type, final boolean normalized, final int stride, final long value) {
      RenderSystem.assertOnRenderThread();
      GL20.glVertexAttribPointer(index, size, type, normalized, stride, value);
   }

   public static void _vertexAttribIPointer(final int index, final int size, final int type, final int stride, final long value) {
      RenderSystem.assertOnRenderThread();
      GL30.glVertexAttribIPointer(index, size, type, stride, value);
   }

   public static void _enableVertexAttribArray(final int index) {
      RenderSystem.assertOnRenderThread();
      GL20.glEnableVertexAttribArray(index);
   }

   public static void _drawElements(final int mode, final int count, final int type, final long indices) {
      RenderSystem.assertOnRenderThread();
      GL11.glDrawElements(mode, count, type, indices);
   }

   public static void _drawArrays(final int mode, final int first, final int count) {
      RenderSystem.assertOnRenderThread();
      GL11.glDrawArrays(mode, first, count);
   }

   public static void _pixelStore(final int name, final int value) {
      RenderSystem.assertOnRenderThread();
      GL11.glPixelStorei(name, value);
   }

   public static void _readPixels(final int x, final int y, final int width, final int height, final int format, final int type, final long pixels) {
      RenderSystem.assertOnRenderThread();
      GL11.glReadPixels(x, y, width, height, format, type, pixels);
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

   public static String _getString(final int id) {
      RenderSystem.assertOnRenderThread();
      return GL11.glGetString(id);
   }

   public static int _getInteger(final int name) {
      RenderSystem.assertOnRenderThread();
      return GL11.glGetInteger(name);
   }

   public static long _glFenceSync(final int condition, final int flags) {
      RenderSystem.assertOnRenderThread();
      return GL32.glFenceSync(condition, flags);
   }

   public static int _glClientWaitSync(final long sync, final int flags, final long timeout) {
      RenderSystem.assertOnRenderThread();
      return GL32.glClientWaitSync(sync, flags, timeout);
   }

   public static void _glDeleteSync(final long sync) {
      RenderSystem.assertOnRenderThread();
      GL32.glDeleteSync(sync);
   }

   static {
      Arrays.setAll(COLOR_MASK, (var0) -> 15);
      Arrays.setAll(BLEND, (var0) -> new BlendState());
   }

   private static class TextureState {
      public int binding;

      private TextureState() {
         super();
      }
   }

   private static class BlendState {
      public final BooleanState mode = new BooleanState(3042);
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

   private static class ColorLogicState {
      public final BooleanState enable = new BooleanState(3058);
      public int op = 5379;

      private ColorLogicState() {
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
               GL11.glEnable(this.state);
            } else {
               GL11.glDisable(this.state);
            }
         }

      }
   }
}
