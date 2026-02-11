package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.DataFixUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debugchart.BandwidthDebugChart;
import net.minecraft.client.gui.components.debugchart.FpsDebugChart;
import net.minecraft.client.gui.components.debugchart.PingDebugChart;
import net.minecraft.client.gui.components.debugchart.ProfilerPieChart;
import net.minecraft.client.gui.components.debugchart.TpsDebugChart;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkLoadStatusView;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraft.util.debugchart.TpsDebugDimensions;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

public class DebugScreenOverlay {
   private static final float CROSSHAIR_SCALE = 0.01F;
   private static final int CROSSHAIR_INDEX_COUNT = 36;
   private static final int MARGIN_RIGHT = 2;
   private static final int MARGIN_LEFT = 2;
   private static final int MARGIN_TOP = 2;
   private final Minecraft minecraft;
   private final Font font;
   private final GpuBuffer crosshairBuffer;
   private final RenderSystem.AutoStorageIndexBuffer crosshairIndicies;
   private @Nullable ChunkPos lastPos;
   private @Nullable LevelChunk clientChunk;
   private @Nullable CompletableFuture<LevelChunk> serverChunk;
   private boolean renderProfilerChart;
   private boolean renderFpsCharts;
   private boolean renderNetworkCharts;
   private boolean renderLightmapTexture;
   private final LocalSampleLogger frameTimeLogger;
   private final LocalSampleLogger tickTimeLogger;
   private final LocalSampleLogger pingLogger;
   private final LocalSampleLogger bandwidthLogger;
   private final Map<RemoteDebugSampleType, LocalSampleLogger> remoteSupportingLoggers;
   private final FpsDebugChart fpsChart;
   private final TpsDebugChart tpsChart;
   private final PingDebugChart pingChart;
   private final BandwidthDebugChart bandwidthChart;
   private final ProfilerPieChart profilerPieChart;

