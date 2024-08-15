package com.mojang.blaze3d.systems;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeSource;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.slf4j.Logger;

@DontObfuscate
public class RenderSystem {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final ConcurrentLinkedQueue<RenderCall> recordingQueue = Queues.newConcurrentLinkedQueue();
   private static final Tesselator RENDER_THREAD_TESSELATOR = new Tesselator(1536);
   private static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
   @Nullable
   private static Thread renderThread;
   private static int MAX_SUPPORTED_TEXTURE_SIZE = -1;
   private static boolean isInInit;
   private static double lastDrawTime = 5.0E-324;
   private static final RenderSystem.AutoStorageIndexBuffer sharedSequential = new RenderSystem.AutoStorageIndexBuffer(1, 1, IntConsumer::accept);
   private static final RenderSystem.AutoStorageIndexBuffer sharedSequentialQuad = new RenderSystem.AutoStorageIndexBuffer(4, 6, (var0, var1) -> {
      var0.accept(var1 + 0);
      var0.accept(var1 + 1);
      var0.accept(var1 + 2);
      var0.accept(var1 + 2);
      var0.accept(var1 + 3);
      var0.accept(var1 + 0);
   });
   private static final RenderSystem.AutoStorageIndexBuffer sharedSequentialLines = new RenderSystem.AutoStorageIndexBuffer(4, 6, (var0, var1) -> {
      var0.accept(var1 + 0);
      var0.accept(var1 + 1);
      var0.accept(var1 + 2);
      var0.accept(var1 + 3);
      var0.accept(var1 + 2);
      var0.accept(var1 + 1);
   });
   private static Matrix4f projectionMatrix = new Matrix4f();
   private static Matrix4f savedProjectionMatrix = new Matrix4f();
   private static VertexSorting vertexSorting = VertexSorting.DISTANCE_TO_ORIGIN;
   private static VertexSorting savedVertexSorting = VertexSorting.DISTANCE_TO_ORIGIN;
   private static final Matrix4fStack modelViewStack = new Matrix4fStack(16);
   private static Matrix4f textureMatrix = new Matrix4f();
   private static final int[] shaderTextures = new int[12];
   private static final float[] shaderColor = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
   private static float shaderGlintAlpha = 1.0F;
   private static FogParameters shaderFog = FogParameters.NO_FOG;
   private static final Vector3f[] shaderLightDirections = new Vector3f[2];
   private static float shaderGameTime;
   private static float shaderLineWidth = 1.0F;
   private static String apiDescription = "Unknown";
   @Nullable
   private static ShaderInstance shader;
   private static final AtomicLong pollEventsWaitStart = new AtomicLong();
   private static final AtomicBoolean pollingEvents = new AtomicBoolean(false);

   public RenderSystem() {
      super();
   }

   public static void initRenderThread() {
      if (renderThread != null) {
         throw new IllegalStateException("Could not initialize render thread");
      } else {
         renderThread = Thread.currentThread();
      }
   }

   public static boolean isOnRenderThread() {
      return Thread.currentThread() == renderThread;
   }

   public static boolean isOnRenderThreadOrInit() {
      return isInInit || isOnRenderThread();
   }

   public static void assertOnRenderThreadOrInit() {
      if (!isInInit && !isOnRenderThread()) {
         throw constructThreadException();
      }
   }

   public static void assertOnRenderThread() {
      if (!isOnRenderThread()) {
         throw constructThreadException();
      }
   }

   private static IllegalStateException constructThreadException() {
      return new IllegalStateException("Rendersystem called from wrong thread");
   }

   public static void recordRenderCall(RenderCall var0) {
      recordingQueue.add(var0);
   }

   private static void pollEvents() {
      pollEventsWaitStart.set(Util.getMillis());
      pollingEvents.set(true);
      GLFW.glfwPollEvents();
      pollingEvents.set(false);
   }

