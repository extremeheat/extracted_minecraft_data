package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

public class GlStateManager {
   private static final FloatBuffer MATRIX_BUFFER = (FloatBuffer)GLX.make(MemoryUtil.memAllocFloat(16), (var0) -> {
      DebugMemoryUntracker.untrack(MemoryUtil.memAddress(var0));
   });
   private static final GlStateManager.AlphaState ALPHA_TEST = new GlStateManager.AlphaState();
   private static final GlStateManager.BooleanState LIGHTING = new GlStateManager.BooleanState(2896);
   private static final GlStateManager.BooleanState[] LIGHT_ENABLE = (GlStateManager.BooleanState[])IntStream.range(0, 8).mapToObj((var0) -> {
      return new GlStateManager.BooleanState(16384 + var0);
   }).toArray((var0) -> {
      return new GlStateManager.BooleanState[var0];
   });
   private static final GlStateManager.ColorMaterialState COLOR_MATERIAL = new GlStateManager.ColorMaterialState();
   private static final GlStateManager.BlendState BLEND = new GlStateManager.BlendState();
   private static final GlStateManager.DepthState DEPTH = new GlStateManager.DepthState();
   private static final GlStateManager.FogState FOG = new GlStateManager.FogState();
   private static final GlStateManager.CullState CULL = new GlStateManager.CullState();
   private static final GlStateManager.PolygonOffsetState POLY_OFFSET = new GlStateManager.PolygonOffsetState();
   private static final GlStateManager.ColorLogicState COLOR_LOGIC = new GlStateManager.ColorLogicState();
   private static final GlStateManager.TexGenState TEX_GEN = new GlStateManager.TexGenState();
   private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
   private static final GlStateManager.ScissorState SCISSOR = new GlStateManager.ScissorState();
   private static final FloatBuffer FLOAT_ARG_BUFFER = MemoryTracker.createFloatBuffer(4);
   private static int activeTexture;
   private static final GlStateManager.TextureState[] TEXTURES = (GlStateManager.TextureState[])IntStream.range(0, 12).mapToObj((var0) -> {
      return new GlStateManager.TextureState();
   }).toArray((var0) -> {
      return new GlStateManager.TextureState[var0];
   });
   private static int shadeModel = 7425;
   private static final GlStateManager.BooleanState RESCALE_NORMAL = new GlStateManager.BooleanState(32826);
   private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();
   private static final GlStateManager.Color COLOR = new GlStateManager.Color();
   private static GlStateManager.FboMode fboMode;
   private static GlStateManager.FboBlitMode fboBlitMode;

