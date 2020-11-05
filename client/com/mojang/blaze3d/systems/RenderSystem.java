package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;

public class RenderSystem {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ConcurrentLinkedQueue<RenderCall> recordingQueue = Queues.newConcurrentLinkedQueue();
   private static final Tesselator RENDER_THREAD_TESSELATOR = new Tesselator();
   public static final float DEFAULTALPHACUTOFF = 0.1F;
   private static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
   private static boolean isReplayingQueue;
   private static Thread gameThread;
   private static Thread renderThread;
   private static int MAX_SUPPORTED_TEXTURE_SIZE = -1;
   private static boolean isInInit;
   private static double lastDrawTime = 4.9E-324D;
   private static final RenderSystem.AutoStorageIndexBuffer sharedSequential = new RenderSystem.AutoStorageIndexBuffer(1, 1, IntConsumer::accept);
   private static final RenderSystem.AutoStorageIndexBuffer sharedSequentialQuad = new RenderSystem.AutoStorageIndexBuffer(4, 6, (var0, var1) -> {
      var0.accept(var1 + 0);
      var0.accept(var1 + 1);
      var0.accept(var1 + 2);
      var0.accept(var1 + 2);
      var0.accept(var1 + 3);
      var0.accept(var1 + 0);
   });

   public RenderSystem() {
      super();
   }

   public static void initRenderThread() {
      if (renderThread == null && gameThread != Thread.currentThread()) {
         renderThread = Thread.currentThread();
      } else {
         throw new IllegalStateException("Could not initialize render thread");
      }
   }

   public static boolean isOnRenderThread() {
      return Thread.currentThread() == renderThread;
   }

   public static boolean isOnRenderThreadOrInit() {
      return isInInit || isOnRenderThread();
   }

   public static void initGameThread(boolean var0) {
      boolean var1 = renderThread == Thread.currentThread();
      if (gameThread == null && renderThread != null && var1 != var0) {
         gameThread = Thread.currentThread();
      } else {
         throw new IllegalStateException("Could not initialize tick thread");
      }
   }

   public static boolean isOnGameThread() {
      return true;
   }

   public static boolean isOnGameThreadOrInit() {
      return isInInit || isOnGameThread();
   }

   public static void assertThread(Supplier<Boolean> var0) {
      if (!(Boolean)var0.get()) {
         throw new IllegalStateException("Rendersystem called from wrong thread");
      }
   }

   public static boolean isInInitPhase() {
      return true;
   }

   public static void recordRenderCall(RenderCall var0) {
      recordingQueue.add(var0);
   }

   public static void flipFrame(long var0) {
      GLFW.glfwPollEvents();
      replayQueue();
      Tesselator.getInstance().getBuilder().clear();
      GLFW.glfwSwapBuffers(var0);
      GLFW.glfwPollEvents();
   }

   public static void replayQueue() {
      isReplayingQueue = true;

      while(!recordingQueue.isEmpty()) {
         RenderCall var0 = (RenderCall)recordingQueue.poll();
         var0.execute();
      }

      isReplayingQueue = false;
   }

   public static void limitDisplayFPS(int var0) {
      double var1 = lastDrawTime + 1.0D / (double)var0;

      double var3;
      for(var3 = GLFW.glfwGetTime(); var3 < var1; var3 = GLFW.glfwGetTime()) {
         GLFW.glfwWaitEventsTimeout(var1 - var3);
      }

      lastDrawTime = var3;
   }