   public static boolean isFrozenAtPollEvents() {
      return pollingEvents.get() && Util.getMillis() - pollEventsWaitStart.get() > 200L;
   }

   public static void flipFrame(long var0) {
      pollEvents();
      replayQueue();
      Tesselator.getInstance().clear();
      GLFW.glfwSwapBuffers(var0);
      pollEvents();
   }

   public static void replayQueue() {
      while (!recordingQueue.isEmpty()) {
         RenderCall var0 = recordingQueue.poll();
         var0.execute();
      }
   }

   public static void limitDisplayFPS(int var0) {
      double var1 = lastDrawTime + 1.0 / (double)var0;

      double var3;
      for (var3 = GLFW.glfwGetTime(); var3 < var1; var3 = GLFW.glfwGetTime()) {
         GLFW.glfwWaitEventsTimeout(var1 - var3);
      }

      lastDrawTime = var3;
   }

   public static void disableDepthTest() {
      assertOnRenderThread();
      GlStateManager._disableDepthTest();
   }

   public static void enableDepthTest() {
      GlStateManager._enableDepthTest();
   }

   public static void enableScissor(int var0, int var1, int var2, int var3) {
      GlStateManager._enableScissorTest();
      GlStateManager._scissorBox(var0, var1, var2, var3);
   }

   public static void disableScissor() {
      GlStateManager._disableScissorTest();
   }

   public static void depthFunc(int var0) {
      assertOnRenderThread();
      GlStateManager._depthFunc(var0);
   }

   public static void depthMask(boolean var0) {
      assertOnRenderThread();
      GlStateManager._depthMask(var0);
   }

   public static void enableBlend() {
      assertOnRenderThread();
      GlStateManager._enableBlend();
   }

   public static void disableBlend() {
      assertOnRenderThread();
      GlStateManager._disableBlend();
   }

   public static void blendFunc(GlStateManager.SourceFactor var0, GlStateManager.DestFactor var1) {
      assertOnRenderThread();
      GlStateManager._blendFunc(var0.value, var1.value);
   }

   public static void blendFunc(int var0, int var1) {
      assertOnRenderThread();
      GlStateManager._blendFunc(var0, var1);
   }

   public static void blendFuncSeparate(
      GlStateManager.SourceFactor var0, GlStateManager.DestFactor var1, GlStateManager.SourceFactor var2, GlStateManager.DestFactor var3
   ) {
      assertOnRenderThread();
      GlStateManager._blendFuncSeparate(var0.value, var1.value, var2.value, var3.value);
   }

   public static void blendFuncSeparate(int var0, int var1, int var2, int var3) {
      assertOnRenderThread();
      GlStateManager._blendFuncSeparate(var0, var1, var2, var3);
   }

   public static void blendEquation(int var0) {
      assertOnRenderThread();
      GlStateManager._blendEquation(var0);
   }

   public static void enableCull() {
      assertOnRenderThread();
      GlStateManager._enableCull();
   }

   public static void disableCull() {
      assertOnRenderThread();
      GlStateManager._disableCull();
   }

   public static void polygonMode(int var0, int var1) {
      assertOnRenderThread();
      GlStateManager._polygonMode(var0, var1);
   }

   public static void enablePolygonOffset() {
      assertOnRenderThread();
      GlStateManager._enablePolygonOffset();
   }

   public static void disablePolygonOffset() {
      assertOnRenderThread();
      GlStateManager._disablePolygonOffset();
   }

   public static void polygonOffset(float var0, float var1) {
      assertOnRenderThread();
      GlStateManager._polygonOffset(var0, var1);
   }

   public static void enableColorLogicOp() {
      assertOnRenderThread();
      GlStateManager._enableColorLogicOp();
   }

   public static void disableColorLogicOp() {
      assertOnRenderThread();
      GlStateManager._disableColorLogicOp();
   }

   public static void logicOp(GlStateManager.LogicOp var0) {
      assertOnRenderThread();
      GlStateManager._logicOp(var0.value);
   }