   @Deprecated
   public static void _pushLightingAttributes() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPushAttrib(8256);
   }

   @Deprecated
   public static void _pushTextureAttributes() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPushAttrib(270336);
   }

   @Deprecated
   public static void _popAttributes() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPopAttrib();
   }

   @Deprecated
   public static void _disableAlphaTest() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      ALPHA_TEST.mode.disable();
   }

   @Deprecated
   public static void _enableAlphaTest() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      ALPHA_TEST.mode.enable();
   }

   @Deprecated
   public static void _alphaFunc(int var0, float var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (var0 != ALPHA_TEST.func || var1 != ALPHA_TEST.reference) {
         ALPHA_TEST.func = var0;
         ALPHA_TEST.reference = var1;
         GL11.glAlphaFunc(var0, var1);
      }

   }

   @Deprecated
   public static void _enableLighting() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      LIGHTING.enable();
   }

   @Deprecated
   public static void _disableLighting() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      LIGHTING.disable();
   }

   @Deprecated
   public static void _enableLight(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      LIGHT_ENABLE[var0].enable();
   }

   @Deprecated
   public static void _enableColorMaterial() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      COLOR_MATERIAL.enable.enable();
   }

   @Deprecated
   public static void _disableColorMaterial() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      COLOR_MATERIAL.enable.disable();
   }

   @Deprecated
   public static void _colorMaterial(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != COLOR_MATERIAL.face || var1 != COLOR_MATERIAL.mode) {
         COLOR_MATERIAL.face = var0;
         COLOR_MATERIAL.mode = var1;
         GL11.glColorMaterial(var0, var1);
      }

   }

   @Deprecated
   public static void _light(int var0, int var1, FloatBuffer var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glLightfv(var0, var1, var2);
   }

   @Deprecated
   public static void _lightModel(int var0, FloatBuffer var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glLightModelfv(var0, var1);
   }

   @Deprecated
   public static void _normal3f(float var0, float var1, float var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glNormal3f(var0, var1, var2);
   }

   public static void _disableScissorTest() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      SCISSOR.mode.disable();
   }

   public static void _enableScissorTest() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      SCISSOR.mode.enable();
   }

   public static void _scissorBox(int var0, int var1, int var2, int var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL20.glScissor(var0, var1, var2, var3);
   }

   public static void _disableDepthTest() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      DEPTH.mode.disable();
   }

   public static void _enableDepthTest() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      DEPTH.mode.enable();
   }

   public static void _depthFunc(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (var0 != DEPTH.func) {
         DEPTH.func = var0;
         GL11.glDepthFunc(var0);
      }

   }

   public static void _depthMask(boolean var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != DEPTH.mask) {
         DEPTH.mask = var0;
         GL11.glDepthMask(var0);
      }

   }

   public static void _disableBlend() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      BLEND.mode.disable();
   }

   public static void _enableBlend() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      BLEND.mode.enable();
   }

   public static void _blendFunc(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != BLEND.srcRgb || var1 != BLEND.dstRgb) {
         BLEND.srcRgb = var0;
         BLEND.dstRgb = var1;
         GL11.glBlendFunc(var0, var1);
      }

   }

   public static void _blendFuncSeparate(int var0, int var1, int var2, int var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != BLEND.srcRgb || var1 != BLEND.dstRgb || var2 != BLEND.srcAlpha || var3 != BLEND.dstAlpha) {
         BLEND.srcRgb = var0;
         BLEND.dstRgb = var1;
         BLEND.srcAlpha = var2;
         BLEND.dstAlpha = var3;
         glBlendFuncSeparate(var0, var1, var2, var3);
      }

   }

   public static void _blendColor(float var0, float var1, float var2, float var3) {
      GL14.glBlendColor(var0, var1, var2, var3);
   }

   public static void _blendEquation(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL14.glBlendEquation(var0);
   }

   public static String _init_fbo(GLCapabilities var0) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      if (var0.OpenGL30) {
         fboBlitMode = GlStateManager.FboBlitMode.BASE;
      } else if (var0.GL_EXT_framebuffer_blit) {
         fboBlitMode = GlStateManager.FboBlitMode.EXT;
      } else {
         fboBlitMode = GlStateManager.FboBlitMode.NONE;
      }

      if (var0.OpenGL30) {
         fboMode = GlStateManager.FboMode.BASE;
         GlConst.GL_FRAMEBUFFER = 36160;
         GlConst.GL_RENDERBUFFER = 36161;
         GlConst.GL_COLOR_ATTACHMENT0 = 36064;
         GlConst.GL_DEPTH_ATTACHMENT = 36096;
         GlConst.GL_FRAMEBUFFER_COMPLETE = 36053;
         GlConst.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
         GlConst.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
         GlConst.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
         GlConst.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
         return "OpenGL 3.0";
      } else if (var0.GL_ARB_framebuffer_object) {
         fboMode = GlStateManager.FboMode.ARB;
         GlConst.GL_FRAMEBUFFER = 36160;
         GlConst.GL_RENDERBUFFER = 36161;
         GlConst.GL_COLOR_ATTACHMENT0 = 36064;
         GlConst.GL_DEPTH_ATTACHMENT = 36096;
         GlConst.GL_FRAMEBUFFER_COMPLETE = 36053;
         GlConst.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
         GlConst.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
         GlConst.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
         GlConst.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
         return "ARB_framebuffer_object extension";
      } else if (var0.GL_EXT_framebuffer_object) {
         fboMode = GlStateManager.FboMode.EXT;
         GlConst.GL_FRAMEBUFFER = 36160;
         GlConst.GL_RENDERBUFFER = 36161;
         GlConst.GL_COLOR_ATTACHMENT0 = 36064;
         GlConst.GL_DEPTH_ATTACHMENT = 36096;
         GlConst.GL_FRAMEBUFFER_COMPLETE = 36053;
         GlConst.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
         GlConst.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
         GlConst.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
         GlConst.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
         return "EXT_framebuffer_object extension";
      } else {
         throw new IllegalStateException("Could not initialize framebuffer support.");
      }
   }

   public static int glGetProgrami(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glGetProgrami(var0, var1);
   }

   public static void glAttachShader(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glAttachShader(var0, var1);
   }

   public static void glDeleteShader(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glDeleteShader(var0);
   }

   public static int glCreateShader(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glCreateShader(var0);
   }

   public static void glShaderSource(int var0, CharSequence var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glShaderSource(var0, var1);
   }

   public static void glCompileShader(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glCompileShader(var0);
   }

   public static int glGetShaderi(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glGetShaderi(var0, var1);
   }

   public static void _glUseProgram(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUseProgram(var0);
   }

   public static int glCreateProgram() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glCreateProgram();
   }

   public static void glDeleteProgram(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glDeleteProgram(var0);
   }

   public static void glLinkProgram(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glLinkProgram(var0);
   }

   public static int _glGetUniformLocation(int var0, CharSequence var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glGetUniformLocation(var0, var1);
   }

   public static void _glUniform1(int var0, IntBuffer var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform1iv(var0, var1);
   }

   public static void _glUniform1i(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform1i(var0, var1);
   }

   public static void _glUniform1(int var0, FloatBuffer var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform1fv(var0, var1);
   }

   public static void _glUniform2(int var0, IntBuffer var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform2iv(var0, var1);
   }

   public static void _glUniform2(int var0, FloatBuffer var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform2fv(var0, var1);
   }

   public static void _glUniform3(int var0, IntBuffer var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform3iv(var0, var1);
   }

   public static void _glUniform3(int var0, FloatBuffer var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform3fv(var0, var1);
   }

   public static void _glUniform4(int var0, IntBuffer var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform4iv(var0, var1);
   }

   public static void _glUniform4(int var0, FloatBuffer var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniform4fv(var0, var1);
   }

   public static void _glUniformMatrix2(int var0, boolean var1, FloatBuffer var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniformMatrix2fv(var0, var1, var2);
   }

   public static void _glUniformMatrix3(int var0, boolean var1, FloatBuffer var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniformMatrix3fv(var0, var1, var2);
   }

   public static void _glUniformMatrix4(int var0, boolean var1, FloatBuffer var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glUniformMatrix4fv(var0, var1, var2);
   }

   public static int _glGetAttribLocation(int var0, CharSequence var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glGetAttribLocation(var0, var1);
   }

   public static int _glGenBuffers() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      return GL15.glGenBuffers();
   }

   public static void _glBindBuffer(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL15.glBindBuffer(var0, var1);
   }

   public static void _glBufferData(int var0, ByteBuffer var1, int var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL15.glBufferData(var0, var1, var2);
   }

   public static void _glDeleteBuffers(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL15.glDeleteBuffers(var0);
   }

   public static void _glCopyTexSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL20.glCopyTexSubImage2D(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static void _glBindFramebuffer(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboMode) {
      case BASE:
         GL30.glBindFramebuffer(var0, var1);
         break;
      case ARB:
         ARBFramebufferObject.glBindFramebuffer(var0, var1);
         break;
      case EXT:
         EXTFramebufferObject.glBindFramebufferEXT(var0, var1);
      }

   }

   public static int getFramebufferDepthTexture() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboMode) {
      case BASE:
         if (GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36048) == 5890) {
            return GL30.glGetFramebufferAttachmentParameteri(36160, 36096, 36049);
         }
         break;
      case ARB:
         if (ARBFramebufferObject.glGetFramebufferAttachmentParameteri(36160, 36096, 36048) == 5890) {
            return ARBFramebufferObject.glGetFramebufferAttachmentParameteri(36160, 36096, 36049);
         }
         break;
      case EXT:
         if (EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT(36160, 36096, 36048) == 5890) {
            return EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT(36160, 36096, 36049);
         }
      }

      return 0;
   }

   public static void _glBlitFrameBuffer(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboBlitMode) {
      case BASE:
         GL30.glBlitFramebuffer(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
         break;
      case EXT:
         EXTFramebufferBlit.glBlitFramebufferEXT(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
      case NONE:
      }

   }

   public static void _glDeleteFramebuffers(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboMode) {
      case BASE:
         GL30.glDeleteFramebuffers(var0);
         break;
      case ARB:
         ARBFramebufferObject.glDeleteFramebuffers(var0);
         break;
      case EXT:
         EXTFramebufferObject.glDeleteFramebuffersEXT(var0);
      }

   }

   public static int glGenFramebuffers() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboMode) {
      case BASE:
         return GL30.glGenFramebuffers();
      case ARB:
         return ARBFramebufferObject.glGenFramebuffers();
      case EXT:
         return EXTFramebufferObject.glGenFramebuffersEXT();
      default:
         return -1;
      }
   }

   public static int glCheckFramebufferStatus(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboMode) {
      case BASE:
         return GL30.glCheckFramebufferStatus(var0);
      case ARB:
         return ARBFramebufferObject.glCheckFramebufferStatus(var0);
      case EXT:
         return EXTFramebufferObject.glCheckFramebufferStatusEXT(var0);
      default:
         return -1;
      }
   }

   public static void _glFramebufferTexture2D(int var0, int var1, int var2, int var3, int var4) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      switch(fboMode) {
      case BASE:
         GL30.glFramebufferTexture2D(var0, var1, var2, var3, var4);
         break;
      case ARB:
         ARBFramebufferObject.glFramebufferTexture2D(var0, var1, var2, var3, var4);
         break;
      case EXT:
         EXTFramebufferObject.glFramebufferTexture2DEXT(var0, var1, var2, var3, var4);
      }

   }

   @Deprecated
   public static int getActiveTextureName() {
      return TEXTURES[activeTexture].binding;
   }

   public static void glActiveTexture(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL13.glActiveTexture(var0);
   }

   @Deprecated
   public static void _glClientActiveTexture(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL13.glClientActiveTexture(var0);
   }

   @Deprecated
   public static void _glMultiTexCoord2f(int var0, float var1, float var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL13.glMultiTexCoord2f(var0, var1, var2);
   }

   public static void glBlendFuncSeparate(int var0, int var1, int var2, int var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL14.glBlendFuncSeparate(var0, var1, var2, var3);
   }

   public static String glGetShaderInfoLog(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glGetShaderInfoLog(var0, var1);
   }

   public static String glGetProgramInfoLog(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL20.glGetProgramInfoLog(var0, var1);
   }

   public static void setupOutline() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      _texEnv(8960, 8704, 34160);
      color1arg(7681, 34168);
   }

   public static void teardownOutline() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      _texEnv(8960, 8704, 8448);
      color3arg(8448, 5890, 34168, 34166);
   }

   public static void setupOverlayColor(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      _activeTexture(33985);
      _enableTexture();
      _matrixMode(5890);
      _loadIdentity();
      float var2 = 1.0F / (float)(var1 - 1);
      _scalef(var2, var2, var2);
      _matrixMode(5888);
      _bindTexture(var0);
      _texParameter(3553, 10241, 9728);
      _texParameter(3553, 10240, 9728);
      _texParameter(3553, 10242, 10496);
      _texParameter(3553, 10243, 10496);
      _texEnv(8960, 8704, 34160);
      color3arg(34165, 34168, 5890, 5890);
      alpha1arg(7681, 34168);
      _activeTexture(33984);
   }

   public static void teardownOverlayColor() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      _activeTexture(33985);
      _disableTexture();
      _activeTexture(33984);
   }

   private static void color1arg(int var0, int var1) {
      _texEnv(8960, 34161, var0);
      _texEnv(8960, 34176, var1);
      _texEnv(8960, 34192, 768);
   }

   private static void color3arg(int var0, int var1, int var2, int var3) {
      _texEnv(8960, 34161, var0);
      _texEnv(8960, 34176, var1);
      _texEnv(8960, 34192, 768);
      _texEnv(8960, 34177, var2);
      _texEnv(8960, 34193, 768);
      _texEnv(8960, 34178, var3);
      _texEnv(8960, 34194, 770);
   }

   private static void alpha1arg(int var0, int var1) {
      _texEnv(8960, 34162, var0);
      _texEnv(8960, 34184, var1);
      _texEnv(8960, 34200, 770);
   }

   public static void setupLevelDiffuseLighting(Vector3f var0, Vector3f var1, Matrix4f var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      _pushMatrix();
      _loadIdentity();
      _enableLight(0);
      _enableLight(1);
      Vector4f var3 = new Vector4f(var0);
      var3.transform(var2);
      _light(16384, 4611, getBuffer(var3.x(), var3.y(), var3.z(), 0.0F));
      float var4 = 0.6F;
      _light(16384, 4609, getBuffer(0.6F, 0.6F, 0.6F, 1.0F));
      _light(16384, 4608, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      _light(16384, 4610, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      Vector4f var5 = new Vector4f(var1);
      var5.transform(var2);
      _light(16385, 4611, getBuffer(var5.x(), var5.y(), var5.z(), 0.0F));
      _light(16385, 4609, getBuffer(0.6F, 0.6F, 0.6F, 1.0F));
      _light(16385, 4608, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      _light(16385, 4610, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      _shadeModel(7424);
      float var6 = 0.4F;
      _lightModel(2899, getBuffer(0.4F, 0.4F, 0.4F, 1.0F));
      _popMatrix();
   }

   public static void setupGuiFlatDiffuseLighting(Vector3f var0, Vector3f var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      Matrix4f var2 = new Matrix4f();
      var2.setIdentity();
      var2.multiply(Matrix4f.createScaleMatrix(1.0F, -1.0F, 1.0F));
      var2.multiply(Vector3f.YP.rotationDegrees(-22.5F));
      var2.multiply(Vector3f.XP.rotationDegrees(135.0F));
      setupLevelDiffuseLighting(var0, var1, var2);
   }

   public static void setupGui3DDiffuseLighting(Vector3f var0, Vector3f var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      Matrix4f var2 = new Matrix4f();
      var2.setIdentity();
      var2.multiply(Vector3f.YP.rotationDegrees(62.0F));
      var2.multiply(Vector3f.XP.rotationDegrees(185.5F));
      var2.multiply(Matrix4f.createScaleMatrix(1.0F, -1.0F, 1.0F));
      var2.multiply(Vector3f.YP.rotationDegrees(-22.5F));
      var2.multiply(Vector3f.XP.rotationDegrees(135.0F));
      setupLevelDiffuseLighting(var0, var1, var2);
   }

   private static FloatBuffer getBuffer(float var0, float var1, float var2, float var3) {
      FLOAT_ARG_BUFFER.clear();
      FLOAT_ARG_BUFFER.put(var0).put(var1).put(var2).put(var3);
      FLOAT_ARG_BUFFER.flip();
      return FLOAT_ARG_BUFFER;
   }

   public static void setupEndPortalTexGen() {
      _texGenMode(GlStateManager.TexGen.S, 9216);
      _texGenMode(GlStateManager.TexGen.T, 9216);
      _texGenMode(GlStateManager.TexGen.R, 9216);
      _texGenParam(GlStateManager.TexGen.S, 9474, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
      _texGenParam(GlStateManager.TexGen.T, 9474, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
      _texGenParam(GlStateManager.TexGen.R, 9474, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
      _enableTexGen(GlStateManager.TexGen.S);
      _enableTexGen(GlStateManager.TexGen.T);
      _enableTexGen(GlStateManager.TexGen.R);
   }

   public static void clearTexGen() {
      _disableTexGen(GlStateManager.TexGen.S);
      _disableTexGen(GlStateManager.TexGen.T);
      _disableTexGen(GlStateManager.TexGen.R);
   }

   public static void mulTextureByProjModelView() {
      _getMatrix(2983, MATRIX_BUFFER);
      _multMatrix(MATRIX_BUFFER);
      _getMatrix(2982, MATRIX_BUFFER);
      _multMatrix(MATRIX_BUFFER);
   }

   @Deprecated
   public static void _enableFog() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      FOG.enable.enable();
   }

   @Deprecated
   public static void _disableFog() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      FOG.enable.disable();
   }

   @Deprecated
   public static void _fogMode(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != FOG.mode) {
         FOG.mode = var0;
         _fogi(2917, var0);
      }

   }

   @Deprecated
   public static void _fogDensity(float var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != FOG.density) {
         FOG.density = var0;
         GL11.glFogf(2914, var0);
      }

   }

   @Deprecated
   public static void _fogStart(float var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != FOG.start) {
         FOG.start = var0;
         GL11.glFogf(2915, var0);
      }

   }

   @Deprecated
   public static void _fogEnd(float var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != FOG.end) {
         FOG.end = var0;
         GL11.glFogf(2916, var0);
      }

   }

   @Deprecated
   public static void _fog(int var0, float[] var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glFogfv(var0, var1);
   }

   @Deprecated
   public static void _fogi(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glFogi(var0, var1);
   }

   public static void _enableCull() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      CULL.enable.enable();
   }

   public static void _disableCull() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      CULL.enable.disable();
   }

   public static void _polygonMode(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPolygonMode(var0, var1);
   }

   public static void _enablePolygonOffset() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      POLY_OFFSET.fill.enable();
   }

   public static void _disablePolygonOffset() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      POLY_OFFSET.fill.disable();
   }

   public static void _enableLineOffset() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      POLY_OFFSET.line.enable();
   }

   public static void _disableLineOffset() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      POLY_OFFSET.line.disable();
   }

   public static void _polygonOffset(float var0, float var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != POLY_OFFSET.factor || var1 != POLY_OFFSET.units) {
         POLY_OFFSET.factor = var0;
         POLY_OFFSET.units = var1;
         GL11.glPolygonOffset(var0, var1);
      }

   }

   public static void _enableColorLogicOp() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      COLOR_LOGIC.enable.enable();
   }

   public static void _disableColorLogicOp() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      COLOR_LOGIC.enable.disable();
   }

   public static void _logicOp(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != COLOR_LOGIC.op) {
         COLOR_LOGIC.op = var0;
         GL11.glLogicOp(var0);
      }

   }

   @Deprecated
   public static void _enableTexGen(GlStateManager.TexGen var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      getTexGen(var0).enable.enable();
   }

   @Deprecated
   public static void _disableTexGen(GlStateManager.TexGen var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      getTexGen(var0).enable.disable();
   }

   @Deprecated
   public static void _texGenMode(GlStateManager.TexGen var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GlStateManager.TexGenCoord var2 = getTexGen(var0);
      if (var1 != var2.mode) {
         var2.mode = var1;
         GL11.glTexGeni(var2.coord, 9472, var1);
      }

   }

   @Deprecated
   public static void _texGenParam(GlStateManager.TexGen var0, int var1, FloatBuffer var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glTexGenfv(getTexGen(var0).coord, var1, var2);
   }

   @Deprecated
   private static GlStateManager.TexGenCoord getTexGen(GlStateManager.TexGen var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      switch(var0) {
      case S:
         return TEX_GEN.s;
      case T:
         return TEX_GEN.t;
      case R:
         return TEX_GEN.r;
      case Q:
         return TEX_GEN.q;
      default:
         return TEX_GEN.s;
      }
   }

   public static void _activeTexture(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (activeTexture != var0 - '\u84c0') {
         activeTexture = var0 - '\u84c0';
         glActiveTexture(var0);
      }

   }

   public static void _enableTexture() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      TEXTURES[activeTexture].enable.enable();
   }

   public static void _disableTexture() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      TEXTURES[activeTexture].enable.disable();
   }

   @Deprecated
   public static void _texEnv(int var0, int var1, int var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glTexEnvi(var0, var1, var2);
   }

   public static void _texParameter(int var0, int var1, float var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glTexParameterf(var0, var1, var2);
   }

   public static void _texParameter(int var0, int var1, int var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glTexParameteri(var0, var1, var2);
   }

   public static int _getTexLevelParameter(int var0, int var1, int var2) {
      RenderSystem.assertThread(RenderSystem::isInInitPhase);
      return GL11.glGetTexLevelParameteri(var0, var1, var2);
   }

   public static int _genTexture() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      return GL11.glGenTextures();
   }

   public static void _genTextures(int[] var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glGenTextures(var0);
   }

   public static void _deleteTexture(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glDeleteTextures(var0);
      GlStateManager.TextureState[] var1 = TEXTURES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GlStateManager.TextureState var4 = var1[var3];
         if (var4.binding == var0) {
            var4.binding = -1;
         }
      }

   }

   public static void _deleteTextures(int[] var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager.TextureState[] var1 = TEXTURES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GlStateManager.TextureState var4 = var1[var3];
         int[] var5 = var0;
         int var6 = var0.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            int var8 = var5[var7];
            if (var4.binding == var8) {
               var4.binding = -1;
            }
         }
      }

      GL11.glDeleteTextures(var0);
   }

   public static void _bindTexture(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (var0 != TEXTURES[activeTexture].binding) {
         TEXTURES[activeTexture].binding = var0;
         GL11.glBindTexture(3553, var0);
      }

   }

   public static void _texImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, @Nullable IntBuffer var8) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glTexImage2D(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void _texSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glTexSubImage2D(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void _getTexImage(int var0, int var1, int var2, int var3, long var4) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glGetTexImage(var0, var1, var2, var3, var4);
   }

   @Deprecated
   public static void _shadeModel(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (var0 != shadeModel) {
         shadeModel = var0;
         GL11.glShadeModel(var0);
      }

   }

   @Deprecated
   public static void _enableRescaleNormal() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      RESCALE_NORMAL.enable();
   }

   @Deprecated
   public static void _disableRescaleNormal() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      RESCALE_NORMAL.disable();
   }

   public static void _viewport(int var0, int var1, int var2, int var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager.Viewport.INSTANCE.x = var0;
      GlStateManager.Viewport.INSTANCE.y = var1;
      GlStateManager.Viewport.INSTANCE.width = var2;
      GlStateManager.Viewport.INSTANCE.height = var3;
      GL11.glViewport(var0, var1, var2, var3);
   }

   public static void _colorMask(boolean var0, boolean var1, boolean var2, boolean var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != COLOR_MASK.red || var1 != COLOR_MASK.green || var2 != COLOR_MASK.blue || var3 != COLOR_MASK.alpha) {
         COLOR_MASK.red = var0;
         COLOR_MASK.green = var1;
         COLOR_MASK.blue = var2;
         COLOR_MASK.alpha = var3;
         GL11.glColorMask(var0, var1, var2, var3);
      }

   }

   public static void _stencilFunc(int var0, int var1, int var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != STENCIL.func.func || var0 != STENCIL.func.ref || var0 != STENCIL.func.mask) {
         STENCIL.func.func = var0;
         STENCIL.func.ref = var1;
         STENCIL.func.mask = var2;
         GL11.glStencilFunc(var0, var1, var2);
      }

   }

   public static void _stencilMask(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != STENCIL.mask) {
         STENCIL.mask = var0;
         GL11.glStencilMask(var0);
      }

   }

   public static void _stencilOp(int var0, int var1, int var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != STENCIL.fail || var1 != STENCIL.zfail || var2 != STENCIL.zpass) {
         STENCIL.fail = var0;
         STENCIL.zfail = var1;
         STENCIL.zpass = var2;
         GL11.glStencilOp(var0, var1, var2);
      }

   }

   public static void _clearDepth(double var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glClearDepth(var0);
   }

   public static void _clearColor(float var0, float var1, float var2, float var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glClearColor(var0, var1, var2, var3);
   }

   public static void _clearStencil(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glClearStencil(var0);
   }

   public static void _clear(int var0, boolean var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glClear(var0);
      if (var1) {
         _getError();
      }

   }

   @Deprecated
   public static void _matrixMode(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glMatrixMode(var0);
   }

   @Deprecated
   public static void _loadIdentity() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glLoadIdentity();
   }

   @Deprecated
   public static void _pushMatrix() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPushMatrix();
   }

   @Deprecated
   public static void _popMatrix() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPopMatrix();
   }

   @Deprecated
   public static void _getMatrix(int var0, FloatBuffer var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glGetFloatv(var0, var1);
   }

   @Deprecated
   public static void _ortho(double var0, double var2, double var4, double var6, double var8, double var10) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glOrtho(var0, var2, var4, var6, var8, var10);
   }

   @Deprecated
   public static void _rotatef(float var0, float var1, float var2, float var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glRotatef(var0, var1, var2, var3);
   }

   @Deprecated
   public static void _scalef(float var0, float var1, float var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glScalef(var0, var1, var2);
   }

   @Deprecated
   public static void _scaled(double var0, double var2, double var4) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glScaled(var0, var2, var4);
   }

   @Deprecated
   public static void _translatef(float var0, float var1, float var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glTranslatef(var0, var1, var2);
   }

   @Deprecated
   public static void _translated(double var0, double var2, double var4) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glTranslated(var0, var2, var4);
   }

   @Deprecated
   public static void _multMatrix(FloatBuffer var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glMultMatrixf(var0);
   }

   @Deprecated
   public static void _multMatrix(Matrix4f var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      var0.store(MATRIX_BUFFER);
      MATRIX_BUFFER.rewind();
      _multMatrix(MATRIX_BUFFER);
   }

   @Deprecated
   public static void _color4f(float var0, float var1, float var2, float var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      if (var0 != COLOR.r || var1 != COLOR.g || var2 != COLOR.b || var3 != COLOR.a) {
         COLOR.r = var0;
         COLOR.g = var1;
         COLOR.b = var2;
         COLOR.a = var3;
         GL11.glColor4f(var0, var1, var2, var3);
      }

   }

   @Deprecated
   public static void _clearCurrentColor() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      COLOR.r = -1.0F;
      COLOR.g = -1.0F;
      COLOR.b = -1.0F;
      COLOR.a = -1.0F;
   }

   @Deprecated
   public static void _normalPointer(int var0, int var1, long var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glNormalPointer(var0, var1, var2);
   }

   @Deprecated
   public static void _texCoordPointer(int var0, int var1, int var2, long var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glTexCoordPointer(var0, var1, var2, var3);
   }

   @Deprecated
   public static void _vertexPointer(int var0, int var1, int var2, long var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glVertexPointer(var0, var1, var2, var3);
   }

   @Deprecated
   public static void _colorPointer(int var0, int var1, int var2, long var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glColorPointer(var0, var1, var2, var3);
   }

   public static void _vertexAttribPointer(int var0, int var1, int var2, boolean var3, int var4, long var5) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glVertexAttribPointer(var0, var1, var2, var3, var4, var5);
   }

   @Deprecated
   public static void _enableClientState(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glEnableClientState(var0);
   }

   @Deprecated
   public static void _disableClientState(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glDisableClientState(var0);
   }

   public static void _enableVertexAttribArray(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glEnableVertexAttribArray(var0);
   }

   public static void _disableVertexAttribArray(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL20.glEnableVertexAttribArray(var0);
   }

   public static void _drawArrays(int var0, int var1, int var2) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glDrawArrays(var0, var1, var2);
   }

   public static void _lineWidth(float var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glLineWidth(var0);
   }

   public static void _pixelStore(int var0, int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GL11.glPixelStorei(var0, var1);
   }

   public static void _pixelTransfer(int var0, float var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glPixelTransferf(var0, var1);
   }

   public static void _readPixels(int var0, int var1, int var2, int var3, int var4, int var5, ByteBuffer var6) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GL11.glReadPixels(var0, var1, var2, var3, var4, var5, var6);
   }

   public static int _getError() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL11.glGetError();
   }

   public static String _getString(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      return GL11.glGetString(var0);
   }

   public static int _getInteger(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      return GL11.glGetInteger(var0);
   }

   public static boolean supportsFramebufferBlit() {
      return fboBlitMode != GlStateManager.FboBlitMode.NONE;
   }

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

      private DestFactor(int var3) {
         this.value = var3;
      }
   }

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

      private SourceFactor(int var3) {
         this.value = var3;
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
         RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
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

   @Deprecated
   static class Color {
      public float r;
      public float g;
      public float b;
      public float a;

      public Color() {
         this(1.0F, 1.0F, 1.0F, 1.0F);
      }

      public Color(float var1, float var2, float var3, float var4) {
         super();
         this.r = 1.0F;
         this.g = 1.0F;
         this.b = 1.0F;
         this.a = 1.0F;
         this.r = var1;
         this.g = var2;
         this.b = var3;
         this.a = var4;
      }
   }

   static class ColorMask {
      public boolean red;
      public boolean green;
      public boolean blue;
      public boolean alpha;

      private ColorMask() {
         super();
         this.red = true;
         this.green = true;
         this.blue = true;
         this.alpha = true;
      }

      // $FF: synthetic method
      ColorMask(Object var1) {
         this();
      }
   }

   @Deprecated
   public static enum TexGen {
      S,
      T,
      R,
      Q;

      private TexGen() {
      }
   }

   @Deprecated
   static class TexGenCoord {
      public final GlStateManager.BooleanState enable;
      public final int coord;
      public int mode = -1;

      public TexGenCoord(int var1, int var2) {
         super();
         this.coord = var1;
         this.enable = new GlStateManager.BooleanState(var2);
      }
   }

   @Deprecated
   static class TexGenState {
      public final GlStateManager.TexGenCoord s;
      public final GlStateManager.TexGenCoord t;
      public final GlStateManager.TexGenCoord r;
      public final GlStateManager.TexGenCoord q;

      private TexGenState() {
         super();
         this.s = new GlStateManager.TexGenCoord(8192, 3168);
         this.t = new GlStateManager.TexGenCoord(8193, 3169);
         this.r = new GlStateManager.TexGenCoord(8194, 3170);
         this.q = new GlStateManager.TexGenCoord(8195, 3171);
      }

      // $FF: synthetic method
      TexGenState(Object var1) {
         this();
      }
   }

   static class ScissorState {
      public final GlStateManager.BooleanState mode;

      private ScissorState() {
         super();
         this.mode = new GlStateManager.BooleanState(3089);
      }

      // $FF: synthetic method
      ScissorState(Object var1) {
         this();
      }
   }

   static class StencilState {
      public final GlStateManager.StencilFunc func;
      public int mask;
      public int fail;
      public int zfail;
      public int zpass;

      private StencilState() {
         super();
         this.func = new GlStateManager.StencilFunc();
         this.mask = -1;
         this.fail = 7680;
         this.zfail = 7680;
         this.zpass = 7680;
      }

      // $FF: synthetic method
      StencilState(Object var1) {
         this();
      }
   }

   static class StencilFunc {
      public int func;
      public int ref;
      public int mask;

      private StencilFunc() {
         super();
         this.func = 519;
         this.mask = -1;
      }

      // $FF: synthetic method
      StencilFunc(Object var1) {
         this();
      }
   }

   static class ColorLogicState {
      public final GlStateManager.BooleanState enable;
      public int op;

      private ColorLogicState() {
         super();
         this.enable = new GlStateManager.BooleanState(3058);
         this.op = 5379;
      }

      // $FF: synthetic method
      ColorLogicState(Object var1) {
         this();
      }
   }

   static class PolygonOffsetState {
      public final GlStateManager.BooleanState fill;
      public final GlStateManager.BooleanState line;
      public float factor;
      public float units;

      private PolygonOffsetState() {
         super();
         this.fill = new GlStateManager.BooleanState(32823);
         this.line = new GlStateManager.BooleanState(10754);
      }

      // $FF: synthetic method
      PolygonOffsetState(Object var1) {
         this();
      }
   }

   static class CullState {
      public final GlStateManager.BooleanState enable;
      public int mode;

      private CullState() {
         super();
         this.enable = new GlStateManager.BooleanState(2884);
         this.mode = 1029;
      }

      // $FF: synthetic method
      CullState(Object var1) {
         this();
      }
   }

   @Deprecated
   static class FogState {
      public final GlStateManager.BooleanState enable;
      public int mode;
      public float density;
      public float start;
      public float end;

      private FogState() {
         super();
         this.enable = new GlStateManager.BooleanState(2912);
         this.mode = 2048;
         this.density = 1.0F;
         this.end = 1.0F;
      }

      // $FF: synthetic method
      FogState(Object var1) {
         this();
      }
   }

   static class DepthState {
      public final GlStateManager.BooleanState mode;
      public boolean mask;
      public int func;

      private DepthState() {
         super();
         this.mode = new GlStateManager.BooleanState(2929);
         this.mask = true;
         this.func = 513;
      }

      // $FF: synthetic method
      DepthState(Object var1) {
         this();
      }
   }

   static class BlendState {
      public final GlStateManager.BooleanState mode;
      public int srcRgb;
      public int dstRgb;
      public int srcAlpha;
      public int dstAlpha;

      private BlendState() {
         super();
         this.mode = new GlStateManager.BooleanState(3042);
         this.srcRgb = 1;
         this.dstRgb = 0;
         this.srcAlpha = 1;
         this.dstAlpha = 0;
      }

      // $FF: synthetic method
      BlendState(Object var1) {
         this();
      }
   }

   @Deprecated
   static class ColorMaterialState {
      public final GlStateManager.BooleanState enable;
      public int face;
      public int mode;

      private ColorMaterialState() {
         super();
         this.enable = new GlStateManager.BooleanState(2903);
         this.face = 1032;
         this.mode = 5634;
      }

      // $FF: synthetic method
      ColorMaterialState(Object var1) {
         this();
      }
   }

   @Deprecated
   static class AlphaState {
      public final GlStateManager.BooleanState mode;
      public int func;
      public float reference;

      private AlphaState() {
         super();
         this.mode = new GlStateManager.BooleanState(3008);
         this.func = 519;
         this.reference = -1.0F;
      }

      // $FF: synthetic method
      AlphaState(Object var1) {
         this();
      }
   }

   static class TextureState {
      public final GlStateManager.BooleanState enable;
      public int binding;

      private TextureState() {
         super();
         this.enable = new GlStateManager.BooleanState(3553);
      }

      // $FF: synthetic method
      TextureState(Object var1) {
         this();
      }
   }

   public static enum FboBlitMode {
      BASE,
      EXT,
      NONE;

      private FboBlitMode() {
      }
   }

   public static enum FboMode {
      BASE,
      ARB,
      EXT;

      private FboMode() {
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

      private LogicOp(int var3) {
         this.value = var3;
      }
   }

   @Deprecated
   public static enum FogMode {
      LINEAR(9729),
      EXP(2048),
      EXP2(2049);

      public final int value;

      private FogMode(int var3) {
         this.value = var3;
      }
   }
}