   public DebugScreenOverlay(final Minecraft minecraft) {
      super();
      this.crosshairIndicies = RenderSystem.getSequentialBuffer(VertexFormat.Mode.LINES);
      this.frameTimeLogger = new LocalSampleLogger(1);
      this.tickTimeLogger = new LocalSampleLogger(TpsDebugDimensions.values().length);
      this.pingLogger = new LocalSampleLogger(1);
      this.bandwidthLogger = new LocalSampleLogger(1);
      this.remoteSupportingLoggers = Map.of(RemoteDebugSampleType.TICK_TIME, this.tickTimeLogger);
      this.minecraft = minecraft;
      this.font = minecraft.font;
      this.fpsChart = new FpsDebugChart(this.font, this.frameTimeLogger);
      this.tpsChart = new TpsDebugChart(this.font, this.tickTimeLogger, () -> minecraft.level == null ? 0.0F : minecraft.level.tickRateManager().millisecondsPerTick());
      this.pingChart = new PingDebugChart(this.font, this.pingLogger);
      this.bandwidthChart = new BandwidthDebugChart(this.font, this.bandwidthLogger);
      this.profilerPieChart = new ProfilerPieChart(this.font);

      try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH.getVertexSize() * 12 * 2)) {
         BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
         bufferBuilder.addVertex(0.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(1.0F, 0.0F, 0.0F).setLineWidth(4.0F);
         bufferBuilder.addVertex(1.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(1.0F, 0.0F, 0.0F).setLineWidth(4.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(0.0F, 1.0F, 0.0F).setLineWidth(4.0F);
         bufferBuilder.addVertex(0.0F, 1.0F, 0.0F).setColor(-16777216).setNormal(0.0F, 1.0F, 0.0F).setLineWidth(4.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(0.0F, 0.0F, 1.0F).setLineWidth(4.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 1.0F).setColor(-16777216).setNormal(0.0F, 0.0F, 1.0F).setLineWidth(4.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 0.0F).setColor(-65536).setNormal(1.0F, 0.0F, 0.0F).setLineWidth(2.0F);
         bufferBuilder.addVertex(1.0F, 0.0F, 0.0F).setColor(-65536).setNormal(1.0F, 0.0F, 0.0F).setLineWidth(2.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 0.0F).setColor(-16711936).setNormal(0.0F, 1.0F, 0.0F).setLineWidth(2.0F);
         bufferBuilder.addVertex(0.0F, 1.0F, 0.0F).setColor(-16711936).setNormal(0.0F, 1.0F, 0.0F).setLineWidth(2.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 0.0F).setColor(-8421377).setNormal(0.0F, 0.0F, 1.0F).setLineWidth(2.0F);
         bufferBuilder.addVertex(0.0F, 0.0F, 1.0F).setColor(-8421377).setNormal(0.0F, 0.0F, 1.0F).setLineWidth(2.0F);

         try (MeshData meshData = bufferBuilder.buildOrThrow()) {
            this.crosshairBuffer = RenderSystem.getDevice().createBuffer(() -> "Crosshair vertex buffer", 32, meshData.vertexBuffer());
         }
      }

   }

   public void clearChunkCache() {
      this.serverChunk = null;
      this.clientChunk = null;
   }

   public void render(final GuiGraphics graphics) {
      Options options = this.minecraft.options;
      if (this.minecraft.isGameLoadFinished() && (!options.hideGui || this.minecraft.screen != null)) {
         Collection<Identifier> visibleEntries = this.minecraft.debugEntries.getCurrentlyEnabled();
         if (!visibleEntries.isEmpty()) {
            graphics.nextStratum();
            ProfilerFiller profiler = Profiler.get();
            profiler.push("debug");
            ChunkPos chunkPos;
            if (this.minecraft.getCameraEntity() != null && this.minecraft.level != null) {
               BlockPos feetPos = this.minecraft.getCameraEntity().blockPosition();
               chunkPos = ChunkPos.containing(feetPos);
            } else {
               chunkPos = null;
            }

            if (!Objects.equals(this.lastPos, chunkPos)) {
               this.lastPos = chunkPos;
               this.clearChunkCache();
            }

            final List<String> leftLines = new ArrayList();
            final List<String> rightLines = new ArrayList();
            final Map<Identifier, Collection<String>> groups = new LinkedHashMap();
            final List<String> regularLines = new ArrayList();
            DebugScreenDisplayer displayer = new DebugScreenDisplayer() {
               {
                  Objects.requireNonNull(DebugScreenOverlay.this);
               }

               public void addPriorityLine(final String line) {
                  if (leftLines.size() > rightLines.size()) {
                     rightLines.add(line);
                  } else {
                     leftLines.add(line);
                  }

               }

               public void addLine(final String line) {
                  regularLines.add(line);
               }

               public void addToGroup(final Identifier group, final Collection<String> lines) {
                  ((Collection)groups.computeIfAbsent(group, (k) -> new ArrayList())).addAll(lines);
               }

               public void addToGroup(final Identifier group, final String lines) {
                  ((Collection)groups.computeIfAbsent(group, (k) -> new ArrayList())).add(lines);
               }
            };
            Level level = this.getLevel();

            for(Identifier id : visibleEntries) {
               DebugScreenEntry entry = DebugScreenEntries.getEntry(id);
               if (entry != null) {
                  entry.display(displayer, level, this.getClientChunk(), this.getServerChunk());
               }
            }

            if (!leftLines.isEmpty()) {
               leftLines.add("");
            }

            if (!rightLines.isEmpty()) {
               rightLines.add("");
            }

            if (!regularLines.isEmpty()) {
               int mid = (regularLines.size() + 1) / 2;
               leftLines.addAll(regularLines.subList(0, mid));
               rightLines.addAll(regularLines.subList(mid, regularLines.size()));
               leftLines.add("");
               if (mid < regularLines.size()) {
                  rightLines.add("");
               }
            }

            List<Collection<String>> finalGroups = new ArrayList(groups.values());
            if (!finalGroups.isEmpty()) {
               int mid = (finalGroups.size() + 1) / 2;

               for(int i = 0; i < finalGroups.size(); ++i) {
                  Collection<String> lines = (Collection)finalGroups.get(i);
                  if (!lines.isEmpty()) {
                     if (i < mid) {
                        leftLines.addAll(lines);
                        leftLines.add("");
                     } else {
                        rightLines.addAll(lines);
                        rightLines.add("");
                     }
                  }
               }
            }

            if (this.minecraft.debugEntries.isOverlayVisible()) {
               leftLines.add("");
               boolean hasServer = this.minecraft.getSingleplayerServer() != null;
               KeyMapping keyDebugModifier = options.keyDebugModifier;
               String var10001 = formatChart(keyDebugModifier, options.keyDebugPofilingChart, "Profiler", this.renderProfilerChart);
               leftLines.add("Debug charts: " + var10001 + "; " + formatChart(keyDebugModifier, options.keyDebugFpsCharts, hasServer ? "FPS + TPS" : "FPS", this.renderFpsCharts) + ";");
               var10001 = formatChart(keyDebugModifier, options.keyDebugNetworkCharts, !this.minecraft.isLocalServer() ? "Bandwidth + Ping" : "Ping", this.renderNetworkCharts);
               leftLines.add(var10001 + "; " + formatChart(keyDebugModifier, options.keyDebugLightmapTexture, "Lightmap", this.renderLightmapTexture));
               var10001 = formatKeybind(keyDebugModifier, options.keyDebugDebugOptions);
               leftLines.add("To edit: press " + var10001);
            }

            this.renderLines(graphics, leftLines, true);
            this.renderLines(graphics, rightLines, false);
            graphics.nextStratum();
            this.profilerPieChart.setBottomOffset(10);
            if (this.showFpsCharts()) {
               int scaledWidth = graphics.guiWidth();
               int maxWidth = scaledWidth / 2;
               this.fpsChart.drawChart(graphics, 0, this.fpsChart.getWidth(maxWidth));
               if (this.tickTimeLogger.size() > 0) {
                  int width = this.tpsChart.getWidth(maxWidth);
                  this.tpsChart.drawChart(graphics, scaledWidth - width, width);
               }

               this.profilerPieChart.setBottomOffset(this.tpsChart.getFullHeight());
            }

            if (this.showNetworkCharts() && this.minecraft.getConnection() != null) {
               int scaledWidth = graphics.guiWidth();
               int maxWidth = scaledWidth / 2;
               if (!this.minecraft.isLocalServer()) {
                  this.bandwidthChart.drawChart(graphics, 0, this.bandwidthChart.getWidth(maxWidth));
               }

               int width = this.pingChart.getWidth(maxWidth);
               this.pingChart.drawChart(graphics, scaledWidth - width, width);
               this.profilerPieChart.setBottomOffset(this.pingChart.getFullHeight());
            }

            if (this.showLightmapTexture()) {
               GpuTextureView lightmapTextureView = this.minecraft.gameRenderer.levelLightmap();
               int displaySize = 64;
               int x = graphics.guiWidth() - 64 - 2;
               int y = graphics.guiHeight() - 64 - 2;
               graphics.fill(x - 1, y - 1, x + 64 + 1, y + 64 + 1, -16777216);
               graphics.blit(lightmapTextureView, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST), x, y, x + 64, y + 64, 0.0F, 1.0F, 1.0F, 0.0F);
            }

            if (this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_CHUNKS_ON_SERVER)) {
               IntegratedServer singleplayerServer = this.minecraft.getSingleplayerServer();
               if (singleplayerServer != null && this.minecraft.player != null) {
                  ChunkLoadStatusView statusView = singleplayerServer.createChunkLoadStatusView(16 + ChunkLevel.RADIUS_AROUND_FULL_CHUNK);
                  statusView.moveTo(this.minecraft.player.level().dimension(), this.minecraft.player.chunkPosition());
                  LevelLoadingScreen.renderChunks(graphics, graphics.guiWidth() / 2, graphics.guiHeight() / 2, 4, 1, statusView);
               }
            }

            try (Zone ignored = profiler.zone("profilerPie")) {
               this.profilerPieChart.render(graphics);
            }

            profiler.pop();
         }
      }
   }

   private static String formatChart(final KeyMapping keyDebugModifier, final KeyMapping keybind, final String name, final boolean status) {
      return formatKeybind(keyDebugModifier, keybind) + " " + name + " " + (status ? "visible" : "hidden");
   }

   private static String formatKeybind(final KeyMapping keyDebugModifier, final KeyMapping keybind) {
      String var10000 = keyDebugModifier.isUnbound() ? "" : keyDebugModifier.getTranslatedKeyMessage().getString() + "+";
      return "[" + var10000 + keybind.getTranslatedKeyMessage().getString() + "]";
   }

   private void renderLines(final GuiGraphics graphics, final List<String> lines, final boolean alignLeft) {
      Objects.requireNonNull(this.font);
      int height = 9;

      for(int i = 0; i < lines.size(); ++i) {
         String line = (String)lines.get(i);
         if (!Strings.isNullOrEmpty(line)) {
            int width = this.font.width(line);
            int left = alignLeft ? 2 : graphics.guiWidth() - 2 - width;
            int top = 2 + height * i;
            graphics.fill(left - 1, top - 1, left + width + 1, top + height - 1, -1873784752);
         }
      }

      for(int i = 0; i < lines.size(); ++i) {
         String line = (String)lines.get(i);
         if (!Strings.isNullOrEmpty(line)) {
            int width = this.font.width(line);
            int left = alignLeft ? 2 : graphics.guiWidth() - 2 - width;
            int top = 2 + height * i;
            graphics.drawString(this.font, line, left, top, -2039584, false);
         }
      }

   }

   private @Nullable ServerLevel getServerLevel() {
      if (this.minecraft.level == null) {
         return null;
      } else {
         IntegratedServer server = this.minecraft.getSingleplayerServer();
         return server != null ? server.getLevel(this.minecraft.level.dimension()) : null;
      }
   }

   private @Nullable Level getLevel() {
      return this.minecraft.level == null ? null : (Level)DataFixUtils.orElse(Optional.ofNullable(this.minecraft.getSingleplayerServer()).flatMap((s) -> Optional.ofNullable(s.getLevel(this.minecraft.level.dimension()))), this.minecraft.level);
   }

   private @Nullable LevelChunk getServerChunk() {
      if (this.minecraft.level != null && this.lastPos != null) {
         if (this.serverChunk == null) {
            ServerLevel level = this.getServerLevel();
            if (level == null) {
               return null;
            }

            this.serverChunk = level.getChunkSource().getChunkFuture(this.lastPos.x(), this.lastPos.z(), ChunkStatus.FULL, false).thenApply((chunkResult) -> (LevelChunk)chunkResult.orElse((Object)null));
         }

         return (LevelChunk)this.serverChunk.getNow((Object)null);
      } else {
         return null;
      }
   }

   private @Nullable LevelChunk getClientChunk() {
      if (this.minecraft.level != null && this.lastPos != null) {
         if (this.clientChunk == null) {
            this.clientChunk = this.minecraft.level.getChunk(this.lastPos.x(), this.lastPos.z());
         }

         return this.clientChunk;
      } else {
         return null;
      }
   }

   public boolean showDebugScreen() {
      DebugScreenEntryList entries = this.minecraft.debugEntries;
      return (entries.isOverlayVisible() || !entries.getCurrentlyEnabled().isEmpty()) && (!this.minecraft.options.hideGui || this.minecraft.screen != null);
   }

   public boolean showProfilerChart() {
      return this.minecraft.debugEntries.isOverlayVisible() && this.renderProfilerChart;
   }

   public boolean showNetworkCharts() {
      return this.minecraft.debugEntries.isOverlayVisible() && this.renderNetworkCharts;
   }

   public boolean showFpsCharts() {
      return this.minecraft.debugEntries.isOverlayVisible() && this.renderFpsCharts;
   }

   public boolean showLightmapTexture() {
      return this.minecraft.debugEntries.isOverlayVisible() && this.renderLightmapTexture;
   }

   public void toggleNetworkCharts() {
      this.renderNetworkCharts = !this.minecraft.debugEntries.isOverlayVisible() || !this.renderNetworkCharts;
      if (this.renderNetworkCharts) {
         this.minecraft.debugEntries.setOverlayVisible(true);
         this.renderFpsCharts = false;
         this.renderLightmapTexture = false;
      }

   }

   public void toggleFpsCharts() {
      this.renderFpsCharts = !this.minecraft.debugEntries.isOverlayVisible() || !this.renderFpsCharts;
      if (this.renderFpsCharts) {
         this.minecraft.debugEntries.setOverlayVisible(true);
         this.renderNetworkCharts = false;
         this.renderLightmapTexture = false;
      }

   }

   public void toggleLightmapTexture() {
      this.renderLightmapTexture = !this.minecraft.debugEntries.isOverlayVisible() || !this.renderLightmapTexture;
      if (this.renderLightmapTexture) {
         this.minecraft.debugEntries.setOverlayVisible(true);
         this.renderFpsCharts = false;
         this.renderNetworkCharts = false;
      }

   }

   public void toggleProfilerChart() {
      this.renderProfilerChart = !this.minecraft.debugEntries.isOverlayVisible() || !this.renderProfilerChart;
      if (this.renderProfilerChart) {
         this.minecraft.debugEntries.setOverlayVisible(true);
      }

   }

   public void logFrameDuration(final long frameDuration) {
      this.frameTimeLogger.logSample(frameDuration);
   }

   public LocalSampleLogger getTickTimeLogger() {
      return this.tickTimeLogger;
   }

   public LocalSampleLogger getPingLogger() {
      return this.pingLogger;
   }

   public LocalSampleLogger getBandwidthLogger() {
      return this.bandwidthLogger;
   }

   public ProfilerPieChart getProfilerPieChart() {
      return this.profilerPieChart;
   }

   public void logRemoteSample(final long[] sample, final RemoteDebugSampleType type) {
      LocalSampleLogger logger = (LocalSampleLogger)this.remoteSupportingLoggers.get(type);
      if (logger != null) {
         logger.logFullSample(sample);
      }

   }

   public void reset() {
      this.tickTimeLogger.reset();
      this.pingLogger.reset();
      this.bandwidthLogger.reset();
   }

   public void render3dCrosshair(final CameraRenderState cameraState) {
      Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.pushMatrix();
      modelViewStack.translate(0.0F, 0.0F, -1.0F);
      modelViewStack.rotateX(cameraState.xRot * 0.017453292F);
      modelViewStack.rotateY(cameraState.yRot * 0.017453292F);
      float crosshairScale = 0.01F * (float)this.minecraft.getWindow().getGuiScale();
      modelViewStack.scale(-crosshairScale, crosshairScale, -crosshairScale);
      RenderPipeline renderPipelineOutline = RenderPipelines.LINES;
      RenderPipeline renderPipelineFill = RenderPipelines.LINES_DEPTH_BIAS;
      RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
      GpuTextureView colorTexture = mainRenderTarget.getColorTextureView();
      GpuTextureView depthTexture = mainRenderTarget.getDepthTextureView();
      GpuBuffer indexBuffer = this.crosshairIndicies.getBuffer(36);
      GpuBufferSlice dynamicTransform = RenderSystem.getDynamicUniforms().writeTransform(modelViewStack, new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());

      try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "3d crosshair", colorTexture, OptionalInt.empty(), depthTexture, OptionalDouble.empty())) {
         renderPass.setPipeline(renderPipelineOutline);
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.setVertexBuffer(0, this.crosshairBuffer);
         renderPass.setIndexBuffer(indexBuffer, this.crosshairIndicies.type());
         renderPass.setUniform("DynamicTransforms", dynamicTransform);
         renderPass.drawIndexed(0, 0, 18, 1);
         renderPass.setPipeline(renderPipelineFill);
         renderPass.drawIndexed(0, 18, 18, 1);
      }

      modelViewStack.popMatrix();
   }
}