   public static void activeTexture(int var0) {
      assertOnRenderThread();
      GlStateManager._activeTexture(var0);
   }

   public static void texParameter(int var0, int var1, int var2) {
      GlStateManager._texParameter(var0, var1, var2);
   }

   public static void deleteTexture(int var0) {
      GlStateManager._deleteTexture(var0);
   }

   public static void bindTextureForSetup(int var0) {
      bindTexture(var0);
   }

   public static void bindTexture(int var0) {
      GlStateManager._bindTexture(var0);
   }

   public static void viewport(int var0, int var1, int var2, int var3) {
      GlStateManager._viewport(var0, var1, var2, var3);
   }

   public static void colorMask(boolean var0, boolean var1, boolean var2, boolean var3) {
      assertOnRenderThread();
      GlStateManager._colorMask(var0, var1, var2, var3);
   }

   public static void stencilFunc(int var0, int var1, int var2) {
      assertOnRenderThread();
      GlStateManager._stencilFunc(var0, var1, var2);
   }

   public static void stencilMask(int var0) {
      assertOnRenderThread();
      GlStateManager._stencilMask(var0);
   }

   public static void stencilOp(int var0, int var1, int var2) {
      assertOnRenderThread();
      GlStateManager._stencilOp(var0, var1, var2);
   }

   public static void clearDepth(double var0) {
      GlStateManager._clearDepth(var0);
   }

   public static void clearColor(float var0, float var1, float var2, float var3) {
      GlStateManager._clearColor(var0, var1, var2, var3);
   }

   public static void clearStencil(int var0) {
      assertOnRenderThread();
      GlStateManager._clearStencil(var0);
   }

   public static void clear(int var0) {
      GlStateManager._clear(var0);
   }

   public static void setShaderFog(FogParameters var0) {
      assertOnRenderThread();
      shaderFog = var0;
   }

   public static FogParameters getShaderFog() {
      assertOnRenderThread();
      return shaderFog;
   }

   public static void setShaderGlintAlpha(double var0) {
      setShaderGlintAlpha((float)var0);
   }

   public static void setShaderGlintAlpha(float var0) {
      assertOnRenderThread();
      shaderGlintAlpha = var0;
   }

   public static float getShaderGlintAlpha() {
      assertOnRenderThread();
      return shaderGlintAlpha;
   }

   public static void setShaderLights(Vector3f var0, Vector3f var1) {
      assertOnRenderThread();
      shaderLightDirections[0] = var0;
      shaderLightDirections[1] = var1;
   }

   public static void setupShaderLights(ShaderInstance var0) {
      assertOnRenderThread();
      if (var0.LIGHT0_DIRECTION != null) {
         var0.LIGHT0_DIRECTION.set(shaderLightDirections[0]);
      }

      if (var0.LIGHT1_DIRECTION != null) {
         var0.LIGHT1_DIRECTION.set(shaderLightDirections[1]);
      }
   }

   public static void setShaderColor(float var0, float var1, float var2, float var3) {
      assertOnRenderThread();
      shaderColor[0] = var0;
      shaderColor[1] = var1;
      shaderColor[2] = var2;
      shaderColor[3] = var3;
   }

   public static float[] getShaderColor() {
      assertOnRenderThread();
      return shaderColor;
   }

   public static void drawElements(int var0, int var1, int var2) {
      assertOnRenderThread();
      GlStateManager._drawElements(var0, var1, var2, 0L);
   }

   public static void lineWidth(float var0) {
      assertOnRenderThread();
      shaderLineWidth = var0;
   }

   public static float getShaderLineWidth() {
      assertOnRenderThread();
      return shaderLineWidth;
   }

   public static void pixelStore(int var0, int var1) {
      GlStateManager._pixelStore(var0, var1);
   }