   @Deprecated
   public static void pushLightingAttributes() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._pushLightingAttributes();
   }

   @Deprecated
   public static void pushTextureAttributes() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._pushTextureAttributes();
   }

   @Deprecated
   public static void popAttributes() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._popAttributes();
   }

   @Deprecated
   public static void disableAlphaTest() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableAlphaTest();
   }

   @Deprecated
   public static void enableAlphaTest() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableAlphaTest();
   }

   @Deprecated
   public static void alphaFunc(int var0, float var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._alphaFunc(var0, var1);
   }

   @Deprecated
   public static void enableLighting() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableLighting();
   }

   @Deprecated
   public static void disableLighting() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableLighting();
   }

   @Deprecated
   public static void enableColorMaterial() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableColorMaterial();
   }

   @Deprecated
   public static void disableColorMaterial() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableColorMaterial();
   }

   @Deprecated
   public static void colorMaterial(int var0, int var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._colorMaterial(var0, var1);
   }

   @Deprecated
   public static void normal3f(float var0, float var1, float var2) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._normal3f(var0, var1, var2);
   }

   public static void disableDepthTest() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableDepthTest();
   }

   public static void enableDepthTest() {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._enableDepthTest();
   }

   public static void enableScissor(int var0, int var1, int var2, int var3) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._enableScissorTest();
      GlStateManager._scissorBox(var0, var1, var2, var3);
   }

   public static void disableScissor() {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._disableScissorTest();
   }

   public static void depthFunc(int var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._depthFunc(var0);
   }

   public static void depthMask(boolean var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._depthMask(var0);
   }

   public static void enableBlend() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableBlend();
   }

   public static void disableBlend() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableBlend();
   }

   public static void blendFunc(GlStateManager.SourceFactor var0, GlStateManager.DestFactor var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._blendFunc(var0.value, var1.value);
   }

   public static void blendFunc(int var0, int var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._blendFunc(var0, var1);
   }

   public static void blendFuncSeparate(GlStateManager.SourceFactor var0, GlStateManager.DestFactor var1, GlStateManager.SourceFactor var2, GlStateManager.DestFactor var3) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._blendFuncSeparate(var0.value, var1.value, var2.value, var3.value);
   }

   public static void blendFuncSeparate(int var0, int var1, int var2, int var3) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._blendFuncSeparate(var0, var1, var2, var3);
   }

   public static void blendEquation(int var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._blendEquation(var0);
   }

   public static void blendColor(float var0, float var1, float var2, float var3) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._blendColor(var0, var1, var2, var3);
   }

   @Deprecated
   public static void enableFog() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableFog();
   }

   @Deprecated
   public static void disableFog() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableFog();
   }

   @Deprecated
   public static void fogMode(GlStateManager.FogMode var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fogMode(var0.value);
   }

   @Deprecated
   public static void fogMode(int var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fogMode(var0);
   }

   @Deprecated
   public static void fogDensity(float var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fogDensity(var0);
   }

   @Deprecated
   public static void fogStart(float var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fogStart(var0);
   }

   @Deprecated
   public static void fogEnd(float var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fogEnd(var0);
   }

   @Deprecated
   public static void fog(int var0, float var1, float var2, float var3, float var4) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fog(var0, new float[]{var1, var2, var3, var4});
   }

   @Deprecated
   public static void fogi(int var0, int var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._fogi(var0, var1);
   }

   public static void enableCull() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableCull();
   }

   public static void disableCull() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableCull();
   }

   public static void polygonMode(int var0, int var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._polygonMode(var0, var1);
   }

   public static void enablePolygonOffset() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enablePolygonOffset();
   }

   public static void disablePolygonOffset() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disablePolygonOffset();
   }

   public static void enableLineOffset() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableLineOffset();
   }

   public static void disableLineOffset() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableLineOffset();
   }

   public static void polygonOffset(float var0, float var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._polygonOffset(var0, var1);
   }

   public static void enableColorLogicOp() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableColorLogicOp();
   }

   public static void disableColorLogicOp() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableColorLogicOp();
   }

   public static void logicOp(GlStateManager.LogicOp var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._logicOp(var0.value);
   }

   public static void activeTexture(int var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._activeTexture(var0);
   }

   public static void enableTexture() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableTexture();
   }

   public static void disableTexture() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableTexture();
   }

   public static void texParameter(int var0, int var1, int var2) {
      GlStateManager._texParameter(var0, var1, var2);
   }

   public static void deleteTexture(int var0) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._deleteTexture(var0);
   }

   public static void bindTexture(int var0) {
      GlStateManager._bindTexture(var0);
   }

   @Deprecated
   public static void shadeModel(int var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._shadeModel(var0);
   }

   @Deprecated
   public static void enableRescaleNormal() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._enableRescaleNormal();
   }

   @Deprecated
   public static void disableRescaleNormal() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._disableRescaleNormal();
   }

   public static void viewport(int var0, int var1, int var2, int var3) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._viewport(var0, var1, var2, var3);
   }

   public static void colorMask(boolean var0, boolean var1, boolean var2, boolean var3) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._colorMask(var0, var1, var2, var3);
   }

   public static void stencilFunc(int var0, int var1, int var2) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._stencilFunc(var0, var1, var2);
   }

   public static void stencilMask(int var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._stencilMask(var0);
   }

   public static void stencilOp(int var0, int var1, int var2) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._stencilOp(var0, var1, var2);
   }

   public static void clearDepth(double var0) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._clearDepth(var0);
   }

   public static void clearColor(float var0, float var1, float var2, float var3) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._clearColor(var0, var1, var2, var3);
   }

   public static void clearStencil(int var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._clearStencil(var0);
   }

   public static void clear(int var0, boolean var1) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._clear(var0, var1);
   }

   @Deprecated
   public static void matrixMode(int var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._matrixMode(var0);
   }

   @Deprecated
   public static void loadIdentity() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._loadIdentity();
   }

   @Deprecated
   public static void pushMatrix() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._pushMatrix();
   }

   @Deprecated
   public static void popMatrix() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._popMatrix();
   }

   @Deprecated
   public static void ortho(double var0, double var2, double var4, double var6, double var8, double var10) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._ortho(var0, var2, var4, var6, var8, var10);
   }

   @Deprecated
   public static void rotatef(float var0, float var1, float var2, float var3) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._rotatef(var0, var1, var2, var3);
   }

   @Deprecated
   public static void scalef(float var0, float var1, float var2) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._scalef(var0, var1, var2);
   }

   @Deprecated
   public static void scaled(double var0, double var2, double var4) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._scaled(var0, var2, var4);
   }

   @Deprecated
   public static void translatef(float var0, float var1, float var2) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._translatef(var0, var1, var2);
   }

   @Deprecated
   public static void translated(double var0, double var2, double var4) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._translated(var0, var2, var4);
   }

   @Deprecated
   public static void multMatrix(Matrix4f var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._multMatrix(var0);
   }

   @Deprecated
   public static void color4f(float var0, float var1, float var2, float var3) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._color4f(var0, var1, var2, var3);
   }

   @Deprecated
   public static void color3f(float var0, float var1, float var2) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._color4f(var0, var1, var2, 1.0F);
   }

   @Deprecated
   public static void clearCurrentColor() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._clearCurrentColor();
   }

   public static void drawElements(int var0, int var1, int var2) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._drawElements(var0, var1, var2, 0L);
   }

   public static void lineWidth(float var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._lineWidth(var0);
   }

   public static void pixelStore(int var0, int var1) {
      assertThread(RenderSystem::isOnGameThreadOrInit);
      GlStateManager._pixelStore(var0, var1);
   }

   public static void pixelTransfer(int var0, float var1) {
      GlStateManager._pixelTransfer(var0, var1);
   }

   public static void readPixels(int var0, int var1, int var2, int var3, int var4, int var5, ByteBuffer var6) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._readPixels(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void getString(int var0, Consumer<String> var1) {
      assertThread(RenderSystem::isOnGameThread);
      var1.accept(GlStateManager._getString(var0));
   }

   public static String getBackendDescription() {
      assertThread(RenderSystem::isInInitPhase);
      return String.format("LWJGL version %s", GLX._getLWJGLVersion());
   }

   public static String getApiDescription() {
      assertThread(RenderSystem::isInInitPhase);
      return GLX.getOpenGLVersionString();
   }

   public static LongSupplier initBackendSystem() {
      assertThread(RenderSystem::isInInitPhase);
      return GLX._initGlfw();
   }

   public static void initRenderer(int var0, boolean var1) {
      assertThread(RenderSystem::isInInitPhase);
      GLX._init(var0, var1);
   }

   public static void setErrorCallback(GLFWErrorCallbackI var0) {
      assertThread(RenderSystem::isInInitPhase);
      GLX._setGlfwErrorCallback(var0);
   }

   public static void renderCrosshair(int var0) {
      assertThread(RenderSystem::isOnGameThread);
      GLX._renderCrosshair(var0, true, true, true);
   }

   public static void setupNvFogDistance() {
      assertThread(RenderSystem::isOnGameThread);
      GLX._setupNvFogDistance();
   }

   @Deprecated
   public static void glMultiTexCoord2f(int var0, float var1, float var2) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glMultiTexCoord2f(var0, var1, var2);
   }

   public static String getCapsString() {
      assertThread(RenderSystem::isOnGameThread);
      return GLX._getCapsString();
   }

   public static void setupDefaultState(int var0, int var1, int var2, int var3) {
      assertThread(RenderSystem::isInInitPhase);
      GlStateManager._enableTexture();
      GlStateManager._shadeModel(7425);
      GlStateManager._clearDepth(1.0D);
      GlStateManager._enableDepthTest();
      GlStateManager._depthFunc(515);
      GlStateManager._enableAlphaTest();
      GlStateManager._alphaFunc(516, 0.1F);
      GlStateManager._matrixMode(5889);
      GlStateManager._loadIdentity();
      GlStateManager._matrixMode(5888);
      GlStateManager._viewport(var0, var1, var2, var3);
   }

   public static int maxSupportedTextureSize() {
      assertThread(RenderSystem::isInInitPhase);
      if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
         int var0 = GlStateManager._getInteger(3379);

         for(int var1 = Math.max(32768, var0); var1 >= 1024; var1 >>= 1) {
            GlStateManager._texImage2D(32868, 0, 6408, var1, var1, 0, 6408, 5121, (IntBuffer)null);
            int var2 = GlStateManager._getTexLevelParameter(32868, 0, 4096);
            if (var2 != 0) {
               MAX_SUPPORTED_TEXTURE_SIZE = var1;
               return var1;
            }
         }

         MAX_SUPPORTED_TEXTURE_SIZE = Math.max(var0, 1024);
         LOGGER.info("Failed to determine maximum texture size by probing, trying GL_MAX_TEXTURE_SIZE = {}", MAX_SUPPORTED_TEXTURE_SIZE);
      }

      return MAX_SUPPORTED_TEXTURE_SIZE;
   }

   public static void glBindBuffer(int var0, Supplier<Integer> var1) {
      GlStateManager._glBindBuffer(var0, (Integer)var1.get());
   }

   public static void glBufferData(int var0, ByteBuffer var1, int var2) {
      assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager._glBufferData(var0, var1, var2);
   }

   public static void glDeleteBuffers(int var0) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glDeleteBuffers(var0);
   }

   public static void glUniform1i(int var0, int var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform1i(var0, var1);
   }

   public static void glUniform1(int var0, IntBuffer var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform1(var0, var1);
   }

   public static void glUniform2(int var0, IntBuffer var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform2(var0, var1);
   }

   public static void glUniform3(int var0, IntBuffer var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform3(var0, var1);
   }

   public static void glUniform4(int var0, IntBuffer var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform4(var0, var1);
   }

   public static void glUniform1(int var0, FloatBuffer var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform1(var0, var1);
   }

   public static void glUniform2(int var0, FloatBuffer var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform2(var0, var1);
   }

   public static void glUniform3(int var0, FloatBuffer var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform3(var0, var1);
   }

   public static void glUniform4(int var0, FloatBuffer var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniform4(var0, var1);
   }

   public static void glUniformMatrix2(int var0, boolean var1, FloatBuffer var2) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniformMatrix2(var0, var1, var2);
   }

   public static void glUniformMatrix3(int var0, boolean var1, FloatBuffer var2) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniformMatrix3(var0, var1, var2);
   }

   public static void glUniformMatrix4(int var0, boolean var1, FloatBuffer var2) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager._glUniformMatrix4(var0, var1, var2);
   }

   public static void setupOutline() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupOutline();
   }

   public static void teardownOutline() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.teardownOutline();
   }

   public static void setupOverlayColor(IntSupplier var0, int var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupOverlayColor(var0.getAsInt(), var1);
   }

   public static void teardownOverlayColor() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.teardownOverlayColor();
   }

   public static void setupLevelDiffuseLighting(Vector3f var0, Vector3f var1, Matrix4f var2) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupLevelDiffuseLighting(var0, var1, var2);
   }

   public static void setupGuiFlatDiffuseLighting(Vector3f var0, Vector3f var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupGuiFlatDiffuseLighting(var0, var1);
   }

   public static void setupGui3DDiffuseLighting(Vector3f var0, Vector3f var1) {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupGui3DDiffuseLighting(var0, var1);
   }

   public static void mulTextureByProjModelView() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.mulTextureByProjModelView();
   }

   public static void setupEndPortalTexGen() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.setupEndPortalTexGen();
   }

   public static void clearTexGen() {
      assertThread(RenderSystem::isOnGameThread);
      GlStateManager.clearTexGen();
   }

   public static void beginInitialization() {
      isInInit = true;
   }

   public static void finishInitialization() {
      isInInit = false;
      if (!recordingQueue.isEmpty()) {
         replayQueue();
      }

      if (!recordingQueue.isEmpty()) {
         throw new IllegalStateException("Recorded to render queue during initialization");
      }
   }

   public static void glGenBuffers(Consumer<Integer> var0) {
      if (!isOnRenderThread()) {
         recordRenderCall(() -> {
            var0.accept(GlStateManager._glGenBuffers());
         });
      } else {
         var0.accept(GlStateManager._glGenBuffers());
      }

   }

   public static Tesselator renderThreadTesselator() {
      assertThread(RenderSystem::isOnRenderThread);
      return RENDER_THREAD_TESSELATOR;
   }

   public static void defaultBlendFunc() {
      blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }

   public static void defaultAlphaFunc() {
      alphaFunc(516, 0.1F);
   }

   @Deprecated
   public static void runAsFancy(Runnable var0) {
      boolean var1 = Minecraft.useShaderTransparency();
      if (!var1) {
         var0.run();
      } else {
         Options var2 = Minecraft.getInstance().options;
         GraphicsStatus var3 = var2.graphicsMode;
         var2.graphicsMode = GraphicsStatus.FANCY;
         var0.run();
         var2.graphicsMode = var3;
      }
   }

   public static RenderSystem.AutoStorageIndexBuffer getSequentialBuffer(VertexFormat.Mode var0, int var1) {
      assertThread(RenderSystem::isOnRenderThread);
      RenderSystem.AutoStorageIndexBuffer var2 = var0 == VertexFormat.Mode.QUADS ? sharedSequentialQuad : sharedSequential;
      var2.ensureStorage(var1);
      return var2;
   }

   // $FF: synthetic method
   private static void lambda$setupGui3DDiffuseLighting$72(Vector3f var0, Vector3f var1) {
      GlStateManager.setupGui3DDiffuseLighting(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$setupGuiFlatDiffuseLighting$71(Vector3f var0, Vector3f var1) {
      GlStateManager.setupGuiFlatDiffuseLighting(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$setupLevelDiffuseLighting$70(Vector3f var0, Vector3f var1, Matrix4f var2) {
      GlStateManager.setupLevelDiffuseLighting(var0, var1, var2);
   }

   // $FF: synthetic method
   private static void lambda$setupOverlayColor$69(IntSupplier var0, int var1) {
      GlStateManager.setupOverlayColor(var0.getAsInt(), var1);
   }

   // $FF: synthetic method
   private static void lambda$glUniformMatrix4$68(int var0, boolean var1, FloatBuffer var2) {
      GlStateManager._glUniformMatrix4(var0, var1, var2);
   }

   // $FF: synthetic method
   private static void lambda$glUniformMatrix3$67(int var0, boolean var1, FloatBuffer var2) {
      GlStateManager._glUniformMatrix3(var0, var1, var2);
   }

   // $FF: synthetic method
   private static void lambda$glUniformMatrix2$66(int var0, boolean var1, FloatBuffer var2) {
      GlStateManager._glUniformMatrix2(var0, var1, var2);
   }

   // $FF: synthetic method
   private static void lambda$glUniform4$65(int var0, FloatBuffer var1) {
      GlStateManager._glUniform4(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$glUniform3$64(int var0, FloatBuffer var1) {
      GlStateManager._glUniform3(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$glUniform2$63(int var0, FloatBuffer var1) {
      GlStateManager._glUniform2(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$glUniform1$62(int var0, FloatBuffer var1) {
      GlStateManager._glUniform1(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$glUniform4$61(int var0, IntBuffer var1) {
      GlStateManager._glUniform4(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$glUniform3$60(int var0, IntBuffer var1) {
      GlStateManager._glUniform3(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$glUniform2$59(int var0, IntBuffer var1) {
      GlStateManager._glUniform2(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$glUniform1$58(int var0, IntBuffer var1) {
      GlStateManager._glUniform1(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$glUniform1i$57(int var0, int var1) {
      GlStateManager._glUniform1i(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$glDeleteBuffers$56(int var0) {
      GlStateManager._glDeleteBuffers(var0);
   }

   // $FF: synthetic method
   private static void lambda$glBindBuffer$55(int var0, Supplier var1) {
      GlStateManager._glBindBuffer(var0, (Integer)var1.get());
   }

   // $FF: synthetic method
   private static void lambda$glMultiTexCoord2f$54(int var0, float var1, float var2) {
      GlStateManager._glMultiTexCoord2f(var0, var1, var2);
   }

   // $FF: synthetic method
   private static void lambda$renderCrosshair$53(int var0) {
      GLX._renderCrosshair(var0, true, true, true);
   }

   // $FF: synthetic method
   private static void lambda$getString$52(int var0, Consumer var1) {
      String var2 = GlStateManager._getString(var0);
      var1.accept(var2);
   }

   // $FF: synthetic method
   private static void lambda$readPixels$51(int var0, int var1, int var2, int var3, int var4, int var5, ByteBuffer var6) {
      GlStateManager._readPixels(var0, var1, var2, var3, var4, var5, var6);
   }

   // $FF: synthetic method
   private static void lambda$pixelTransfer$50(int var0, float var1) {
      GlStateManager._pixelTransfer(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$pixelStore$49(int var0, int var1) {
      GlStateManager._pixelStore(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$lineWidth$48(float var0) {
      GlStateManager._lineWidth(var0);
   }

   // $FF: synthetic method
   private static void lambda$drawElements$47(int var0, int var1, int var2) {
      GlStateManager._drawElements(var0, var1, var2, 0L);
   }

   // $FF: synthetic method
   private static void lambda$color3f$46(float var0, float var1, float var2) {
      GlStateManager._color4f(var0, var1, var2, 1.0F);
   }

   // $FF: synthetic method
   private static void lambda$color4f$45(float var0, float var1, float var2, float var3) {
      GlStateManager._color4f(var0, var1, var2, var3);
   }

   // $FF: synthetic method
   private static void lambda$multMatrix$44(Matrix4f var0) {
      GlStateManager._multMatrix(var0);
   }

   // $FF: synthetic method
   private static void lambda$translated$43(double var0, double var2, double var4) {
      GlStateManager._translated(var0, var2, var4);
   }

   // $FF: synthetic method
   private static void lambda$translatef$42(float var0, float var1, float var2) {
      GlStateManager._translatef(var0, var1, var2);
   }

   // $FF: synthetic method
   private static void lambda$scaled$41(double var0, double var2, double var4) {
      GlStateManager._scaled(var0, var2, var4);
   }

   // $FF: synthetic method
   private static void lambda$scalef$40(float var0, float var1, float var2) {
      GlStateManager._scalef(var0, var1, var2);
   }

   // $FF: synthetic method
   private static void lambda$rotatef$39(float var0, float var1, float var2, float var3) {
      GlStateManager._rotatef(var0, var1, var2, var3);
   }

   // $FF: synthetic method
   private static void lambda$ortho$38(double var0, double var2, double var4, double var6, double var8, double var10) {
      GlStateManager._ortho(var0, var2, var4, var6, var8, var10);
   }

   // $FF: synthetic method
   private static void lambda$matrixMode$37(int var0) {
      GlStateManager._matrixMode(var0);
   }

   // $FF: synthetic method
   private static void lambda$clear$36(int var0, boolean var1) {
      GlStateManager._clear(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$clearStencil$35(int var0) {
      GlStateManager._clearStencil(var0);
   }

   // $FF: synthetic method
   private static void lambda$clearColor$34(float var0, float var1, float var2, float var3) {
      GlStateManager._clearColor(var0, var1, var2, var3);
   }

   // $FF: synthetic method
   private static void lambda$clearDepth$33(double var0) {
      GlStateManager._clearDepth(var0);
   }

   // $FF: synthetic method
   private static void lambda$stencilOp$32(int var0, int var1, int var2) {
      GlStateManager._stencilOp(var0, var1, var2);
   }

   // $FF: synthetic method
   private static void lambda$stencilMask$31(int var0) {
      GlStateManager._stencilMask(var0);
   }

   // $FF: synthetic method
   private static void lambda$stencilFunc$30(int var0, int var1, int var2) {
      GlStateManager._stencilFunc(var0, var1, var2);
   }

   // $FF: synthetic method
   private static void lambda$colorMask$29(boolean var0, boolean var1, boolean var2, boolean var3) {
      GlStateManager._colorMask(var0, var1, var2, var3);
   }

   // $FF: synthetic method
   private static void lambda$viewport$28(int var0, int var1, int var2, int var3) {
      GlStateManager._viewport(var0, var1, var2, var3);
   }

   // $FF: synthetic method
   private static void lambda$shadeModel$27(int var0) {
      GlStateManager._shadeModel(var0);
   }

   // $FF: synthetic method
   private static void lambda$bindTexture$26(int var0) {
      GlStateManager._bindTexture(var0);
   }

   // $FF: synthetic method
   private static void lambda$deleteTexture$25(int var0) {
      GlStateManager._deleteTexture(var0);
   }

   // $FF: synthetic method
   private static void lambda$texParameter$24(int var0, int var1, int var2) {
      GlStateManager._texParameter(var0, var1, var2);
   }

   // $FF: synthetic method
   private static void lambda$activeTexture$23(int var0) {
      GlStateManager._activeTexture(var0);
   }

   // $FF: synthetic method
   private static void lambda$logicOp$22(GlStateManager.LogicOp var0) {
      GlStateManager._logicOp(var0.value);
   }

   // $FF: synthetic method
   private static void lambda$polygonOffset$21(float var0, float var1) {
      GlStateManager._polygonOffset(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$polygonMode$20(int var0, int var1) {
      GlStateManager._polygonMode(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$fogi$19(int var0, int var1) {
      GlStateManager._fogi(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$fog$18(int var0, float var1, float var2, float var3, float var4) {
      GlStateManager._fog(var0, new float[]{var1, var2, var3, var4});
   }

   // $FF: synthetic method
   private static void lambda$fogEnd$17(float var0) {
      GlStateManager._fogEnd(var0);
   }

   // $FF: synthetic method
   private static void lambda$fogStart$16(float var0) {
      GlStateManager._fogStart(var0);
   }

   // $FF: synthetic method
   private static void lambda$fogDensity$15(float var0) {
      GlStateManager._fogDensity(var0);
   }

   // $FF: synthetic method
   private static void lambda$fogMode$14(int var0) {
      GlStateManager._fogMode(var0);
   }

   // $FF: synthetic method
   private static void lambda$fogMode$13(GlStateManager.FogMode var0) {
      GlStateManager._fogMode(var0.value);
   }

   // $FF: synthetic method
   private static void lambda$blendColor$12(float var0, float var1, float var2, float var3) {
      GlStateManager._blendColor(var0, var1, var2, var3);
   }

   // $FF: synthetic method
   private static void lambda$blendEquation$11(int var0) {
      GlStateManager._blendEquation(var0);
   }

   // $FF: synthetic method
   private static void lambda$blendFuncSeparate$10(int var0, int var1, int var2, int var3) {
      GlStateManager._blendFuncSeparate(var0, var1, var2, var3);
   }

   // $FF: synthetic method
   private static void lambda$blendFuncSeparate$9(GlStateManager.SourceFactor var0, GlStateManager.DestFactor var1, GlStateManager.SourceFactor var2, GlStateManager.DestFactor var3) {
      GlStateManager._blendFuncSeparate(var0.value, var1.value, var2.value, var3.value);
   }

   // $FF: synthetic method
   private static void lambda$blendFunc$8(int var0, int var1) {
      GlStateManager._blendFunc(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$blendFunc$7(GlStateManager.SourceFactor var0, GlStateManager.DestFactor var1) {
      GlStateManager._blendFunc(var0.value, var1.value);
   }

   // $FF: synthetic method
   private static void lambda$depthMask$6(boolean var0) {
      GlStateManager._depthMask(var0);
   }

   // $FF: synthetic method
   private static void lambda$depthFunc$5(int var0) {
      GlStateManager._depthFunc(var0);
   }

   // $FF: synthetic method
   private static void lambda$enableScissor$4(int var0, int var1, int var2, int var3) {
      GlStateManager._enableScissorTest();
      GlStateManager._scissorBox(var0, var1, var2, var3);
   }

   // $FF: synthetic method
   private static void lambda$normal3f$3(float var0, float var1, float var2) {
      GlStateManager._normal3f(var0, var1, var2);
   }

   // $FF: synthetic method
   private static void lambda$colorMaterial$2(int var0, int var1) {
      GlStateManager._colorMaterial(var0, var1);
   }

   // $FF: synthetic method
   private static void lambda$alphaFunc$1(int var0, float var1) {
      GlStateManager._alphaFunc(var0, var1);
   }

   public static final class AutoStorageIndexBuffer {
      private final int vertexStride;
      private final int indexStride;
      private final RenderSystem.AutoStorageIndexBuffer.IndexGenerator generator;
      private int name;
      private VertexFormat.IndexType type;
      private int indexCount;

      private AutoStorageIndexBuffer(int var1, int var2, RenderSystem.AutoStorageIndexBuffer.IndexGenerator var3) {
         super();
         this.type = VertexFormat.IndexType.BYTE;
         this.vertexStride = var1;
         this.indexStride = var2;
         this.generator = var3;
      }

      private void ensureStorage(int var1) {
         if (var1 > this.indexCount) {
            RenderSystem.LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", this.indexCount, var1);
            if (this.name == 0) {
               this.name = GlStateManager._glGenBuffers();
            }

            VertexFormat.IndexType var2 = VertexFormat.IndexType.least(var1);
            int var3 = Mth.roundToward(var1 * var2.bytes, 4);
            GlStateManager._glBindBuffer(34963, this.name);
            GlStateManager._glBufferData(34963, (long)var3, 35044);
            ByteBuffer var4 = GlStateManager._glMapBuffer(34963, 35001);
            if (var4 == null) {
               throw new RuntimeException("Failed to map GL buffer");
            } else {
               this.type = var2;
               it.unimi.dsi.fastutil.ints.IntConsumer var5 = this.intConsumer(var4);

               for(int var6 = 0; var6 < var1; var6 += this.indexStride) {
                  this.generator.accept(var5, var6 * this.vertexStride / this.indexStride);
               }

               GlStateManager._glUnmapBuffer(34963);
               GlStateManager._glBindBuffer(34963, 0);
               this.indexCount = var1;
            }
         }
      }

      private it.unimi.dsi.fastutil.ints.IntConsumer intConsumer(ByteBuffer var1) {
         switch(this.type) {
         case BYTE:
            return (var1x) -> {
               var1.put((byte)var1x);
            };
         case SHORT:
            return (var1x) -> {
               var1.putShort((short)var1x);
            };
         case INT:
         default:
            return var1::putInt;
         }
      }

      public int name() {
         return this.name;
      }

      public VertexFormat.IndexType type() {
         return this.type;
      }

      // $FF: synthetic method
      AutoStorageIndexBuffer(int var1, int var2, RenderSystem.AutoStorageIndexBuffer.IndexGenerator var3, Object var4) {
         this(var1, var2, var3);
      }

      interface IndexGenerator {
         void accept(it.unimi.dsi.fastutil.ints.IntConsumer var1, int var2);
      }
   }
}
