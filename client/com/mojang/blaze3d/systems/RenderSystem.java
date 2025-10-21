package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.TracyFrameCapture;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.IntConsumer;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DynamicUniforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ArrayListDeque;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeSource;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@DontObfuscate
public class RenderSystem {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
   public static final int PROJECTION_MATRIX_UBO_SIZE = (new Std140SizeCalculator()).putMat4f().get();
   @Nullable
   private static Thread renderThread;
   @Nullable
   private static GpuDevice DEVICE;
   private static double lastDrawTime = 4.9E-324;
   private static final AutoStorageIndexBuffer sharedSequential = new AutoStorageIndexBuffer(1, 1, IntConsumer::accept);
   private static final AutoStorageIndexBuffer sharedSequentialQuad = new AutoStorageIndexBuffer(4, 6, (var0, var1) -> {
      var0.accept(var1);
      var0.accept(var1 + 1);
      var0.accept(var1 + 2);
      var0.accept(var1 + 2);
      var0.accept(var1 + 3);
      var0.accept(var1);
   });
   private static final AutoStorageIndexBuffer sharedSequentialLines = new AutoStorageIndexBuffer(4, 6, (var0, var1) -> {
      var0.accept(var1);
      var0.accept(var1 + 1);
      var0.accept(var1 + 2);
      var0.accept(var1 + 3);
      var0.accept(var1 + 2);
      var0.accept(var1 + 1);
   });
   private static ProjectionType projectionType;
   private static ProjectionType savedProjectionType;
   private static final Matrix4fStack modelViewStack;
   @Nullable
   private static GpuBufferSlice shaderFog;
   @Nullable
   private static GpuBufferSlice shaderLightDirections;
   @Nullable
   private static GpuBufferSlice projectionMatrixBuffer;
   @Nullable
   private static GpuBufferSlice savedProjectionMatrixBuffer;
   private static String apiDescription;
   private static final AtomicLong pollEventsWaitStart;
   private static final AtomicBoolean pollingEvents;
   private static final ArrayListDeque<GpuAsyncTask> PENDING_FENCES;
   @Nullable
   public static GpuTextureView outputColorTextureOverride;
   @Nullable
   public static GpuTextureView outputDepthTextureOverride;
   @Nullable
   private static GpuBuffer globalSettingsUniform;
   @Nullable
   private static DynamicUniforms dynamicUniforms;
   private static final ScissorState scissorStateForRenderTypeDraws;
   private static SamplerCache samplerCache;

   public RenderSystem() {
      super();
   }