   public static void readPixels(int var0, int var1, int var2, int var3, int var4, int var5, ByteBuffer var6) {
      assertOnRenderThread();
      GlStateManager._readPixels(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void getString(int var0, Consumer<String> var1) {
      assertOnRenderThread();
      var1.accept(GlStateManager._getString(var0));
   }

   public static String getBackendDescription() {
      return String.format(Locale.ROOT, "LWJGL version %s", GLX._getLWJGLVersion());
   }

   public static String getApiDescription() {
      return apiDescription;
   }

   public static TimeSource.NanoTimeSource initBackendSystem() {
      return GLX._initGlfw()::getAsLong;
   }

   public static void initRenderer(int var0, boolean var1) {
      GLX._init(var0, var1);
      apiDescription = GLX.getOpenGLVersionString();
   }

   public static void setErrorCallback(GLFWErrorCallbackI var0) {
      GLX._setGlfwErrorCallback(var0);
   }

   public static void renderCrosshair(int var0) {
      assertOnRenderThread();
      GLX._renderCrosshair(var0, true, true, true);
   }

   public static String getCapsString() {
      assertOnRenderThread();
      return "Using framebuffer using OpenGL 3.2";
   }

   public static void setupDefaultState(int var0, int var1, int var2, int var3) {
      GlStateManager._clearDepth(1.0);
      GlStateManager._enableDepthTest();
      GlStateManager._depthFunc(515);
      projectionMatrix.identity();
      savedProjectionMatrix.identity();
      modelViewStack.clear();
      textureMatrix.identity();
      GlStateManager._viewport(var0, var1, var2, var3);
   }

   public static int maxSupportedTextureSize() {
      if (MAX_SUPPORTED_TEXTURE_SIZE == -1) {
         assertOnRenderThreadOrInit();
         int var0 = GlStateManager._getInteger(3379);

         for (int var1 = Math.max(32768, var0); var1 >= 1024; var1 >>= 1) {
            GlStateManager._texImage2D(32868, 0, 6408, var1, var1, 0, 6408, 5121, null);
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

   public static void glBindBuffer(int var0, int var1) {
      GlStateManager._glBindBuffer(var0, var1);
   }

   public static void glBindVertexArray(int var0) {
      GlStateManager._glBindVertexArray(var0);
   }

   public static void glBufferData(int var0, ByteBuffer var1, int var2) {
      assertOnRenderThreadOrInit();
      GlStateManager._glBufferData(var0, var1, var2);
   }

   public static void glDeleteBuffers(int var0) {
      assertOnRenderThread();
      GlStateManager._glDeleteBuffers(var0);
   }

   public static void glDeleteVertexArrays(int var0) {
      assertOnRenderThread();
      GlStateManager._glDeleteVertexArrays(var0);
   }

   public static void glUniform1i(int var0, int var1) {
      assertOnRenderThread();
      GlStateManager._glUniform1i(var0, var1);
   }

   public static void glUniform1(int var0, IntBuffer var1) {
      assertOnRenderThread();
      GlStateManager._glUniform1(var0, var1);
   }

   public static void glUniform2(int var0, IntBuffer var1) {
      assertOnRenderThread();
      GlStateManager._glUniform2(var0, var1);
   }

   public static void glUniform3(int var0, IntBuffer var1) {
      assertOnRenderThread();
      GlStateManager._glUniform3(var0, var1);
   }

   public static void glUniform4(int var0, IntBuffer var1) {
      assertOnRenderThread();
      GlStateManager._glUniform4(var0, var1);
   }

   public static void glUniform1(int var0, FloatBuffer var1) {
      assertOnRenderThread();
      GlStateManager._glUniform1(var0, var1);
   }

   public static void glUniform2(int var0, FloatBuffer var1) {
      assertOnRenderThread();
      GlStateManager._glUniform2(var0, var1);
   }

   public static void glUniform3(int var0, FloatBuffer var1) {
      assertOnRenderThread();
      GlStateManager._glUniform3(var0, var1);
   }

   public static void glUniform4(int var0, FloatBuffer var1) {
      assertOnRenderThread();
      GlStateManager._glUniform4(var0, var1);
   }

   public static void glUniformMatrix2(int var0, boolean var1, FloatBuffer var2) {
      assertOnRenderThread();
      GlStateManager._glUniformMatrix2(var0, var1, var2);
   }

   public static void glUniformMatrix3(int var0, boolean var1, FloatBuffer var2) {
      assertOnRenderThread();
      GlStateManager._glUniformMatrix3(var0, var1, var2);
   }

   public static void glUniformMatrix4(int var0, boolean var1, FloatBuffer var2) {
      assertOnRenderThread();
      GlStateManager._glUniformMatrix4(var0, var1, var2);
   }

   public static void setupOverlayColor(int var0, int var1) {
      assertOnRenderThread();
      setShaderTexture(1, var0);
   }

   public static void teardownOverlayColor() {
      assertOnRenderThread();
      setShaderTexture(1, 0);
   }

   public static void setupLevelDiffuseLighting(Vector3f var0, Vector3f var1) {
      assertOnRenderThread();
      setShaderLights(var0, var1);
   }

   public static void setupGuiFlatDiffuseLighting(Vector3f var0, Vector3f var1) {
      assertOnRenderThread();
      GlStateManager.setupGuiFlatDiffuseLighting(var0, var1);
   }

   public static void setupGui3DDiffuseLighting(Vector3f var0, Vector3f var1) {
      assertOnRenderThread();
      GlStateManager.setupGui3DDiffuseLighting(var0, var1);
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

   public static Tesselator renderThreadTesselator() {
      assertOnRenderThread();
      return RENDER_THREAD_TESSELATOR;
   }

   public static void defaultBlendFunc() {
      blendFuncSeparate(
         GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
      );
   }

   public static void overlayBlendFunc() {
      blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }

   @Deprecated
   public static void runAsFancy(Runnable var0) {
      boolean var1 = Minecraft.useShaderTransparency();
      if (!var1) {
         var0.run();
      } else {
         OptionInstance var2 = Minecraft.getInstance().options.graphicsMode();
         GraphicsStatus var3 = (GraphicsStatus)var2.get();
         var2.set(GraphicsStatus.FANCY);
         var0.run();
         var2.set(var3);
      }
   }

   public static void setShader(Supplier<ShaderInstance> var0) {
      assertOnRenderThread();
      shader = (ShaderInstance)var0.get();
   }

   @Nullable
   public static ShaderInstance getShader() {
      assertOnRenderThread();
      return shader;
   }

   public static void setShaderTexture(int var0, ResourceLocation var1) {
      assertOnRenderThread();
      if (var0 >= 0 && var0 < shaderTextures.length) {
         TextureManager var2 = Minecraft.getInstance().getTextureManager();
         AbstractTexture var3 = var2.getTexture(var1);
         shaderTextures[var0] = var3.getId();
      }
   }

   public static void setShaderTexture(int var0, int var1) {
      assertOnRenderThread();
      if (var0 >= 0 && var0 < shaderTextures.length) {
         shaderTextures[var0] = var1;
      }
   }

   public static int getShaderTexture(int var0) {
      assertOnRenderThread();
      return var0 >= 0 && var0 < shaderTextures.length ? shaderTextures[var0] : 0;
   }

   public static void setProjectionMatrix(Matrix4f var0, VertexSorting var1) {
      assertOnRenderThread();
      projectionMatrix = new Matrix4f(var0);
      vertexSorting = var1;
   }

   public static void setTextureMatrix(Matrix4f var0) {
      assertOnRenderThread();
      textureMatrix = new Matrix4f(var0);
   }

   public static void resetTextureMatrix() {
      assertOnRenderThread();
      textureMatrix.identity();
   }

   public static void backupProjectionMatrix() {
      assertOnRenderThread();
      savedProjectionMatrix = projectionMatrix;
      savedVertexSorting = vertexSorting;
   }

   public static void restoreProjectionMatrix() {
      assertOnRenderThread();
      projectionMatrix = savedProjectionMatrix;
      vertexSorting = savedVertexSorting;
   }

   public static Matrix4f getProjectionMatrix() {
      assertOnRenderThread();
      return projectionMatrix;
   }

   public static Matrix4f getModelViewMatrix() {
      assertOnRenderThread();
      return modelViewStack;
   }

   public static Matrix4fStack getModelViewStack() {
      assertOnRenderThread();
      return modelViewStack;
   }

   public static Matrix4f getTextureMatrix() {
      assertOnRenderThread();
      return textureMatrix;
   }

   public static RenderSystem.AutoStorageIndexBuffer getSequentialBuffer(VertexFormat.Mode var0) {
      assertOnRenderThread();

      return switch (var0) {
         case QUADS -> sharedSequentialQuad;
         case LINES -> sharedSequentialLines;
         default -> sharedSequential;
      };
   }

   public static void setShaderGameTime(long var0, float var2) {
      assertOnRenderThread();
      shaderGameTime = ((float)(var0 % 24000L) + var2) / 24000.0F;
   }

   public static float getShaderGameTime() {
      assertOnRenderThread();
      return shaderGameTime;
   }

   public static VertexSorting getVertexSorting() {
      assertOnRenderThread();
      return vertexSorting;
   }

   public static final class AutoStorageIndexBuffer {
      private final int vertexStride;
      private final int indexStride;
      private final RenderSystem.AutoStorageIndexBuffer.IndexGenerator generator;
      private int name;
      private VertexFormat.IndexType type = VertexFormat.IndexType.SHORT;
      private int indexCount;

      AutoStorageIndexBuffer(int var1, int var2, RenderSystem.AutoStorageIndexBuffer.IndexGenerator var3) {
         super();
         this.vertexStride = var1;
         this.indexStride = var2;
         this.generator = var3;
      }

      public boolean hasStorage(int var1) {
         return var1 <= this.indexCount;
      }

      public void bind(int var1) {
         if (this.name == 0) {
            this.name = GlStateManager._glGenBuffers();
         }

         GlStateManager._glBindBuffer(34963, this.name);
         this.ensureStorage(var1);
      }

      private void ensureStorage(int var1) {
         if (!this.hasStorage(var1)) {
            var1 = Mth.roundToward(var1 * 2, this.indexStride);
            RenderSystem.LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", this.indexCount, var1);
            int var2 = var1 / this.indexStride;
            int var3 = var2 * this.vertexStride;
            VertexFormat.IndexType var4 = VertexFormat.IndexType.least(var3);
            int var5 = Mth.roundToward(var1 * var4.bytes, 4);
            GlStateManager._glBufferData(34963, (long)var5, 35048);
            ByteBuffer var6 = GlStateManager._glMapBuffer(34963, 35001);
            if (var6 == null) {
               throw new RuntimeException("Failed to map GL buffer");
            } else {
               this.type = var4;
               it.unimi.dsi.fastutil.ints.IntConsumer var7 = this.intConsumer(var6);

               for (int var8 = 0; var8 < var1; var8 += this.indexStride) {
                  this.generator.accept(var7, var8 * this.vertexStride / this.indexStride);
               }

               GlStateManager._glUnmapBuffer(34963);
               this.indexCount = var1;
            }
         }
      }

      private it.unimi.dsi.fastutil.ints.IntConsumer intConsumer(ByteBuffer var1) {
         switch (this.type) {
            case SHORT:
               return var1x -> var1.putShort((short)var1x);
            case INT:
            default:
               return var1::putInt;
         }
      }

      public VertexFormat.IndexType type() {
         return this.type;
      }

      interface IndexGenerator {
         void accept(it.unimi.dsi.fastutil.ints.IntConsumer var1, int var2);
      }
   }
}
