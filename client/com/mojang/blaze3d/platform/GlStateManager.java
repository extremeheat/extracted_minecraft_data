package com.mojang.blaze3d.platform;

import com.mojang.math.Matrix4f;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryUtil;

public class GlStateManager {
   private static final int LIGHT_COUNT = 8;
   private static final int TEXTURE_COUNT = 8;
   private static final FloatBuffer MATRIX_BUFFER = (FloatBuffer)GLX.make(MemoryUtil.memAllocFloat(16), (var0) -> {
      DebugMemoryUntracker.untrack(MemoryUtil.memAddress(var0));
   });
   private static final FloatBuffer COLOR_BUFFER = (FloatBuffer)GLX.make(MemoryUtil.memAllocFloat(4), (var0) -> {
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
   private static final GlStateManager.ClearState CLEAR = new GlStateManager.ClearState();
   private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
   private static final GlStateManager.BooleanState NORMALIZE = new GlStateManager.BooleanState(2977);
   private static int activeTexture;
   private static final GlStateManager.TextureState[] TEXTURES = (GlStateManager.TextureState[])IntStream.range(0, 8).mapToObj((var0) -> {
      return new GlStateManager.TextureState();
   }).toArray((var0) -> {
      return new GlStateManager.TextureState[var0];
   });
   private static int shadeModel = 7425;
   private static final GlStateManager.BooleanState RESCALE_NORMAL = new GlStateManager.BooleanState(32826);
   private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();
   private static final GlStateManager.Color COLOR = new GlStateManager.Color();
   private static final float DEFAULTALPHACUTOFF = 0.1F;

   public GlStateManager() {
      super();
   }

   public static void pushLightingAttributes() {
      GL11.glPushAttrib(8256);
   }

   public static void pushTextureAttributes() {
      GL11.glPushAttrib(270336);
   }

   public static void popAttributes() {
      GL11.glPopAttrib();
   }

   public static void disableAlphaTest() {
      ALPHA_TEST.mode.disable();
   }

   public static void enableAlphaTest() {
      ALPHA_TEST.mode.enable();
   }

   public static void alphaFunc(int var0, float var1) {
      if (var0 != ALPHA_TEST.func || var1 != ALPHA_TEST.reference) {
         ALPHA_TEST.func = var0;
         ALPHA_TEST.reference = var1;
         GL11.glAlphaFunc(var0, var1);
      }

   }

   public static void enableLighting() {
      LIGHTING.enable();
   }

   public static void disableLighting() {
      LIGHTING.disable();
   }

   public static void enableLight(int var0) {
      LIGHT_ENABLE[var0].enable();
   }

   public static void disableLight(int var0) {
      LIGHT_ENABLE[var0].disable();
   }

   public static void enableColorMaterial() {
      COLOR_MATERIAL.enable.enable();
   }

   public static void disableColorMaterial() {
      COLOR_MATERIAL.enable.disable();
   }

   public static void colorMaterial(int var0, int var1) {
      if (var0 != COLOR_MATERIAL.face || var1 != COLOR_MATERIAL.mode) {
         COLOR_MATERIAL.face = var0;
         COLOR_MATERIAL.mode = var1;
         GL11.glColorMaterial(var0, var1);
      }

   }

   public static void light(int var0, int var1, FloatBuffer var2) {
      GL11.glLightfv(var0, var1, var2);
   }

   public static void lightModel(int var0, FloatBuffer var1) {
      GL11.glLightModelfv(var0, var1);
   }

   public static void normal3f(float var0, float var1, float var2) {
      GL11.glNormal3f(var0, var1, var2);
   }

   public static void disableDepthTest() {
      DEPTH.mode.disable();
   }

   public static void enableDepthTest() {
      DEPTH.mode.enable();
   }

   public static void depthFunc(int var0) {
      if (var0 != DEPTH.func) {
         DEPTH.func = var0;
         GL11.glDepthFunc(var0);
      }

   }

   public static void depthMask(boolean var0) {
      if (var0 != DEPTH.mask) {
         DEPTH.mask = var0;
         GL11.glDepthMask(var0);
      }

   }

   public static void disableBlend() {
      BLEND.mode.disable();
   }

   public static void enableBlend() {
      BLEND.mode.enable();
   }

   public static void blendFunc(GlStateManager.SourceFactor var0, GlStateManager.DestFactor var1) {
      blendFunc(var0.value, var1.value);
   }

   public static void blendFunc(int var0, int var1) {
      if (var0 != BLEND.srcRgb || var1 != BLEND.dstRgb) {
         BLEND.srcRgb = var0;
         BLEND.dstRgb = var1;
         GL11.glBlendFunc(var0, var1);
      }

   }

   public static void blendFuncSeparate(GlStateManager.SourceFactor var0, GlStateManager.DestFactor var1, GlStateManager.SourceFactor var2, GlStateManager.DestFactor var3) {
      blendFuncSeparate(var0.value, var1.value, var2.value, var3.value);
   }

   public static void blendFuncSeparate(int var0, int var1, int var2, int var3) {
      if (var0 != BLEND.srcRgb || var1 != BLEND.dstRgb || var2 != BLEND.srcAlpha || var3 != BLEND.dstAlpha) {
         BLEND.srcRgb = var0;
         BLEND.dstRgb = var1;
         BLEND.srcAlpha = var2;
         BLEND.dstAlpha = var3;
         GLX.glBlendFuncSeparate(var0, var1, var2, var3);
      }

   }

   public static void blendEquation(int var0) {
      GL14.glBlendEquation(var0);
   }

   public static void setupSolidRenderingTextureCombine(int var0) {
      COLOR_BUFFER.put(0, (float)(var0 >> 16 & 255) / 255.0F);
      COLOR_BUFFER.put(1, (float)(var0 >> 8 & 255) / 255.0F);
      COLOR_BUFFER.put(2, (float)(var0 >> 0 & 255) / 255.0F);
      COLOR_BUFFER.put(3, (float)(var0 >> 24 & 255) / 255.0F);
      texEnv(8960, 8705, COLOR_BUFFER);
      texEnv(8960, 8704, 34160);
      texEnv(8960, 34161, 7681);
      texEnv(8960, 34176, 34166);
      texEnv(8960, 34192, 768);
      texEnv(8960, 34162, 7681);
      texEnv(8960, 34184, 5890);
      texEnv(8960, 34200, 770);
   }

   public static void tearDownSolidRenderingTextureCombine() {
      texEnv(8960, 8704, 8448);
      texEnv(8960, 34161, 8448);
      texEnv(8960, 34162, 8448);
      texEnv(8960, 34176, 5890);
      texEnv(8960, 34184, 5890);
      texEnv(8960, 34192, 768);
      texEnv(8960, 34200, 770);
   }

   public static void enableFog() {
      FOG.enable.enable();
   }

   public static void disableFog() {
      FOG.enable.disable();
   }

   public static void fogMode(GlStateManager.FogMode var0) {
      fogMode(var0.value);
   }

   private static void fogMode(int var0) {
      if (var0 != FOG.mode) {
         FOG.mode = var0;
         GL11.glFogi(2917, var0);
      }

   }

   public static void fogDensity(float var0) {
      if (var0 != FOG.density) {
         FOG.density = var0;
         GL11.glFogf(2914, var0);
      }

   }

   public static void fogStart(float var0) {
      if (var0 != FOG.start) {
         FOG.start = var0;
         GL11.glFogf(2915, var0);
      }

   }

   public static void fogEnd(float var0) {
      if (var0 != FOG.end) {
         FOG.end = var0;
         GL11.glFogf(2916, var0);
      }

   }

   public static void fog(int var0, FloatBuffer var1) {
      GL11.glFogfv(var0, var1);
   }

   public static void fogi(int var0, int var1) {
      GL11.glFogi(var0, var1);
   }

   public static void enableCull() {
      CULL.enable.enable();
   }

   public static void disableCull() {
      CULL.enable.disable();
   }

   public static void cullFace(GlStateManager.CullFace var0) {
      cullFace(var0.value);
   }

   private static void cullFace(int var0) {
      if (var0 != CULL.mode) {
         CULL.mode = var0;
         GL11.glCullFace(var0);
      }

   }

   public static void polygonMode(int var0, int var1) {
      GL11.glPolygonMode(var0, var1);
   }

   public static void enablePolygonOffset() {
      POLY_OFFSET.fill.enable();
   }

   public static void disablePolygonOffset() {
      POLY_OFFSET.fill.disable();
   }

   public static void enableLineOffset() {
      POLY_OFFSET.line.enable();
   }

   public static void disableLineOffset() {
      POLY_OFFSET.line.disable();
   }

   public static void polygonOffset(float var0, float var1) {
      if (var0 != POLY_OFFSET.factor || var1 != POLY_OFFSET.units) {
         POLY_OFFSET.factor = var0;
         POLY_OFFSET.units = var1;
         GL11.glPolygonOffset(var0, var1);
      }

   }

   public static void enableColorLogicOp() {
      COLOR_LOGIC.enable.enable();
   }

   public static void disableColorLogicOp() {
      COLOR_LOGIC.enable.disable();
   }

   public static void logicOp(GlStateManager.LogicOp var0) {
      logicOp(var0.value);
   }

   public static void logicOp(int var0) {
      if (var0 != COLOR_LOGIC.op) {
         COLOR_LOGIC.op = var0;
         GL11.glLogicOp(var0);
      }

   }

   public static void enableTexGen(GlStateManager.TexGen var0) {
      getTexGen(var0).enable.enable();
   }

   public static void disableTexGen(GlStateManager.TexGen var0) {
      getTexGen(var0).enable.disable();
   }

   public static void texGenMode(GlStateManager.TexGen var0, int var1) {
      GlStateManager.TexGenCoord var2 = getTexGen(var0);
      if (var1 != var2.mode) {
         var2.mode = var1;
         GL11.glTexGeni(var2.coord, 9472, var1);
      }

   }

   public static void texGenParam(GlStateManager.TexGen var0, int var1, FloatBuffer var2) {
      GL11.glTexGenfv(getTexGen(var0).coord, var1, var2);
   }

   private static GlStateManager.TexGenCoord getTexGen(GlStateManager.TexGen var0) {
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

   public static void activeTexture(int var0) {
      if (activeTexture != var0 - GLX.GL_TEXTURE0) {
         activeTexture = var0 - GLX.GL_TEXTURE0;
         GLX.glActiveTexture(var0);
      }

   }

   public static void enableTexture() {
      TEXTURES[activeTexture].enable.enable();
   }

   public static void disableTexture() {
      TEXTURES[activeTexture].enable.disable();
   }

   public static void texEnv(int var0, int var1, FloatBuffer var2) {
      GL11.glTexEnvfv(var0, var1, var2);
   }

   public static void texEnv(int var0, int var1, int var2) {
      GL11.glTexEnvi(var0, var1, var2);
   }

   public static void texEnv(int var0, int var1, float var2) {
      GL11.glTexEnvf(var0, var1, var2);
   }

   public static void texParameter(int var0, int var1, float var2) {
      GL11.glTexParameterf(var0, var1, var2);
   }

   public static void texParameter(int var0, int var1, int var2) {
      GL11.glTexParameteri(var0, var1, var2);
   }

   public static int getTexLevelParameter(int var0, int var1, int var2) {
      return GL11.glGetTexLevelParameteri(var0, var1, var2);
   }

   public static int genTexture() {
      return GL11.glGenTextures();
   }

   public static void deleteTexture(int var0) {
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

   public static void bindTexture(int var0) {
      if (var0 != TEXTURES[activeTexture].binding) {
         TEXTURES[activeTexture].binding = var0;
         GL11.glBindTexture(3553, var0);
      }

   }

   public static void texImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, @Nullable IntBuffer var8) {
      GL11.glTexImage2D(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void texSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8) {
      GL11.glTexSubImage2D(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void copyTexSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      GL11.glCopyTexSubImage2D(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static void getTexImage(int var0, int var1, int var2, int var3, long var4) {
      GL11.glGetTexImage(var0, var1, var2, var3, var4);
   }

   public static void enableNormalize() {
      NORMALIZE.enable();
   }

   public static void disableNormalize() {
      NORMALIZE.disable();
   }

   public static void shadeModel(int var0) {
      if (var0 != shadeModel) {
         shadeModel = var0;
         GL11.glShadeModel(var0);
      }

   }

   public static void enableRescaleNormal() {
      RESCALE_NORMAL.enable();
   }

   public static void disableRescaleNormal() {
      RESCALE_NORMAL.disable();
   }

   public static void viewport(int var0, int var1, int var2, int var3) {
      GlStateManager.Viewport.INSTANCE.x = var0;
      GlStateManager.Viewport.INSTANCE.y = var1;
      GlStateManager.Viewport.INSTANCE.width = var2;
      GlStateManager.Viewport.INSTANCE.height = var3;
      GL11.glViewport(var0, var1, var2, var3);
   }

   public static void colorMask(boolean var0, boolean var1, boolean var2, boolean var3) {
      if (var0 != COLOR_MASK.red || var1 != COLOR_MASK.green || var2 != COLOR_MASK.blue || var3 != COLOR_MASK.alpha) {
         COLOR_MASK.red = var0;
         COLOR_MASK.green = var1;
         COLOR_MASK.blue = var2;
         COLOR_MASK.alpha = var3;
         GL11.glColorMask(var0, var1, var2, var3);
      }

   }

   public static void stencilFunc(int var0, int var1, int var2) {
      if (var0 != STENCIL.func.func || var0 != STENCIL.func.ref || var0 != STENCIL.func.mask) {
         STENCIL.func.func = var0;
         STENCIL.func.ref = var1;
         STENCIL.func.mask = var2;
         GL11.glStencilFunc(var0, var1, var2);
      }

   }

   public static void stencilMask(int var0) {
      if (var0 != STENCIL.mask) {
         STENCIL.mask = var0;
         GL11.glStencilMask(var0);
      }

   }

   public static void stencilOp(int var0, int var1, int var2) {
      if (var0 != STENCIL.fail || var1 != STENCIL.zfail || var2 != STENCIL.zpass) {
         STENCIL.fail = var0;
         STENCIL.zfail = var1;
         STENCIL.zpass = var2;
         GL11.glStencilOp(var0, var1, var2);
      }

   }

   public static void clearDepth(double var0) {
      if (var0 != CLEAR.depth) {
         CLEAR.depth = var0;
         GL11.glClearDepth(var0);
      }

   }

   public static void clearColor(float var0, float var1, float var2, float var3) {
      if (var0 != CLEAR.color.r || var1 != CLEAR.color.g || var2 != CLEAR.color.b || var3 != CLEAR.color.a) {
         CLEAR.color.r = var0;
         CLEAR.color.g = var1;
         CLEAR.color.b = var2;
         CLEAR.color.a = var3;
         GL11.glClearColor(var0, var1, var2, var3);
      }

   }

   public static void clearStencil(int var0) {
      if (var0 != CLEAR.stencil) {
         CLEAR.stencil = var0;
         GL11.glClearStencil(var0);
      }

   }

   public static void clear(int var0, boolean var1) {
      GL11.glClear(var0);
      if (var1) {
         getError();
      }

   }

   public static void matrixMode(int var0) {
      GL11.glMatrixMode(var0);
   }

   public static void loadIdentity() {
      GL11.glLoadIdentity();
   }

   public static void pushMatrix() {
      GL11.glPushMatrix();
   }

   public static void popMatrix() {
      GL11.glPopMatrix();
   }

   public static void getMatrix(int var0, FloatBuffer var1) {
      GL11.glGetFloatv(var0, var1);
   }

   public static Matrix4f getMatrix4f(int var0) {
      GL11.glGetFloatv(var0, MATRIX_BUFFER);
      MATRIX_BUFFER.rewind();
      Matrix4f var1 = new Matrix4f();
      var1.load(MATRIX_BUFFER);
      MATRIX_BUFFER.rewind();
      return var1;
   }

   public static void ortho(double var0, double var2, double var4, double var6, double var8, double var10) {
      GL11.glOrtho(var0, var2, var4, var6, var8, var10);
   }

   public static void rotatef(float var0, float var1, float var2, float var3) {
      GL11.glRotatef(var0, var1, var2, var3);
   }

   public static void rotated(double var0, double var2, double var4, double var6) {
      GL11.glRotated(var0, var2, var4, var6);
   }

   public static void scalef(float var0, float var1, float var2) {
      GL11.glScalef(var0, var1, var2);
   }

   public static void scaled(double var0, double var2, double var4) {
      GL11.glScaled(var0, var2, var4);
   }

   public static void translatef(float var0, float var1, float var2) {
      GL11.glTranslatef(var0, var1, var2);
   }

   public static void translated(double var0, double var2, double var4) {
      GL11.glTranslated(var0, var2, var4);
   }

   public static void multMatrix(FloatBuffer var0) {
      GL11.glMultMatrixf(var0);
   }

   public static void multMatrix(Matrix4f var0) {
      var0.store(MATRIX_BUFFER);
      MATRIX_BUFFER.rewind();
      GL11.glMultMatrixf(MATRIX_BUFFER);
   }

   public static void color4f(float var0, float var1, float var2, float var3) {
      if (var0 != COLOR.r || var1 != COLOR.g || var2 != COLOR.b || var3 != COLOR.a) {
         COLOR.r = var0;
         COLOR.g = var1;
         COLOR.b = var2;
         COLOR.a = var3;
         GL11.glColor4f(var0, var1, var2, var3);
      }

   }

   public static void color3f(float var0, float var1, float var2) {
      color4f(var0, var1, var2, 1.0F);
   }

   public static void texCoord2f(float var0, float var1) {
      GL11.glTexCoord2f(var0, var1);
   }

   public static void vertex3f(float var0, float var1, float var2) {
      GL11.glVertex3f(var0, var1, var2);
   }

   public static void clearCurrentColor() {
      COLOR.r = -1.0F;
      COLOR.g = -1.0F;
      COLOR.b = -1.0F;
      COLOR.a = -1.0F;
   }

   public static void normalPointer(int var0, int var1, int var2) {
      GL11.glNormalPointer(var0, var1, (long)var2);
   }

   public static void normalPointer(int var0, int var1, ByteBuffer var2) {
      GL11.glNormalPointer(var0, var1, var2);
   }

   public static void texCoordPointer(int var0, int var1, int var2, int var3) {
      GL11.glTexCoordPointer(var0, var1, var2, (long)var3);
   }

   public static void texCoordPointer(int var0, int var1, int var2, ByteBuffer var3) {
      GL11.glTexCoordPointer(var0, var1, var2, var3);
   }

   public static void vertexPointer(int var0, int var1, int var2, int var3) {
      GL11.glVertexPointer(var0, var1, var2, (long)var3);
   }

   public static void vertexPointer(int var0, int var1, int var2, ByteBuffer var3) {
      GL11.glVertexPointer(var0, var1, var2, var3);
   }

   public static void colorPointer(int var0, int var1, int var2, int var3) {
      GL11.glColorPointer(var0, var1, var2, (long)var3);
   }

   public static void colorPointer(int var0, int var1, int var2, ByteBuffer var3) {
      GL11.glColorPointer(var0, var1, var2, var3);
   }

   public static void disableClientState(int var0) {
      GL11.glDisableClientState(var0);
   }

   public static void enableClientState(int var0) {
      GL11.glEnableClientState(var0);
   }

   public static void begin(int var0) {
      GL11.glBegin(var0);
   }

   public static void end() {
      GL11.glEnd();
   }

   public static void drawArrays(int var0, int var1, int var2) {
      GL11.glDrawArrays(var0, var1, var2);
   }

   public static void lineWidth(float var0) {
      GL11.glLineWidth(var0);
   }

   public static void callList(int var0) {
      GL11.glCallList(var0);
   }

   public static void deleteLists(int var0, int var1) {
      GL11.glDeleteLists(var0, var1);
   }

   public static void newList(int var0, int var1) {
      GL11.glNewList(var0, var1);
   }

   public static void endList() {
      GL11.glEndList();
   }

   public static int genLists(int var0) {
      return GL11.glGenLists(var0);
   }

   public static void pixelStore(int var0, int var1) {
      GL11.glPixelStorei(var0, var1);
   }

   public static void pixelTransfer(int var0, float var1) {
      GL11.glPixelTransferf(var0, var1);
   }

   public static void readPixels(int var0, int var1, int var2, int var3, int var4, int var5, ByteBuffer var6) {
      GL11.glReadPixels(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void readPixels(int var0, int var1, int var2, int var3, int var4, int var5, long var6) {
      GL11.glReadPixels(var0, var1, var2, var3, var4, var5, var6);
   }

   public static int getError() {
      return GL11.glGetError();
   }

   public static String getString(int var0) {
      return GL11.glGetString(var0);
   }

   public static void getInteger(int var0, IntBuffer var1) {
      GL11.glGetIntegerv(var0, var1);
   }

   public static int getInteger(int var0) {
      return GL11.glGetInteger(var0);
   }

   public static void setProfile(GlStateManager.Profile var0) {
      var0.apply();
   }

   public static void unsetProfile(GlStateManager.Profile var0) {
      var0.clean();
   }

   public static enum Profile {
      DEFAULT {
         public void apply() {
            GlStateManager.disableAlphaTest();
            GlStateManager.alphaFunc(519, 0.0F);
            GlStateManager.disableLighting();
            GlStateManager.lightModel(2899, Lighting.getBuffer(0.2F, 0.2F, 0.2F, 1.0F));

            for(int var1 = 0; var1 < 8; ++var1) {
               GlStateManager.disableLight(var1);
               GlStateManager.light(16384 + var1, 4608, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
               GlStateManager.light(16384 + var1, 4611, Lighting.getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
               if (var1 == 0) {
                  GlStateManager.light(16384 + var1, 4609, Lighting.getBuffer(1.0F, 1.0F, 1.0F, 1.0F));
                  GlStateManager.light(16384 + var1, 4610, Lighting.getBuffer(1.0F, 1.0F, 1.0F, 1.0F));
               } else {
                  GlStateManager.light(16384 + var1, 4609, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                  GlStateManager.light(16384 + var1, 4610, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
               }
            }

            GlStateManager.disableColorMaterial();
            GlStateManager.colorMaterial(1032, 5634);
            GlStateManager.disableDepthTest();
            GlStateManager.depthFunc(513);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendEquation(32774);
            GlStateManager.disableFog();
            GlStateManager.fogi(2917, 2048);
            GlStateManager.fogDensity(1.0F);
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(1.0F);
            GlStateManager.fog(2918, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            if (GL.getCapabilities().GL_NV_fog_distance) {
               GlStateManager.fogi(2917, 34140);
            }

            GlStateManager.polygonOffset(0.0F, 0.0F);
            GlStateManager.disableColorLogicOp();
            GlStateManager.logicOp(5379);
            GlStateManager.disableTexGen(GlStateManager.TexGen.S);
            GlStateManager.texGenMode(GlStateManager.TexGen.S, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9474, Lighting.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9217, Lighting.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.T);
            GlStateManager.texGenMode(GlStateManager.TexGen.T, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9474, Lighting.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9217, Lighting.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.R);
            GlStateManager.texGenMode(GlStateManager.TexGen.R, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9474, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9217, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.Q);
            GlStateManager.texGenMode(GlStateManager.TexGen.Q, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9474, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9217, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.activeTexture(0);
            GlStateManager.texParameter(3553, 10240, 9729);
            GlStateManager.texParameter(3553, 10241, 9986);
            GlStateManager.texParameter(3553, 10242, 10497);
            GlStateManager.texParameter(3553, 10243, 10497);
            GlStateManager.texParameter(3553, 33085, 1000);
            GlStateManager.texParameter(3553, 33083, 1000);
            GlStateManager.texParameter(3553, 33082, -1000);
            GlStateManager.texParameter(3553, 34049, 0.0F);
            GlStateManager.texEnv(8960, 8704, 8448);
            GlStateManager.texEnv(8960, 8705, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texEnv(8960, 34161, 8448);
            GlStateManager.texEnv(8960, 34162, 8448);
            GlStateManager.texEnv(8960, 34176, 5890);
            GlStateManager.texEnv(8960, 34177, 34168);
            GlStateManager.texEnv(8960, 34178, 34166);
            GlStateManager.texEnv(8960, 34184, 5890);
            GlStateManager.texEnv(8960, 34185, 34168);
            GlStateManager.texEnv(8960, 34186, 34166);
            GlStateManager.texEnv(8960, 34192, 768);
            GlStateManager.texEnv(8960, 34193, 768);
            GlStateManager.texEnv(8960, 34194, 770);
            GlStateManager.texEnv(8960, 34200, 770);
            GlStateManager.texEnv(8960, 34201, 770);
            GlStateManager.texEnv(8960, 34202, 770);
            GlStateManager.texEnv(8960, 34163, 1.0F);
            GlStateManager.texEnv(8960, 3356, 1.0F);
            GlStateManager.disableNormalize();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableRescaleNormal();
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.clearDepth(1.0D);
            GlStateManager.lineWidth(1.0F);
            GlStateManager.normal3f(0.0F, 0.0F, 1.0F);
            GlStateManager.polygonMode(1028, 6914);
            GlStateManager.polygonMode(1029, 6914);
         }

         public void clean() {
         }
      },
      PLAYER_SKIN {
         public void apply() {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         }

         public void clean() {
            GlStateManager.disableBlend();
         }
      },
      TRANSPARENT_MODEL {
         public void apply() {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         public void clean() {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.depthMask(true);
         }
      };

      private Profile() {
      }

      public abstract void apply();

      public abstract void clean();

      // $FF: synthetic method
      Profile(Object var3) {
         this();
      }
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

   public static enum TexGen {
      S,
      T,
      R,
      Q;

      private TexGen() {
      }
   }

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

   static class ClearState {
      public double depth;
      public final GlStateManager.Color color;
      public int stencil;

      private ClearState() {
         super();
         this.depth = 1.0D;
         this.color = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
      }

      // $FF: synthetic method
      ClearState(Object var1) {
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

   public static enum CullFace {
      FRONT(1028),
      BACK(1029),
      FRONT_AND_BACK(1032);

      public final int value;

      private CullFace(int var3) {
         this.value = var3;
      }
   }

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