   public static SamplerCache getSamplerCache() {
      return samplerCache;
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

   public static void assertOnRenderThread() {
      if (!isOnRenderThread()) {
         throw constructThreadException();
      }
   }

   private static IllegalStateException constructThreadException() {
      return new IllegalStateException("Rendersystem called from wrong thread");
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

   public static void flipFrame(Window var0, @Nullable TracyFrameCapture var1) {
      pollEvents();
      Tesselator.getInstance().clear();
      GLFW.glfwSwapBuffers(var0.handle());
      if (var1 != null) {
         var1.endFrame();
      }

      dynamicUniforms.reset();
      Minecraft.getInstance().levelRenderer.endFrame();
      pollEvents();
   }

   public static void limitDisplayFPS(int var0) {
      double var1 = lastDrawTime + 1.0 / (double)var0;

      double var3;
      for(var3 = GLFW.glfwGetTime(); var3 < var1; var3 = GLFW.glfwGetTime()) {
         GLFW.glfwWaitEventsTimeout(var1 - var3);
      }

      lastDrawTime = var3;
   }

   public static void setShaderFog(GpuBufferSlice var0) {
      shaderFog = var0;
   }

   @Nullable
   public static GpuBufferSlice getShaderFog() {
      return shaderFog;
   }

   public static void setShaderLights(GpuBufferSlice var0) {
      shaderLightDirections = var0;
   }

   @Nullable
   public static GpuBufferSlice getShaderLights() {
      return shaderLightDirections;
   }

   public static void enableScissorForRenderTypeDraws(int var0, int var1, int var2, int var3) {
      scissorStateForRenderTypeDraws.enable(var0, var1, var2, var3);
   }

   public static void disableScissorForRenderTypeDraws() {
      scissorStateForRenderTypeDraws.disable();
   }

   public static ScissorState getScissorStateForRenderTypeDraws() {
      return scissorStateForRenderTypeDraws;
   }

   public static String getBackendDescription() {
      return String.format(Locale.ROOT, "LWJGL version %s", GLX._getLWJGLVersion());
   }

   public static String getApiDescription() {
      return apiDescription;
   }

   public static TimeSource.NanoTimeSource initBackendSystem() {
      LongSupplier var10000 = GLX._initGlfw();
      Objects.requireNonNull(var10000);
      return var10000::getAsLong;
   }

   public static void initRenderer(long var0, int var2, boolean var3, BiFunction<ResourceLocation, ShaderType, String> var4, boolean var5) {
      DEVICE = new GlDevice(var0, var2, var3, var4, var5);
      apiDescription = getDevice().getImplementationInformation();
      dynamicUniforms = new DynamicUniforms();
      samplerCache.initialize();
   }

   public static void setErrorCallback(GLFWErrorCallbackI var0) {
      GLX._setGlfwErrorCallback(var0);
   }

   public static void setupDefaultState() {
      modelViewStack.clear();
   }

   public static void setProjectionMatrix(GpuBufferSlice var0, ProjectionType var1) {
      assertOnRenderThread();
      projectionMatrixBuffer = var0;
      projectionType = var1;
   }

   public static void backupProjectionMatrix() {
      assertOnRenderThread();
      savedProjectionMatrixBuffer = projectionMatrixBuffer;
      savedProjectionType = projectionType;
   }

   public static void restoreProjectionMatrix() {
      assertOnRenderThread();
      projectionMatrixBuffer = savedProjectionMatrixBuffer;
      projectionType = savedProjectionType;
   }

   @Nullable
   public static GpuBufferSlice getProjectionMatrixBuffer() {
      assertOnRenderThread();
      return projectionMatrixBuffer;
   }

   public static Matrix4f getModelViewMatrix() {
      assertOnRenderThread();
      return modelViewStack;
   }

   public static Matrix4fStack getModelViewStack() {
      assertOnRenderThread();
      return modelViewStack;
   }

   public static AutoStorageIndexBuffer getSequentialBuffer(VertexFormat.Mode var0) {
      assertOnRenderThread();
      AutoStorageIndexBuffer var10000;
      switch (var0) {
         case QUADS -> var10000 = sharedSequentialQuad;
         case LINES -> var10000 = sharedSequentialLines;
         default -> var10000 = sharedSequential;
      }

      return var10000;
   }

   public static void setGlobalSettingsUniform(GpuBuffer var0) {
      globalSettingsUniform = var0;
   }

   @Nullable
   public static GpuBuffer getGlobalSettingsUniform() {
      return globalSettingsUniform;
   }

   public static ProjectionType getProjectionType() {
      assertOnRenderThread();
      return projectionType;
   }

   public static void queueFencedTask(Runnable var0) {
      PENDING_FENCES.addLast(new GpuAsyncTask(var0, getDevice().createCommandEncoder().createFence()));
   }

   public static void executePendingTasks() {
      for(GpuAsyncTask var0 = PENDING_FENCES.peekFirst(); var0 != null; var0 = PENDING_FENCES.peekFirst()) {
         if (!var0.fence.awaitCompletion(0L)) {
            return;
         }

         try {
            var0.callback.run();
         } finally {
            var0.fence.close();
         }

         PENDING_FENCES.removeFirst();
      }

   }

   public static GpuDevice getDevice() {
      if (DEVICE == null) {
         throw new IllegalStateException("Can't getDevice() before it was initialized");
      } else {
         return DEVICE;
      }
   }

   @Nullable
   public static GpuDevice tryGetDevice() {
      return DEVICE;
   }

   public static DynamicUniforms getDynamicUniforms() {
      if (dynamicUniforms == null) {
         throw new IllegalStateException("Can't getDynamicUniforms() before device was initialized");
      } else {
         return dynamicUniforms;
      }
   }

   public static void bindDefaultUniforms(RenderPass var0) {
      GpuBufferSlice var1 = getProjectionMatrixBuffer();
      if (var1 != null) {
         var0.setUniform("Projection", var1);
      }

      GpuBufferSlice var2 = getShaderFog();
      if (var2 != null) {
         var0.setUniform("Fog", var2);
      }

      GpuBuffer var3 = getGlobalSettingsUniform();
      if (var3 != null) {
         var0.setUniform("Globals", var3);
      }

      GpuBufferSlice var4 = getShaderLights();
      if (var4 != null) {
         var0.setUniform("Lighting", var4);
      }

   }

   static {
      projectionType = ProjectionType.PERSPECTIVE;
      savedProjectionType = ProjectionType.PERSPECTIVE;
      modelViewStack = new Matrix4fStack(16);
      shaderFog = null;
      apiDescription = "Unknown";
      pollEventsWaitStart = new AtomicLong();
      pollingEvents = new AtomicBoolean(false);
      PENDING_FENCES = new ArrayListDeque<GpuAsyncTask>();
      scissorStateForRenderTypeDraws = new ScissorState();
      samplerCache = new SamplerCache();
   }

   public static final class AutoStorageIndexBuffer {
      private final int vertexStride;
      private final int indexStride;
      private final IndexGenerator generator;
      @Nullable
      private GpuBuffer buffer;
      private VertexFormat.IndexType type;
      private int indexCount;

      AutoStorageIndexBuffer(int var1, int var2, IndexGenerator var3) {
         super();
         this.type = VertexFormat.IndexType.SHORT;
         this.vertexStride = var1;
         this.indexStride = var2;
         this.generator = var3;
      }

      public boolean hasStorage(int var1) {
         return var1 <= this.indexCount;
      }

      public GpuBuffer getBuffer(int var1) {
         this.ensureStorage(var1);
         return this.buffer;
      }

      private void ensureStorage(int var1) {
         if (!this.hasStorage(var1)) {
            var1 = Mth.roundToward(var1 * 2, this.indexStride);
            RenderSystem.LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", this.indexCount, var1);
            int var2 = var1 / this.indexStride;
            int var3 = var2 * this.vertexStride;
            VertexFormat.IndexType var4 = VertexFormat.IndexType.least(var3);
            int var5 = Mth.roundToward(var1 * var4.bytes, 4);
            ByteBuffer var6 = MemoryUtil.memAlloc(var5);

            try {
               this.type = var4;
               it.unimi.dsi.fastutil.ints.IntConsumer var7 = this.intConsumer(var6);

               for(int var8 = 0; var8 < var1; var8 += this.indexStride) {
                  this.generator.accept(var7, var8 * this.vertexStride / this.indexStride);
               }

               var6.flip();
               if (this.buffer != null) {
                  this.buffer.close();
               }

               this.buffer = RenderSystem.getDevice().createBuffer(() -> "Auto Storage index buffer", 64, var6);
            } finally {
               MemoryUtil.memFree(var6);
            }

            this.indexCount = var1;
         }
      }

      private it.unimi.dsi.fastutil.ints.IntConsumer intConsumer(ByteBuffer var1) {
         switch (this.type) {
            case SHORT:
               return (var1x) -> var1.putShort((short)var1x);
            case INT:
            default:
               Objects.requireNonNull(var1);
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

   static record GpuAsyncTask(Runnable callback, GpuFence fence) {
      final Runnable callback;
      final GpuFence fence;

      GpuAsyncTask(Runnable var1, GpuFence var2) {
         super();
         this.callback = var1;
         this.fence = var2;
      }
   }
}
