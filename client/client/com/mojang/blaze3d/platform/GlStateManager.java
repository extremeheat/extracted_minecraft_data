package com.mojang.blaze3d.platform;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@DontObfuscate
public class GlStateManager {
   private static final boolean ON_LINUX = Util.getPlatform() == Util.OS.LINUX;
   public static final int TEXTURE_COUNT = 12;
   private static final GlStateManager.BlendState BLEND = new GlStateManager.BlendState();
   private static final GlStateManager.DepthState DEPTH = new GlStateManager.DepthState();
   private static final GlStateManager.CullState CULL = new GlStateManager.CullState();
   private static final GlStateManager.PolygonOffsetState POLY_OFFSET = new GlStateManager.PolygonOffsetState();
   private static final GlStateManager.ColorLogicState COLOR_LOGIC = new GlStateManager.ColorLogicState();
   private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
   private static final GlStateManager.ScissorState SCISSOR = new GlStateManager.ScissorState();
   private static int activeTexture;
   private static final GlStateManager.TextureState[] TEXTURES = IntStream.range(0, 12)
      .mapToObj(var0 -> new GlStateManager.TextureState())
      .toArray(GlStateManager.TextureState[]::new);
   private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();

   public GlStateManager() {
      super();
   }

   public static void _disableScissorTest() {
      RenderSystem.assertOnRenderThreadOrInit();
      SCISSOR.mode.disable();
   }

   public static void _enableScissorTest() {
      RenderSystem.assertOnRenderThreadOrInit();
      SCISSOR.mode.enable();
   }

