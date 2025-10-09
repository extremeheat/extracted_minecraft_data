package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
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
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
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
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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

public class DebugScreenOverlay {
   private static final float CROSSHAIR_SCALE = 0.01F;
   private static final int CROSSHAIR_INDEX_COUNT = 36;
   private static final int COLOR_GREY = -2039584;
   private static final int MARGIN_RIGHT = 2;
   private static final int MARGIN_LEFT = 2;
   private static final int MARGIN_TOP = 2;
   private final Minecraft minecraft;
   private final Font font;
   private final GpuBuffer crosshairBuffer;
   private final RenderSystem.AutoStorageIndexBuffer crosshairIndicies;
   @Nullable
   private ChunkPos lastPos;
   @Nullable
   private LevelChunk clientChunk;
   @Nullable
   private CompletableFuture<LevelChunk> serverChunk;
   private boolean renderProfilerChart;
   private boolean renderFpsCharts;
   private boolean renderNetworkCharts;
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

   public DebugScreenOverlay(Minecraft var1) {
      super();
      this.crosshairIndicies = RenderSystem.getSequentialBuffer(VertexFormat.Mode.LINES);
      this.frameTimeLogger = new LocalSampleLogger(1);
      this.tickTimeLogger = new LocalSampleLogger(TpsDebugDimensions.values().length);
      this.pingLogger = new LocalSampleLogger(1);
      this.bandwidthLogger = new LocalSampleLogger(1);
      this.remoteSupportingLoggers = Map.of(RemoteDebugSampleType.TICK_TIME, this.tickTimeLogger);
      this.minecraft = var1;
      this.font = var1.font;
      this.fpsChart = new FpsDebugChart(this.font, this.frameTimeLogger);
      this.tpsChart = new TpsDebugChart(this.font, this.tickTimeLogger, () -> var1.level == null ? 0.0F : var1.level.tickRateManager().millisecondsPerTick());
      this.pingChart = new PingDebugChart(this.font, this.pingLogger);
      this.bandwidthChart = new BandwidthDebugChart(this.font, this.bandwidthLogger);
      this.profilerPieChart = new ProfilerPieChart(this.font);

      try (ByteBufferBuilder var2 = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH.getVertexSize() * 12 * 2)) {
         BufferBuilder var3 = new BufferBuilder(var2, VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
         var3.addVertex(0.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(1.0F, 0.0F, 0.0F).setLineWidth(4.0F);
         var3.addVertex(1.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(1.0F, 0.0F, 0.0F).setLineWidth(4.0F);
         var3.addVertex(0.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(0.0F, 1.0F, 0.0F).setLineWidth(4.0F);
         var3.addVertex(0.0F, 1.0F, 0.0F).setColor(-16777216).setNormal(0.0F, 1.0F, 0.0F).setLineWidth(4.0F);
         var3.addVertex(0.0F, 0.0F, 0.0F).setColor(-16777216).setNormal(0.0F, 0.0F, 1.0F).setLineWidth(4.0F);
         var3.addVertex(0.0F, 0.0F, 1.0F).setColor(-16777216).setNormal(0.0F, 0.0F, 1.0F).setLineWidth(4.0F);
         var3.addVertex(0.0F, 0.0F, 0.0F).setColor(-65536).setNormal(1.0F, 0.0F, 0.0F).setLineWidth(2.0F);
         var3.addVertex(1.0F, 0.0F, 0.0F).setColor(-65536).setNormal(1.0F, 0.0F, 0.0F).setLineWidth(2.0F);
         var3.addVertex(0.0F, 0.0F, 0.0F).setColor(-16711936).setNormal(0.0F, 1.0F, 0.0F).setLineWidth(2.0F);
         var3.addVertex(0.0F, 1.0F, 0.0F).setColor(-16711936).setNormal(0.0F, 1.0F, 0.0F).setLineWidth(2.0F);
         var3.addVertex(0.0F, 0.0F, 0.0F).setColor(-8421377).setNormal(0.0F, 0.0F, 1.0F).setLineWidth(2.0F);
         var3.addVertex(0.0F, 0.0F, 1.0F).setColor(-8421377).setNormal(0.0F, 0.0F, 1.0F).setLineWidth(2.0F);

         try (MeshData var4 = var3.buildOrThrow()) {
            this.crosshairBuffer = RenderSystem.getDevice().createBuffer(() -> "Crosshair vertex buffer", 32, var4.vertexBuffer());
         }
      }

   }

   public void clearChunkCache() {
      this.serverChunk = null;
      this.clientChunk = null;
   }

   public void render(GuiGraphics var1) {
      Options var2 = this.minecraft.options;
      if (this.minecraft.isGameLoadFinished() && (!var2.hideGui || this.minecraft.screen != null)) {
         Collection var3 = this.minecraft.debugEntries.getCurrentlyEnabled();
         if (!var3.isEmpty()) {
            var1.nextStratum();
            ProfilerFiller var4 = Profiler.get();
            var4.push("debug");
            ChunkPos var5;
            if (this.minecraft.getCameraEntity() != null && this.minecraft.level != null) {
               BlockPos var6 = this.minecraft.getCameraEntity().blockPosition();
               var5 = new ChunkPos(var6);
            } else {
               var5 = null;
            }

            if (!Objects.equals(this.lastPos, var5)) {
               this.lastPos = var5;
               this.clearChunkCache();
            }

            final ArrayList var23 = new ArrayList();
            final ArrayList var7 = new ArrayList();
            final LinkedHashMap var8 = new LinkedHashMap();
            final ArrayList var9 = new ArrayList();
            DebugScreenDisplayer var10 = new DebugScreenDisplayer() {
               public void addPriorityLine(String var1) {
                  if (var23.size() > var7.size()) {
                     var7.add(var1);
                  } else {
                     var23.add(var1);
                  }

               }

               public void addLine(String var1) {
                  var9.add(var1);
               }

               public void addToGroup(ResourceLocation var1, Collection<String> var2) {
                  ((Collection)var8.computeIfAbsent(var1, (var0) -> new ArrayList())).addAll(var2);
               }

               public void addToGroup(ResourceLocation var1, String var2) {
                  ((Collection)var8.computeIfAbsent(var1, (var0) -> new ArrayList())).add(var2);
               }
            };
            Level var11 = this.getLevel();

            for(ResourceLocation var13 : var3) {
               DebugScreenEntry var14 = DebugScreenEntries.getEntry(var13);
               if (var14 != null) {
                  var14.display(var10, var11, this.getClientChunk(), this.getServerChunk());
               }
            }

            if (!var23.isEmpty()) {
               var23.add("");
            }

            if (!var7.isEmpty()) {
               var7.add("");
            }

            if (!var9.isEmpty()) {
               int var24 = (var9.size() + 1) / 2;
               var23.addAll(var9.subList(0, var24));
               var7.addAll(var9.subList(var24, var9.size()));
               var23.add("");
               if (var24 < var9.size()) {
                  var7.add("");
               }
            }

            ArrayList var25 = new ArrayList(var8.values());
            if (!var25.isEmpty()) {
               int var26 = (var25.size() + 1) / 2;

               for(int var32 = 0; var32 < var25.size(); ++var32) {
                  Collection var15 = (Collection)var25.get(var32);
                  if (!var15.isEmpty()) {
                     if (var32 < var26) {
                        var23.addAll(var15);
                        var23.add("");
                     } else {
                        var7.addAll(var15);
                        var7.add("");
                     }
                  }
               }
            }

            if (this.minecraft.debugEntries.isOverlayVisible()) {
               var23.add("");
               boolean var27 = this.minecraft.getSingleplayerServer() != null;
               KeyMapping var33 = var2.keyDebugModifier;
               String var37 = var33.getTranslatedKeyMessage().getString();
               String var10000 = var33.isUnbound() ? "" : var37 + "+";
               String var16 = "[" + var10000;
               String var17 = var16 + var2.keyDebugPofilingChart.getTranslatedKeyMessage().getString() + "]";
               String var18 = var16 + var2.keyDebugFpsCharts.getTranslatedKeyMessage().getString() + "]";
               String var19 = var16 + var2.keyDebugNetworkCharts.getTranslatedKeyMessage().getString() + "]";
               var23.add("Debug charts: " + var17 + " Profiler " + (this.renderProfilerChart ? "visible" : "hidden") + "; " + var18 + " " + (var27 ? "FPS + TPS " : "FPS ") + (this.renderFpsCharts ? "visible" : "hidden") + "; " + var19 + " " + (!this.minecraft.isLocalServer() ? "Bandwidth + Ping" : "Ping") + (this.renderNetworkCharts ? " visible" : " hidden"));
               String var20 = var16 + var2.keyDebugDebugOptions.getTranslatedKeyMessage().getString() + "]";
               var23.add("To edit: press " + var20);
            }

            this.renderLines(var1, var23, true);
            this.renderLines(var1, var7, false);
            var1.nextStratum();
            this.profilerPieChart.setBottomOffset(10);
            if (this.showFpsCharts()) {
               int var28 = var1.guiWidth();
               int var34 = var28 / 2;
               this.fpsChart.drawChart(var1, 0, this.fpsChart.getWidth(var34));
               if (this.tickTimeLogger.size() > 0) {
                  int var38 = this.tpsChart.getWidth(var34);
                  this.tpsChart.drawChart(var1, var28 - var38, var38);
               }

               this.profilerPieChart.setBottomOffset(this.tpsChart.getFullHeight());
            }

            if (this.showNetworkCharts() && this.minecraft.getConnection() != null) {
               int var29 = var1.guiWidth();
               int var35 = var29 / 2;
               if (!this.minecraft.isLocalServer()) {
                  this.bandwidthChart.drawChart(var1, 0, this.bandwidthChart.getWidth(var35));
               }

               int var39 = this.pingChart.getWidth(var35);
               this.pingChart.drawChart(var1, var29 - var39, var39);
               this.profilerPieChart.setBottomOffset(this.pingChart.getFullHeight());
            }

            if (this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.VISUALIZE_CHUNKS_ON_SERVER)) {
               IntegratedServer var30 = this.minecraft.getSingleplayerServer();
               if (var30 != null && this.minecraft.player != null) {
                  ChunkLoadStatusView var36 = var30.createChunkLoadStatusView(16 + ChunkLevel.RADIUS_AROUND_FULL_CHUNK);
                  var36.moveTo(this.minecraft.player.level().dimension(), this.minecraft.player.chunkPosition());
                  LevelLoadingScreen.renderChunks(var1, var1.guiWidth() / 2, var1.guiHeight() / 2, 4, 1, var36);
               }
            }

            try (Zone var31 = var4.zone("profilerPie")) {
               this.profilerPieChart.render(var1);
            }

            var4.pop();
         }
      }
   }

   private void renderLines(GuiGraphics var1, List<String> var2, boolean var3) {
      Objects.requireNonNull(this.font);
      byte var4 = 9;

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         String var6 = (String)var2.get(var5);
         if (!Strings.isNullOrEmpty(var6)) {
            int var7 = this.font.width(var6);
            int var8 = var3 ? 2 : var1.guiWidth() - 2 - var7;
            int var9 = 2 + var4 * var5;
            var1.fill(var8 - 1, var9 - 1, var8 + var7 + 1, var9 + var4 - 1, -1873784752);
         }
      }

      for(int var10 = 0; var10 < var2.size(); ++var10) {
         String var11 = (String)var2.get(var10);
         if (!Strings.isNullOrEmpty(var11)) {
            int var12 = this.font.width(var11);
            int var13 = var3 ? 2 : var1.guiWidth() - 2 - var12;
            int var14 = 2 + var4 * var10;
            var1.drawString(this.font, var11, var13, var14, -2039584, false);
         }
      }

   }

   @Nullable
   private ServerLevel getServerLevel() {
      if (this.minecraft.level == null) {
         return null;
      } else {
         IntegratedServer var1 = this.minecraft.getSingleplayerServer();
         return var1 != null ? var1.getLevel(this.minecraft.level.dimension()) : null;
      }
   }

   @Nullable
   private Level getLevel() {
      return this.minecraft.level == null ? null : (Level)DataFixUtils.orElse(Optional.ofNullable(this.minecraft.getSingleplayerServer()).flatMap((var1) -> Optional.ofNullable(var1.getLevel(this.minecraft.level.dimension()))), this.minecraft.level);
   }

   @Nullable
   private LevelChunk getServerChunk() {
      if (this.minecraft.level != null && this.lastPos != null) {
         if (this.serverChunk == null) {
            ServerLevel var1 = this.getServerLevel();
            if (var1 == null) {
               return null;
            }

            this.serverChunk = var1.getChunkSource().getChunkFuture(this.lastPos.x, this.lastPos.z, ChunkStatus.FULL, false).thenApply((var0) -> (LevelChunk)var0.orElse((Object)null));
         }

         return (LevelChunk)this.serverChunk.getNow((Object)null);
      } else {
         return null;
      }
   }

   @Nullable
   private LevelChunk getClientChunk() {
      if (this.minecraft.level != null && this.lastPos != null) {
         if (this.clientChunk == null) {
            this.clientChunk = this.minecraft.level.getChunk(this.lastPos.x, this.lastPos.z);
         }

         return this.clientChunk;
      } else {
         return null;
      }
   }

   public boolean showDebugScreen() {
      DebugScreenEntryList var1 = this.minecraft.debugEntries;
      return (var1.isOverlayVisible() || !var1.getCurrentlyEnabled().isEmpty()) && (!this.minecraft.options.hideGui || this.minecraft.screen != null);
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

   public void toggleNetworkCharts() {
      this.renderNetworkCharts = !this.minecraft.debugEntries.isOverlayVisible() || !this.renderNetworkCharts;
      if (this.renderNetworkCharts) {
         this.minecraft.debugEntries.setOverlayVisible(true);
         this.renderFpsCharts = false;
      }

   }

   public void toggleFpsCharts() {
      this.renderFpsCharts = !this.minecraft.debugEntries.isOverlayVisible() || !this.renderFpsCharts;
      if (this.renderFpsCharts) {
         this.minecraft.debugEntries.setOverlayVisible(true);
         this.renderNetworkCharts = false;
      }

   }

   public void toggleProfilerChart() {
      this.renderProfilerChart = !this.minecraft.debugEntries.isOverlayVisible() || !this.renderProfilerChart;
      if (this.renderProfilerChart) {
         this.minecraft.debugEntries.setOverlayVisible(true);
      }

   }

   public void logFrameDuration(long var1) {
      this.frameTimeLogger.logSample(var1);
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

   public void logRemoteSample(long[] var1, RemoteDebugSampleType var2) {
      LocalSampleLogger var3 = (LocalSampleLogger)this.remoteSupportingLoggers.get(var2);
      if (var3 != null) {
         var3.logFullSample(var1);
      }

   }

   public void reset() {
      this.tickTimeLogger.reset();
      this.pingLogger.reset();
      this.bandwidthLogger.reset();
   }

   public void render3dCrosshair(Camera var1) {
      Matrix4fStack var2 = RenderSystem.getModelViewStack();
      var2.pushMatrix();
      var2.translate(0.0F, 0.0F, -1.0F);
      var2.rotateX(var1.xRot() * 0.017453292F);
      var2.rotateY(var1.yRot() * 0.017453292F);
      float var3 = 0.01F * (float)this.minecraft.getWindow().getGuiScale();
      var2.scale(-var3, var3, -var3);
      RenderPipeline var4 = RenderPipelines.LINES;
      RenderTarget var5 = Minecraft.getInstance().getMainRenderTarget();
      GpuTextureView var6 = var5.getColorTextureView();
      GpuTextureView var7 = var5.getDepthTextureView();
      GpuBuffer var8 = this.crosshairIndicies.getBuffer(36);
      GpuBufferSlice var9 = RenderSystem.getDynamicUniforms().writeTransform(var2, new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());

      try (RenderPass var10 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "3d crosshair", var6, OptionalInt.empty(), var7, OptionalDouble.empty())) {
         var10.setPipeline(var4);
         RenderSystem.bindDefaultUniforms(var10);
         var10.setVertexBuffer(0, this.crosshairBuffer);
         var10.setIndexBuffer(var8, this.crosshairIndicies.type());
         var10.setUniform("DynamicTransforms", var9);
         var10.drawIndexed(0, 0, 36, 1);
      }

      var2.popMatrix();
   }
}
