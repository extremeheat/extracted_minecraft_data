package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.PipelineCache;
import com.mojang.blaze3d.platform.SDLEventHandler;
import com.mojang.blaze3d.platform.SdlDebug;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.GpuFence;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.device.GpuBackend;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.IndexType;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntConsumer;
import net.minecraft.SharedConstants;
import net.minecraft.client.renderer.DynamicGpuData;
import net.minecraft.util.ArrayListDeque;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeSource;
import net.minecraft.util.Util;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.jspecify.annotations.Nullable;
import org.lwjgl.Version;
import org.lwjgl.sdl.SDLError;
import org.lwjgl.sdl.SDLHints;
import org.lwjgl.sdl.SDLInit;
import org.lwjgl.sdl.SDLTimer;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class RenderSystem {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final double DEFAULT_DEPTH_CLEAR_VALUE = 0.0;
   public static final int MINIMUM_ATLAS_TEXTURE_SIZE = 1024;
   public static final int PROJECTION_MATRIX_UBO_SIZE = (new Std140SizeCalculator()).putMat4f().get();
   private static @Nullable Thread renderThread;
   private static @Nullable GpuDevice DEVICE;
   private static @Nullable GpuBackend BACKEND;
   private static final AutoStorageIndexBuffer sharedSequential = new AutoStorageIndexBuffer(1, 1, IntConsumer::accept);
   private static final AutoStorageIndexBuffer sharedSequentialQuad = new AutoStorageIndexBuffer(4, 6, (c, i) -> {
      c.accept(i);
      c.accept(i + 1);
      c.accept(i + 2);
      c.accept(i + 2);
      c.accept(i + 3);
      c.accept(i);
   });
   private static final AutoStorageIndexBuffer sharedSequentialLines = new AutoStorageIndexBuffer(4, 6, (c, i) -> {
      c.accept(i);
      c.accept(i + 1);
      c.accept(i + 2);
      c.accept(i + 3);
      c.accept(i + 2);
      c.accept(i + 1);
   });
   private static ProjectionType projectionType;
   private static ProjectionType savedProjectionType;
   private static final Matrix4fStack modelViewStack;
   private static @Nullable GpuBufferSlice shaderFog;
   private static @Nullable GpuBufferSlice shaderLightDirections;
   private static @Nullable GpuBufferSlice projectionMatrixBuffer;
   private static @Nullable GpuBufferSlice savedProjectionMatrixBuffer;
   private static final AtomicLong pollEventsWaitStart;
   private static final AtomicBoolean pollingEvents;
   private static final ArrayListDeque<GpuAsyncTask> PENDING_FENCES;
   public static boolean isRenderingLevel;
   private static @Nullable GpuBuffer globalSettingsUniform;
   private static @Nullable DynamicGpuData dynamicGpuData;
   private static final ScissorState scissorStateForRenderTypeDraws;
   private static final SamplerCache samplerCache;
   private static @Nullable PipelineCache fallbackPipelineCache;
   private static @Nullable PipelineCache currentPipelineCache;

   public RenderSystem() {
      super();
   }

   public static SamplerCache getSamplerCache() {
      return samplerCache;
   }

   public static void setFallbackPipelineCache(final PipelineCache pipelineCache) {
      if (fallbackPipelineCache != null) {
         throw new IllegalStateException("Fallback pipeline cache already set");
      } else {
         fallbackPipelineCache = pipelineCache;
      }
   }

   public static @Nullable PipelineCache setCurrentPipelineCache(final PipelineCache pipelineCache) {
      PipelineCache oldCache = currentPipelineCache;
      currentPipelineCache = pipelineCache;
      return oldCache;
   }

   public static @Nullable CompiledRenderPipeline getCompiledPipelineNullable(final RenderPipeline pipeline) {
      if (currentPipelineCache != null) {
         CompiledRenderPipeline cachedPipeline = currentPipelineCache.get(pipeline);
         if (cachedPipeline != null) {
            return cachedPipeline;
         }
      }

      if (fallbackPipelineCache == null) {
         throw new IllegalStateException("Fallback pipeline cache not yet set");
      } else {
         return fallbackPipelineCache.get(pipeline);
      }
   }

   public static CompiledRenderPipeline getCompiledPipeline(final RenderPipeline pipeline) {
      CompiledRenderPipeline compiledPipeline = getCompiledPipelineNullable(pipeline);
      if (compiledPipeline != null) {
         return compiledPipeline;
      } else {
         throw new IllegalStateException("Failed to find or load pipeline " + String.valueOf(pipeline.getLocation()));
      }
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

   public static void pollEvents(final SDLEventHandler eventHandler) {
      pollEventsWaitStart.set(Util.getMillis());
      pollingEvents.set(true);
      eventHandler.pollEvents();
      pollingEvents.set(false);
   }

   public static boolean isFrozenAtPollEvents() {
      return pollingEvents.get() && Util.getMillis() - pollEventsWaitStart.get() > 200L;
   }

   public static void pumpEvents(final SDLEventHandler eventHandler) {
      pollEventsWaitStart.set(Util.getMillis());
      pollingEvents.set(true);
      eventHandler.pumpEvents();
      pollingEvents.set(false);
   }

   public static void setShaderFog(final GpuBufferSlice fog) {
      shaderFog = fog;
   }

   public static @Nullable GpuBufferSlice getShaderFog() {
      return shaderFog;
   }

   public static void setShaderLights(final GpuBufferSlice buffer) {
      shaderLightDirections = buffer;
   }

   public static @Nullable GpuBufferSlice getShaderLights() {
      return shaderLightDirections;
   }

   public static void enableScissorForRenderTypeDraws(final int x, final int y, final int width, final int height) {
      scissorStateForRenderTypeDraws.enable(x, y, width, height);
   }

   public static void disableScissorForRenderTypeDraws() {
      scissorStateForRenderTypeDraws.disable();
   }

   public static ScissorState getScissorStateForRenderTypeDraws() {
      return scissorStateForRenderTypeDraws;
   }

   public static String getBackendDescription() {
      return String.format(Locale.ROOT, "LWJGL version %s", Version.getVersion());
   }

   public static TimeSource.NanoTimeSource initBackendSystem() {
      SdlDebug.init();
      SDLInit.SDL_SetAppMetadataProperty("SDL.app.metadata.name", "Minecraft");
      SDLInit.SDL_SetAppMetadataProperty("SDL.app.metadata.version", SharedConstants.getCurrentVersion().name());
      SDLInit.SDL_SetAppMetadataProperty("SDL.app.metadata.identifier", "com.mojang.minecraft");
      SDLInit.SDL_SetAppMetadataProperty("SDL.app.metadata.creator", "Mojang Studios");
      SDLInit.SDL_SetAppMetadataProperty("SDL.app.metadata.copyright", "Copyright Mojang AB.");
      SDLInit.SDL_SetAppMetadataProperty("SDL.app.metadata.url", "https://www.minecraft.net");
      SDLInit.SDL_SetAppMetadataProperty("SDL.app.metadata.type", "game");
      SDLHints.SDL_SetHint("SDL_NO_SIGNAL_HANDLERS", "1");
      SDLHints.SDL_SetHint("SDL_VIDEO_MAC_FULLSCREEN_SPACES", "1");
      SDLHints.SDL_SetHint("SDL_VIDEO_MAC_FULLSCREEN_MENU_VISIBILITY", "0");
      SDLHints.SDL_SetHint("SDL_QUIT_ON_LAST_WINDOW_CLOSE", "0");
      SDLHints.SDL_SetHint("SDL_MOUSE_FOCUS_CLICKTHROUGH", "1");
      SDLHints.SDL_SetHint("SDL_ENABLE_SCREEN_KEYBOARD", "0");
      SDLHints.SDL_SetHint("SDL_IME_IMPLEMENTED_UI", "composition, candidates");
      if (!SDLInit.SDL_Init(32)) {
         throw new IllegalStateException("Unable to initialize SDL: " + SDLError.SDL_GetError());
      } else {
         return SDLTimer::SDL_GetTicksNS;
      }
   }

   public static void initRenderer(final GpuDevice device) {
      if (DEVICE != null) {
         throw new IllegalStateException("RenderSystem.DEVICE already initialized");
      } else {
         DEVICE = device;
         dynamicGpuData = new DynamicGpuData();
         samplerCache.initialize();
      }
   }

   public static void shutdownRenderer() {
      if (currentPipelineCache != null) {
         currentPipelineCache.close();
      }

      if (fallbackPipelineCache != null) {
         fallbackPipelineCache.close();
      }

      sharedSequential.close();
      sharedSequentialQuad.close();
      sharedSequentialLines.close();
      samplerCache.close();
      if (dynamicGpuData != null) {
         dynamicGpuData.close();
      }

      if (DEVICE != null) {
         DEVICE.close();
      }

   }

   public static void trackBackendLibraryForShutdown(final GpuBackend backend) {
      BACKEND = backend;
   }

   public static void unloadTrackedBackendLibrary() {
      if (BACKEND != null) {
         BACKEND.unloadLibrary();
         BACKEND = null;
      }
   }

   public static void setupDefaultState() {
      modelViewStack.clear();
   }

   public static void setProjectionMatrix(final GpuBufferSlice projectionMatrixBuffer, final ProjectionType type) {
      assertOnRenderThread();
      RenderSystem.projectionMatrixBuffer = projectionMatrixBuffer;
      projectionType = type;
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

   public static @Nullable GpuBufferSlice getProjectionMatrixBuffer() {
      assertOnRenderThread();
      return projectionMatrixBuffer;
   }

   public static Matrix4f getModelViewMatrixCopy() {
      assertOnRenderThread();
      return new Matrix4f(modelViewStack);
   }

   public static Matrix4fStack getModelViewStack() {
      assertOnRenderThread();
      return modelViewStack;
   }

   public static AutoStorageIndexBuffer getSequentialBuffer(final PrimitiveTopology primitiveTopology) {
      assertOnRenderThread();
      AutoStorageIndexBuffer var10000;
      switch (primitiveTopology) {
         case QUADS -> var10000 = sharedSequentialQuad;
         case LINES -> var10000 = sharedSequentialLines;
         default -> var10000 = sharedSequential;
      }

      return var10000;
   }

   public static void setGlobalSettingsUniform(final GpuBuffer buffer) {
      globalSettingsUniform = buffer;
   }

   public static @Nullable GpuBuffer getGlobalSettingsUniform() {
      return globalSettingsUniform;
   }

   public static ProjectionType getProjectionType() {
      assertOnRenderThread();
      return projectionType;
   }

   public static void queueFencedTask(final Runnable task) {
      PENDING_FENCES.addLast(new GpuAsyncTask(task, getDevice().createCommandEncoder().createFence()));
   }

   public static void executePendingTasks() {
      for(GpuAsyncTask task = PENDING_FENCES.peekFirst(); task != null; task = PENDING_FENCES.peekFirst()) {
         if (!task.fence.awaitCompletion(0L)) {
            return;
         }

         try {
            task.callback.run();
         } finally {
            task.fence.close();
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

   public static @Nullable GpuDevice tryGetDevice() {
      return DEVICE;
   }

   public static boolean isWireframeAvailable() {
      return getDevice().getDeviceInfo().features().wireframeFillMode();
   }

   public static DynamicGpuData getDynamicUniforms() {
      if (dynamicGpuData == null) {
         throw new IllegalStateException("Can't getDynamicUniforms() before device was initialized");
      } else {
         return dynamicGpuData;
      }
   }

   public static void bindDefaultUniforms(final RenderPass renderPass) {
      GpuBufferSlice projectionMatrix = getProjectionMatrixBuffer();
      if (projectionMatrix != null) {
         renderPass.setUniform("Projection", projectionMatrix);
      }

      GpuBufferSlice fog = getShaderFog();
      if (fog != null) {
         renderPass.setUniform("Fog", fog);
      }

      GpuBuffer globalUniform = getGlobalSettingsUniform();
      if (globalUniform != null) {
         renderPass.setUniform("Globals", globalUniform);
      }

      GpuBufferSlice shaderLights = getShaderLights();
      if (shaderLights != null) {
         renderPass.setUniform("Lighting", shaderLights);
      }

   }

   public static void resizeAllAutoStorageIndexBuffers() {
      sharedSequential.resizeToRequestedIndexCount();
      sharedSequentialQuad.resizeToRequestedIndexCount();
      sharedSequentialLines.resizeToRequestedIndexCount();
   }

   static {
      projectionType = ProjectionType.PERSPECTIVE;
      savedProjectionType = ProjectionType.PERSPECTIVE;
      modelViewStack = new Matrix4fStack(16);
      shaderFog = null;
      pollEventsWaitStart = new AtomicLong();
      pollingEvents = new AtomicBoolean(false);
      PENDING_FENCES = new ArrayListDeque<GpuAsyncTask>();
      isRenderingLevel = false;
      scissorStateForRenderTypeDraws = new ScissorState();
      samplerCache = new SamplerCache();
   }

   public static final class AutoStorageIndexBuffer implements AutoCloseable {
      private final int vertexStride;
      private final int indexStride;
      private final IndexGenerator generator;
      private @Nullable GpuBuffer buffer;
      private IndexType type;
      private int indexCount;
      private int maxRequestedIndexCount;

      private AutoStorageIndexBuffer(final int vertexStride, final int indexStride, final IndexGenerator generator) {
         super();
         this.type = IndexType.SHORT;
         this.vertexStride = vertexStride;
         this.indexStride = indexStride;
         this.generator = generator;
      }

      public void close() {
         if (this.buffer != null) {
            this.buffer.close();
         }

      }

      public boolean hasStorage(final int indexCount) {
         return indexCount <= this.indexCount;
      }

      public void requestIndexCount(final int indexCount) {
         this.maxRequestedIndexCount = Math.max(this.maxRequestedIndexCount, indexCount);
      }

      public void resizeToRequestedIndexCount() {
         this.ensureStorage(this.maxRequestedIndexCount);
      }

      public GpuBuffer getBuffer(final int indexCount) {
         this.requestIndexCount(indexCount);
         this.ensureStorage(indexCount);
         return this.buffer;
      }

      public GpuBuffer getBuffer() {
         return this.buffer;
      }

      private void ensureStorage(int indexCount) {
         if (!this.hasStorage(indexCount)) {
            indexCount = Mth.roundToward(indexCount * 2, this.indexStride);
            RenderSystem.LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", this.indexCount, indexCount);
            int primitiveCount = indexCount / this.indexStride;
            int vertexCount = primitiveCount * this.vertexStride;
            IndexType type = IndexType.least(vertexCount);
            int bufferSize = Mth.roundToward(indexCount * type.bytes, 4);
            ByteBuffer data = MemoryUtil.memAlloc(bufferSize);

            try {
               this.type = type;
               it.unimi.dsi.fastutil.ints.IntConsumer intConsumer = this.intConsumer(data);

               for(int ii = 0; ii < indexCount; ii += this.indexStride) {
                  this.generator.accept(intConsumer, ii * this.vertexStride / this.indexStride);
               }

               data.flip();
               if (this.buffer != null) {
                  this.buffer.close();
               }

               this.buffer = RenderSystem.getDevice().createBuffer(() -> "Auto Storage index buffer", 64, data);
            } finally {
               MemoryUtil.memFree(data);
            }

            this.indexCount = indexCount;
         }
      }

      private it.unimi.dsi.fastutil.ints.IntConsumer intConsumer(final ByteBuffer buffer) {
         switch (this.type) {
            case SHORT:
               return (value) -> buffer.putShort((short)value);
            case INT:
            default:
               Objects.requireNonNull(buffer);
               return buffer::putInt;
         }
      }

      public IndexType type() {
         return this.type;
      }

      private interface IndexGenerator {
         void accept(final it.unimi.dsi.fastutil.ints.IntConsumer consumer, final int start);
      }
   }

   private static record GpuAsyncTask(Runnable callback, GpuFence fence) {
      private GpuAsyncTask {
         super();
      }
   }
}