   public static void _scissorBox(int var0, int var1, int var2, int var3) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL20.glScissor(var0, var1, var2, var3);
   }

   public static void _disableDepthTest() {
      RenderSystem.assertOnRenderThreadOrInit();
      DEPTH.mode.disable();
   }

   public static void _enableDepthTest() {
      RenderSystem.assertOnRenderThreadOrInit();
      DEPTH.mode.enable();
   }

   public static void _depthFunc(int var0) {
      RenderSystem.assertOnRenderThreadOrInit();
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

   public static void _blendFunc(int var0, int var1) {
      RenderSystem.assertOnRenderThread();
      if (var0 != BLEND.srcRgb || var1 != BLEND.dstRgb) {
         BLEND.srcRgb = var0;
         BLEND.dstRgb = var1;
         GL11.glBlendFunc(var0, var1);
      }
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

   public static void _blendEquation(int var0) {
      RenderSystem.assertOnRenderThread();
      GL14.glBlendEquation(var0);
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

   public static void glShaderSource(int var0, List<String> var1) {
      RenderSystem.assertOnRenderThread();
      StringBuilder var2 = new StringBuilder();

      for (String var4 : var1) {
         var2.append(var4);
      }

      byte[] var15 = var2.toString().getBytes(Charsets.UTF_8);
      ByteBuffer var16 = MemoryUtil.memAlloc(var15.length + 1);
      var16.put(var15);
      var16.put((byte)0);
      var16.flip();

      try {
         MemoryStack var5 = MemoryStack.stackPush();

         try {
            PointerBuffer var6 = var5.mallocPointer(1);
            var6.put(var16);
            GL20C.nglShaderSource(var0, 1, var6.address0(), 0L);
         } catch (Throwable var13) {
            if (var5 != null) {
               try {
                  var5.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (var5 != null) {
            var5.close();
         }
      } finally {
         MemoryUtil.memFree(var16);
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

   public static void _glUniform2(int var0, IntBuffer var1) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform2iv(var0, var1);
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

   public static void _glUniform4(int var0, IntBuffer var1) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform4iv(var0, var1);
   }

   public static void _glUniform4(int var0, FloatBuffer var1) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniform4fv(var0, var1);
   }

   public static void _glUniformMatrix2(int var0, boolean var1, FloatBuffer var2) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniformMatrix2fv(var0, var1, var2);
   }

   public static void _glUniformMatrix3(int var0, boolean var1, FloatBuffer var2) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniformMatrix3fv(var0, var1, var2);
   }

   public static void _glUniformMatrix4(int var0, boolean var1, FloatBuffer var2) {
      RenderSystem.assertOnRenderThread();
      GL20.glUniformMatrix4fv(var0, var1, var2);
   }

   public static int _glGetAttribLocation(int var0, CharSequence var1) {
      RenderSystem.assertOnRenderThread();
      return GL20.glGetAttribLocation(var0, var1);
   }

   public static void _glBindAttribLocation(int var0, int var1, CharSequence var2) {
      RenderSystem.assertOnRenderThread();
      GL20.glBindAttribLocation(var0, var1, var2);
   }

   public static int _glGenBuffers() {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL15.glGenBuffers();
   }

   public static int _glGenVertexArrays() {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL30.glGenVertexArrays();
   }

   public static void _glBindBuffer(int var0, int var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL15.glBindBuffer(var0, var1);
   }

   public static void _glBindVertexArray(int var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glBindVertexArray(var0);
   }

   public static void _glBufferData(int var0, ByteBuffer var1, int var2) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL15.glBufferData(var0, var1, var2);
   }

   public static void _glBufferData(int var0, long var1, int var3) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL15.glBufferData(var0, var1, var3);
   }

   @Nullable
   public static ByteBuffer _glMapBuffer(int var0, int var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL15.glMapBuffer(var0, var1);
   }

   public static void _glUnmapBuffer(int var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL15.glUnmapBuffer(var0);
   }

   public static void _glDeleteBuffers(int var0) {
      RenderSystem.assertOnRenderThread();
      if (ON_LINUX) {
         GL32C.glBindBuffer(34962, var0);
         GL32C.glBufferData(34962, 0L, 35048);
         GL32C.glBindBuffer(34962, 0);
      }

      GL15.glDeleteBuffers(var0);
   }

   public static void _glCopyTexSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL20.glCopyTexSubImage2D(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static void _glDeleteVertexArrays(int var0) {
      RenderSystem.assertOnRenderThread();
      GL30.glDeleteVertexArrays(var0);
   }

   public static void _glBindFramebuffer(int var0, int var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glBindFramebuffer(var0, var1);
   }

   public static void _glBlitFrameBuffer(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glBlitFramebuffer(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public static void _glBindRenderbuffer(int var0, int var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glBindRenderbuffer(var0, var1);
   }

   public static void _glDeleteRenderbuffers(int var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glDeleteRenderbuffers(var0);
   }

   public static void _glDeleteFramebuffers(int var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glDeleteFramebuffers(var0);
   }

   public static int glGenFramebuffers() {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL30.glGenFramebuffers();
   }

   public static int glGenRenderbuffers() {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL30.glGenRenderbuffers();
   }

   public static void _glRenderbufferStorage(int var0, int var1, int var2, int var3) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glRenderbufferStorage(var0, var1, var2, var3);
   }

   public static void _glFramebufferRenderbuffer(int var0, int var1, int var2, int var3) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glFramebufferRenderbuffer(var0, var1, var2, var3);
   }

   public static int glCheckFramebufferStatus(int var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL30.glCheckFramebufferStatus(var0);
   }

   public static void _glFramebufferTexture2D(int var0, int var1, int var2, int var3, int var4) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL30.glFramebufferTexture2D(var0, var1, var2, var3, var4);
   }

   public static int getBoundFramebuffer() {
      RenderSystem.assertOnRenderThread();
      return _getInteger(36006);
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

   public static void setupLevelDiffuseLighting(Vector3f var0, Vector3f var1, Matrix4f var2) {
      RenderSystem.assertOnRenderThread();
      RenderSystem.setShaderLights(var2.transformDirection(var0, new Vector3f()), var2.transformDirection(var1, new Vector3f()));
   }

   public static void setupGuiFlatDiffuseLighting(Vector3f var0, Vector3f var1) {
      RenderSystem.assertOnRenderThread();
      Matrix4f var2 = new Matrix4f().rotationY(-0.3926991F).rotateX(2.3561945F);
      setupLevelDiffuseLighting(var0, var1, var2);
   }

   public static void setupGui3DDiffuseLighting(Vector3f var0, Vector3f var1) {
      RenderSystem.assertOnRenderThread();
      Matrix4f var2 = new Matrix4f().scaling(1.0F, -1.0F, 1.0F).rotateYXZ(1.0821041F, 3.2375858F, 0.0F).rotateYXZ(-0.3926991F, 2.3561945F, 0.0F);
      setupLevelDiffuseLighting(var0, var1, var2);
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
      if (activeTexture != var0 - 33984) {
         activeTexture = var0 - 33984;
         glActiveTexture(var0);
      }
   }

   public static void _texParameter(int var0, int var1, float var2) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glTexParameterf(var0, var1, var2);
   }

   public static void _texParameter(int var0, int var1, int var2) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glTexParameteri(var0, var1, var2);
   }

   public static int _getTexLevelParameter(int var0, int var1, int var2) {
      RenderSystem.assertInInitPhase();
      return GL11.glGetTexLevelParameteri(var0, var1, var2);
   }

   public static int _genTexture() {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL11.glGenTextures();
   }

   public static void _genTextures(int[] var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glGenTextures(var0);
   }

   public static void _deleteTexture(int var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glDeleteTextures(var0);

      for (GlStateManager.TextureState var4 : TEXTURES) {
         if (var4.binding == var0) {
            var4.binding = -1;
         }
      }
   }

   public static void _deleteTextures(int[] var0) {
      RenderSystem.assertOnRenderThreadOrInit();

      for (GlStateManager.TextureState var4 : TEXTURES) {
         for (int var8 : var0) {
            if (var4.binding == var8) {
               var4.binding = -1;
            }
         }
      }

      GL11.glDeleteTextures(var0);
   }

   public static void _bindTexture(int var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      if (var0 != TEXTURES[activeTexture].binding) {
         TEXTURES[activeTexture].binding = var0;
         GL11.glBindTexture(3553, var0);
      }
   }

   public static int _getActiveTexture() {
      return activeTexture + 33984;
   }

   public static void _texImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, @Nullable IntBuffer var8) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glTexImage2D(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void _texSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glTexSubImage2D(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void upload(int var0, int var1, int var2, int var3, int var4, NativeImage.Format var5, IntBuffer var6, Consumer<IntBuffer> var7) {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> _upload(var0, var1, var2, var3, var4, var5, var6, var7));
      } else {
         _upload(var0, var1, var2, var3, var4, var5, var6, var7);
      }
   }

   private static void _upload(int var0, int var1, int var2, int var3, int var4, NativeImage.Format var5, IntBuffer var6, Consumer<IntBuffer> var7) {
      try {
         RenderSystem.assertOnRenderThreadOrInit();
         _pixelStore(3314, var3);
         _pixelStore(3316, 0);
         _pixelStore(3315, 0);
         var5.setUnpackPixelStoreState();
         GL11.glTexSubImage2D(3553, var0, var1, var2, var3, var4, var5.glFormat(), 5121, var6);
      } finally {
         var7.accept(var6);
      }
   }

   public static void _getTexImage(int var0, int var1, int var2, int var3, long var4) {
      RenderSystem.assertOnRenderThread();
      GL11.glGetTexImage(var0, var1, var2, var3, var4);
   }

   public static void _viewport(int var0, int var1, int var2, int var3) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager.Viewport.INSTANCE.x = var0;
      GlStateManager.Viewport.INSTANCE.y = var1;
      GlStateManager.Viewport.INSTANCE.width = var2;
      GlStateManager.Viewport.INSTANCE.height = var3;
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

   public static void _stencilFunc(int var0, int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      if (var0 != STENCIL.func.func || var0 != STENCIL.func.ref || var0 != STENCIL.func.mask) {
         STENCIL.func.func = var0;
         STENCIL.func.ref = var1;
         STENCIL.func.mask = var2;
         GL11.glStencilFunc(var0, var1, var2);
      }
   }

   public static void _stencilMask(int var0) {
      RenderSystem.assertOnRenderThread();
      if (var0 != STENCIL.mask) {
         STENCIL.mask = var0;
         GL11.glStencilMask(var0);
      }
   }

   public static void _stencilOp(int var0, int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      if (var0 != STENCIL.fail || var1 != STENCIL.zfail || var2 != STENCIL.zpass) {
         STENCIL.fail = var0;
         STENCIL.zfail = var1;
         STENCIL.zpass = var2;
         GL11.glStencilOp(var0, var1, var2);
      }
   }

   public static void _clearDepth(double var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glClearDepth(var0);
   }

   public static void _clearColor(float var0, float var1, float var2, float var3) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glClearColor(var0, var1, var2, var3);
   }

   public static void _clearStencil(int var0) {
      RenderSystem.assertOnRenderThread();
      GL11.glClearStencil(var0);
   }

   public static void _clear(int var0, boolean var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glClear(var0);
      if (var1) {
         _getError();
      }
   }

   public static void _glDrawPixels(int var0, int var1, int var2, int var3, long var4) {
      RenderSystem.assertOnRenderThread();
      GL11.glDrawPixels(var0, var1, var2, var3, var4);
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

   public static void _disableVertexAttribArray(int var0) {
      RenderSystem.assertOnRenderThread();
      GL20.glDisableVertexAttribArray(var0);
   }

   public static void _drawElements(int var0, int var1, int var2, long var3) {
      RenderSystem.assertOnRenderThread();
      GL11.glDrawElements(var0, var1, var2, var3);
   }

   public static void _pixelStore(int var0, int var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      GL11.glPixelStorei(var0, var1);
   }

   public static void _readPixels(int var0, int var1, int var2, int var3, int var4, int var5, ByteBuffer var6) {
      RenderSystem.assertOnRenderThread();
      GL11.glReadPixels(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void _readPixels(int var0, int var1, int var2, int var3, int var4, int var5, long var6) {
      RenderSystem.assertOnRenderThread();
      GL11.glReadPixels(var0, var1, var2, var3, var4, var5, var6);
   }

   public static int _getError() {
      RenderSystem.assertOnRenderThread();
      return GL11.glGetError();
   }

   public static String _getString(int var0) {
      RenderSystem.assertOnRenderThread();
      return GL11.glGetString(var0);
   }

   public static int _getInteger(int var0) {
      RenderSystem.assertOnRenderThreadOrInit();
      return GL11.glGetInteger(var0);
   }

   static class BlendState {
      public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(3042);
      public int srcRgb = 1;
      public int dstRgb = 0;
      public int srcAlpha = 1;
      public int dstAlpha = 0;

      BlendState() {
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
         RenderSystem.assertOnRenderThreadOrInit();
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

   static class ColorLogicState {
      public final GlStateManager.BooleanState enable = new GlStateManager.BooleanState(3058);
      public int op = 5379;

      ColorLogicState() {
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

   static class CullState {
      public final GlStateManager.BooleanState enable = new GlStateManager.BooleanState(2884);
      public int mode = 1029;

      CullState() {
         super();
      }
   }

   static class DepthState {
      public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(2929);
      public boolean mask = true;
      public int func = 513;

      DepthState() {
         super();
      }
   }

   @DontObfuscate
   public static enum DestFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_COLOR(768),
      ZERO(0);

      public final int value;

      private DestFactor(final int nullxx) {
         this.value = nullxx;
      }
   }

   public static enum LogicOp {
      AND(5377),
      AND_INVERTED(5380),
      AND_REVERSE(5378),
      CLEAR(5376),
      COPY(5379),
      COPY_INVERTED(5388),
      EQUIV(5385),
      INVERT(5386),
      NAND(5390),
      NOOP(5381),
      NOR(5384),
      OR(5383),
      OR_INVERTED(5389),
      OR_REVERSE(5387),
      SET(5391),
      XOR(5382);

      public final int value;

      private LogicOp(final int nullxx) {
         this.value = nullxx;
      }
   }

   static class PolygonOffsetState {
      public final GlStateManager.BooleanState fill = new GlStateManager.BooleanState(32823);
      public final GlStateManager.BooleanState line = new GlStateManager.BooleanState(10754);
      public float factor;
      public float units;

      PolygonOffsetState() {
         super();
      }
   }

   static class ScissorState {
      public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(3089);

      ScissorState() {
         super();
      }
   }

   @DontObfuscate
   public static enum SourceFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_ALPHA_SATURATE(776),
      SRC_COLOR(768),
      ZERO(0);

      public final int value;

      private SourceFactor(final int nullxx) {
         this.value = nullxx;
      }
   }

   static class StencilFunc {
      public int func = 519;
      public int ref;
      public int mask = -1;

      StencilFunc() {
         super();
      }
   }

   static class StencilState {
      public final GlStateManager.StencilFunc func = new GlStateManager.StencilFunc();
      public int mask = -1;
      public int fail = 7680;
      public int zfail = 7680;
      public int zpass = 7680;

      StencilState() {
         super();
      }
   }

   static class TextureState {
      public int binding;

      TextureState() {
         super();
      }
   }

   public static enum Viewport {
      INSTANCE;

      protected int x;
      protected int y;
      protected int width;
      protected int height;

      private Viewport() {
      }

      public static int x() {
         return INSTANCE.x;
      }

      public static int y() {
         return INSTANCE.y;
      }

      public static int width() {
         return INSTANCE.width;
      }

      public static int height() {
         return INSTANCE.height;
      }
   }
}
