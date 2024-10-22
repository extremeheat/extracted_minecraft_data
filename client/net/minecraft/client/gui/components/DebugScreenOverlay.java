package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debugchart.BandwidthDebugChart;
import net.minecraft.client.gui.components.debugchart.FpsDebugChart;
import net.minecraft.client.gui.components.debugchart.PingDebugChart;
import net.minecraft.client.gui.components.debugchart.ProfilerPieChart;
import net.minecraft.client.gui.components.debugchart.TpsDebugChart;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraft.util.debugchart.TpsDebugDimensions;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class DebugScreenOverlay {
   private static final int COLOR_GREY = 14737632;
   private static final int MARGIN_RIGHT = 2;
   private static final int MARGIN_LEFT = 2;
   private static final int MARGIN_TOP = 2;
   private static final Map<Heightmap.Types, String> HEIGHTMAP_NAMES = Util.make(new EnumMap<>(Heightmap.Types.class), var0 -> {
      var0.put(Heightmap.Types.WORLD_SURFACE_WG, "SW");
      var0.put(Heightmap.Types.WORLD_SURFACE, "S");
      var0.put(Heightmap.Types.OCEAN_FLOOR_WG, "OW");
      var0.put(Heightmap.Types.OCEAN_FLOOR, "O");
      var0.put(Heightmap.Types.MOTION_BLOCKING, "M");
      var0.put(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, "ML");
   });
   private final Minecraft minecraft;
   private final DebugScreenOverlay.AllocationRateCalculator allocationRateCalculator;
   private final Font font;
   private HitResult block;
   private HitResult liquid;
   @Nullable
   private ChunkPos lastPos;
   @Nullable
   private LevelChunk clientChunk;
   @Nullable
   private CompletableFuture<LevelChunk> serverChunk;
   private boolean renderDebug;
   private boolean renderProfilerChart;
   private boolean renderFpsCharts;
   private boolean renderNetworkCharts;
   private final LocalSampleLogger frameTimeLogger = new LocalSampleLogger(1);
   private final LocalSampleLogger tickTimeLogger = new LocalSampleLogger(TpsDebugDimensions.values().length);
   private final LocalSampleLogger pingLogger = new LocalSampleLogger(1);
   private final LocalSampleLogger bandwidthLogger = new LocalSampleLogger(1);
   private final Map<RemoteDebugSampleType, LocalSampleLogger> remoteSupportingLoggers = Map.of(RemoteDebugSampleType.TICK_TIME, this.tickTimeLogger);
   private final FpsDebugChart fpsChart;
   private final TpsDebugChart tpsChart;
   private final PingDebugChart pingChart;
   private final BandwidthDebugChart bandwidthChart;
   private final ProfilerPieChart profilerPieChart;

   public DebugScreenOverlay(Minecraft var1) {
      super();
      this.minecraft = var1;
      this.allocationRateCalculator = new DebugScreenOverlay.AllocationRateCalculator();
      this.font = var1.font;
      this.fpsChart = new FpsDebugChart(this.font, this.frameTimeLogger);
      this.tpsChart = new TpsDebugChart(this.font, this.tickTimeLogger, () -> var1.level.tickRateManager().millisecondsPerTick());
      this.pingChart = new PingDebugChart(this.font, this.pingLogger);
      this.bandwidthChart = new BandwidthDebugChart(this.font, this.bandwidthLogger);
      this.profilerPieChart = new ProfilerPieChart(this.font);
   }

   public void clearChunkCache() {
      this.serverChunk = null;
      this.clientChunk = null;
   }

   public void render(GuiGraphics var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("debug");
      Entity var3 = this.minecraft.getCameraEntity();
      this.block = var3.pick(20.0, 0.0F, false);
      this.liquid = var3.pick(20.0, 0.0F, true);
      this.drawGameInformation(var1);
      this.drawSystemInformation(var1);
      this.profilerPieChart.setBottomOffset(10);
      if (this.renderFpsCharts) {
         int var4 = var1.guiWidth();
         int var5 = var4 / 2;
         this.fpsChart.drawChart(var1, 0, this.fpsChart.getWidth(var5));
         if (this.tickTimeLogger.size() > 0) {
            int var6 = this.tpsChart.getWidth(var5);
            this.tpsChart.drawChart(var1, var4 - var6, var6);
         }

         this.profilerPieChart.setBottomOffset(this.tpsChart.getFullHeight());
      }

      if (this.renderNetworkCharts) {
         int var9 = var1.guiWidth();
         int var11 = var9 / 2;
         if (!this.minecraft.isLocalServer()) {
            this.bandwidthChart.drawChart(var1, 0, this.bandwidthChart.getWidth(var11));
         }

         int var12 = this.pingChart.getWidth(var11);
         this.pingChart.drawChart(var1, var9 - var12, var12);
         this.profilerPieChart.setBottomOffset(this.pingChart.getFullHeight());
      }

      try (Zone var10 = var2.zone("profilerPie")) {
         this.profilerPieChart.render(var1);
      }

      var2.pop();
   }

   protected void drawGameInformation(GuiGraphics var1) {
      List var2 = this.getGameInformation();
      var2.add("");
      boolean var3 = this.minecraft.getSingleplayerServer() != null;
      var2.add(
         "Debug charts: [F3+1] Profiler "
            + (this.renderProfilerChart ? "visible" : "hidden")
            + "; [F3+2] "
            + (var3 ? "FPS + TPS " : "FPS ")
            + (this.renderFpsCharts ? "visible" : "hidden")
            + "; [F3+3] "
            + (!this.minecraft.isLocalServer() ? "Bandwidth + Ping" : "Ping")
            + (this.renderNetworkCharts ? " visible" : " hidden")
      );
      var2.add("For help: press F3 + Q");
      this.renderLines(var1, var2, true);
   }

   protected void drawSystemInformation(GuiGraphics var1) {
      List var2 = this.getSystemInformation();
      this.renderLines(var1, var2, false);
   }

   private void renderLines(GuiGraphics var1, List<String> var2, boolean var3) {
      byte var4 = 9;

      for (int var5 = 0; var5 < var2.size(); var5++) {
         String var6 = (String)var2.get(var5);
         if (!Strings.isNullOrEmpty(var6)) {
            int var7 = this.font.width(var6);
            int var8 = var3 ? 2 : var1.guiWidth() - 2 - var7;
            int var9 = 2 + var4 * var5;
            var1.fill(var8 - 1, var9 - 1, var8 + var7 + 1, var9 + var4 - 1, -1873784752);
         }
      }

      for (int var10 = 0; var10 < var2.size(); var10++) {
         String var11 = (String)var2.get(var10);
         if (!Strings.isNullOrEmpty(var11)) {
            int var12 = this.font.width(var11);
            int var13 = var3 ? 2 : var1.guiWidth() - 2 - var12;
            int var14 = 2 + var4 * var10;
            var1.drawString(this.font, var11, var13, var14, 14737632, false);
         }
      }
   }

   protected List<String> getGameInformation() {
      IntegratedServer var2 = this.minecraft.getSingleplayerServer();
      ClientPacketListener var3 = this.minecraft.getConnection();
      Connection var4 = var3.getConnection();
      float var5 = var4.getAverageSentPackets();
      float var6 = var4.getAverageReceivedPackets();
      TickRateManager var8 = this.getLevel().tickRateManager();
      String var7;
      if (var8.isSteppingForward()) {
         var7 = " (frozen - stepping)";
      } else if (var8.isFrozen()) {
         var7 = " (frozen)";
      } else {
         var7 = "";
      }

      String var1;
      if (var2 != null) {
         ServerTickRateManager var9 = var2.tickRateManager();
         boolean var10 = var9.isSprinting();
         if (var10) {
            var7 = " (sprinting)";
         }

         String var11 = var10 ? "-" : String.format(Locale.ROOT, "%.1f", var8.millisecondsPerTick());
         var1 = String.format(Locale.ROOT, "Integrated server @ %.1f/%s ms%s, %.0f tx, %.0f rx", var2.getCurrentSmoothedTickTime(), var11, var7, var5, var6);
      } else {
         var1 = String.format(Locale.ROOT, "\"%s\" server%s, %.0f tx, %.0f rx", var3.serverBrand(), var7, var5, var6);
      }

      BlockPos var28 = this.minecraft.getCameraEntity().blockPosition();
      if (this.minecraft.showOnlyReducedInfo()) {
         return Lists.newArrayList(
            new String[]{
               "Minecraft "
                  + SharedConstants.getCurrentVersion().getName()
                  + " ("
                  + this.minecraft.getLaunchedVersion()
                  + "/"
                  + ClientBrandRetriever.getClientModName()
                  + ")",
               this.minecraft.fpsString,
               var1,
               this.minecraft.levelRenderer.getSectionStatistics(),
               this.minecraft.levelRenderer.getEntityStatistics(),
               "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(),
               this.minecraft.level.gatherChunkSourceStats(),
               "",
               String.format(Locale.ROOT, "Chunk-relative: %d %d %d", var28.getX() & 15, var28.getY() & 15, var28.getZ() & 15)
            }
         );
      } else {
         Entity var29 = this.minecraft.getCameraEntity();
         Direction var30 = var29.getDirection();

         String var12 = switch (var30) {
            case NORTH -> "Towards negative Z";
            case SOUTH -> "Towards positive Z";
            case WEST -> "Towards negative X";
            case EAST -> "Towards positive X";
            default -> "Invalid";
         };
         ChunkPos var13 = new ChunkPos(var28);
         if (!Objects.equals(this.lastPos, var13)) {
            this.lastPos = var13;
            this.clearChunkCache();
         }

         Level var14 = this.getLevel();
         Object var15 = var14 instanceof ServerLevel ? ((ServerLevel)var14).getForcedChunks() : LongSets.EMPTY_SET;
         ArrayList var16 = Lists.newArrayList(
            new String[]{
               "Minecraft "
                  + SharedConstants.getCurrentVersion().getName()
                  + " ("
                  + this.minecraft.getLaunchedVersion()
                  + "/"
                  + ClientBrandRetriever.getClientModName()
                  + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType())
                  + ")",
               this.minecraft.fpsString,
               var1,
               this.minecraft.levelRenderer.getSectionStatistics(),
               this.minecraft.levelRenderer.getEntityStatistics(),
               "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(),
               this.minecraft.level.gatherChunkSourceStats()
            }
         );
         String var17 = this.getServerChunkStats();
         if (var17 != null) {
            var16.add(var17);
         }

         var16.add(this.minecraft.level.dimension().location() + " FC: " + var15.size());
         var16.add("");
         var16.add(
            String.format(
               Locale.ROOT,
               "XYZ: %.3f / %.5f / %.3f",
               this.minecraft.getCameraEntity().getX(),
               this.minecraft.getCameraEntity().getY(),
               this.minecraft.getCameraEntity().getZ()
            )
         );
         var16.add(
            String.format(
               Locale.ROOT, "Block: %d %d %d [%d %d %d]", var28.getX(), var28.getY(), var28.getZ(), var28.getX() & 15, var28.getY() & 15, var28.getZ() & 15
            )
         );
         var16.add(
            String.format(
               Locale.ROOT,
               "Chunk: %d %d %d [%d %d in r.%d.%d.mca]",
               var13.x,
               SectionPos.blockToSectionCoord(var28.getY()),
               var13.z,
               var13.getRegionLocalX(),
               var13.getRegionLocalZ(),
               var13.getRegionX(),
               var13.getRegionZ()
            )
         );
         var16.add(
            String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", var30, var12, Mth.wrapDegrees(var29.getYRot()), Mth.wrapDegrees(var29.getXRot()))
         );
         LevelChunk var18 = this.getClientChunk();
         if (var18.isEmpty()) {
            var16.add("Waiting for chunk...");
         } else {
            int var19 = this.minecraft.level.getChunkSource().getLightEngine().getRawBrightness(var28, 0);
            int var20 = this.minecraft.level.getBrightness(LightLayer.SKY, var28);
            int var21 = this.minecraft.level.getBrightness(LightLayer.BLOCK, var28);
            var16.add("Client Light: " + var19 + " (" + var20 + " sky, " + var21 + " block)");
            LevelChunk var22 = this.getServerChunk();
            StringBuilder var23 = new StringBuilder("CH");

            for (Heightmap.Types var27 : Heightmap.Types.values()) {
               if (var27.sendToClient()) {
                  var23.append(" ").append(HEIGHTMAP_NAMES.get(var27)).append(": ").append(var18.getHeight(var27, var28.getX(), var28.getZ()));
               }
            }

            var16.add(var23.toString());
            var23.setLength(0);
            var23.append("SH");

            for (Heightmap.Types var45 : Heightmap.Types.values()) {
               if (var45.keepAfterWorldgen()) {
                  var23.append(" ").append(HEIGHTMAP_NAMES.get(var45)).append(": ");
                  if (var22 != null) {
                     var23.append(var22.getHeight(var45, var28.getX(), var28.getZ()));
                  } else {
                     var23.append("??");
                  }
               }
            }

            var16.add(var23.toString());
            if (this.minecraft.level.isInsideBuildHeight(var28.getY())) {
               var16.add("Biome: " + printBiome(this.minecraft.level.getBiome(var28)));
               if (var22 != null) {
                  float var38 = var14.getMoonBrightness();
                  long var41 = var22.getInhabitedTime();
                  DifficultyInstance var46 = new DifficultyInstance(var14.getDifficulty(), var14.getDayTime(), var41, var38);
                  var16.add(
                     String.format(
                        Locale.ROOT,
                        "Local Difficulty: %.2f // %.2f (Day %d)",
                        var46.getEffectiveDifficulty(),
                        var46.getSpecialMultiplier(),
                        this.minecraft.level.getDayTime() / 24000L
                     )
                  );
               } else {
                  var16.add("Local Difficulty: ??");
               }
            }

            if (var22 != null && var22.isOldNoiseGeneration()) {
               var16.add("Blending: Old");
            }
         }

         ServerLevel var31 = this.getServerLevel();
         if (var31 != null) {
            ServerChunkCache var32 = var31.getChunkSource();
            ChunkGenerator var34 = var32.getGenerator();
            RandomState var35 = var32.randomState();
            var34.addDebugScreenInfo(var16, var35, var28);
            Climate.Sampler var36 = var35.sampler();
            BiomeSource var39 = var34.getBiomeSource();
            var39.addDebugInfo(var16, var28, var36);
            NaturalSpawner.SpawnState var42 = var32.getLastSpawnState();
            if (var42 != null) {
               Object2IntMap var44 = var42.getMobCategoryCounts();
               int var47 = var42.getSpawnableChunkCount();
               var16.add(
                  "SC: "
                     + var47
                     + ", "
                     + Stream.of(MobCategory.values())
                        .map(var1x -> Character.toUpperCase(var1x.getName().charAt(0)) + ": " + var44.getInt(var1x))
                        .collect(Collectors.joining(", "))
               );
            } else {
               var16.add("SC: N/A");
            }
         }

         ResourceLocation var33 = this.minecraft.gameRenderer.currentPostEffect();
         if (var33 != null) {
            var16.add("Post: " + var33);
         }

         var16.add(
            this.minecraft.getSoundManager().getDebugString()
               + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(this.minecraft.player.getCurrentMood() * 100.0F))
         );
         return var16;
      }
   }

   private static String printBiome(Holder<Biome> var0) {
      return (String)var0.unwrap().map(var0x -> var0x.location().toString(), var0x -> "[unregistered " + var0x + "]");
   }

   @Nullable
   private ServerLevel getServerLevel() {
      IntegratedServer var1 = this.minecraft.getSingleplayerServer();
      return var1 != null ? var1.getLevel(this.minecraft.level.dimension()) : null;
   }

   @Nullable
   private String getServerChunkStats() {
      ServerLevel var1 = this.getServerLevel();
      return var1 != null ? var1.gatherChunkSourceStats() : null;
   }

   private Level getLevel() {
      return (Level)DataFixUtils.orElse(
         Optional.ofNullable(this.minecraft.getSingleplayerServer()).flatMap(var1 -> Optional.ofNullable(var1.getLevel(this.minecraft.level.dimension()))),
         this.minecraft.level
      );
   }

   @Nullable
   private LevelChunk getServerChunk() {
      if (this.serverChunk == null) {
         ServerLevel var1 = this.getServerLevel();
         if (var1 == null) {
            return null;
         }

         this.serverChunk = var1.getChunkSource()
            .getChunkFuture(this.lastPos.x, this.lastPos.z, ChunkStatus.FULL, false)
            .thenApply(var0 -> (LevelChunk)var0.orElse(null));
      }

      return this.serverChunk.getNow(null);
   }

   private LevelChunk getClientChunk() {
      if (this.clientChunk == null) {
         this.clientChunk = this.minecraft.level.getChunk(this.lastPos.x, this.lastPos.z);
      }

      return this.clientChunk;
   }

   protected List<String> getSystemInformation() {
      long var1 = Runtime.getRuntime().maxMemory();
      long var3 = Runtime.getRuntime().totalMemory();
      long var5 = Runtime.getRuntime().freeMemory();
      long var7 = var3 - var5;
      ArrayList var9 = Lists.newArrayList(
         new String[]{
            String.format(Locale.ROOT, "Java: %s", System.getProperty("java.version")),
            String.format(Locale.ROOT, "Mem: %2d%% %03d/%03dMB", var7 * 100L / var1, bytesToMegabytes(var7), bytesToMegabytes(var1)),
            String.format(Locale.ROOT, "Allocation rate: %03dMB/s", bytesToMegabytes(this.allocationRateCalculator.bytesAllocatedPerSecond(var7))),
            String.format(Locale.ROOT, "Allocated: %2d%% %03dMB", var3 * 100L / var1, bytesToMegabytes(var3)),
            "",
            String.format(Locale.ROOT, "CPU: %s", GlUtil.getCpuInfo()),
            "",
            String.format(
               Locale.ROOT,
               "Display: %dx%d (%s)",
               Minecraft.getInstance().getWindow().getWidth(),
               Minecraft.getInstance().getWindow().getHeight(),
               GlUtil.getVendor()
            ),
            GlUtil.getRenderer(),
            GlUtil.getOpenGLVersion()
         }
      );
      if (this.minecraft.showOnlyReducedInfo()) {
         return var9;
      } else {
         if (this.block.getType() == HitResult.Type.BLOCK) {
            BlockPos var10 = ((BlockHitResult)this.block).getBlockPos();
            BlockState var11 = this.minecraft.level.getBlockState(var10);
            var9.add("");
            var9.add(ChatFormatting.UNDERLINE + "Targeted Block: " + var10.getX() + ", " + var10.getY() + ", " + var10.getZ());
            var9.add(String.valueOf(BuiltInRegistries.BLOCK.getKey(var11.getBlock())));

            for (Entry var13 : var11.getValues().entrySet()) {
               var9.add(this.getPropertyValueString(var13));
            }

            var11.getTags().map(var0 -> "#" + var0.location()).forEach(var9::add);
         }

         if (this.liquid.getType() == HitResult.Type.BLOCK) {
            BlockPos var14 = ((BlockHitResult)this.liquid).getBlockPos();
            FluidState var16 = this.minecraft.level.getFluidState(var14);
            var9.add("");
            var9.add(ChatFormatting.UNDERLINE + "Targeted Fluid: " + var14.getX() + ", " + var14.getY() + ", " + var14.getZ());
            var9.add(String.valueOf(BuiltInRegistries.FLUID.getKey(var16.getType())));

            for (Entry var18 : var16.getValues().entrySet()) {
               var9.add(this.getPropertyValueString(var18));
            }

            var16.getTags().map(var0 -> "#" + var0.location()).forEach(var9::add);
         }

         Entity var15 = this.minecraft.crosshairPickEntity;
         if (var15 != null) {
            var9.add("");
            var9.add(ChatFormatting.UNDERLINE + "Targeted Entity");
            var9.add(String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(var15.getType())));
         }

         return var9;
      }
   }

   private String getPropertyValueString(Entry<Property<?>, Comparable<?>> var1) {
      Property var2 = (Property)var1.getKey();
      Comparable var3 = (Comparable)var1.getValue();
      Object var4 = Util.getPropertyName(var2, var3);
      if (Boolean.TRUE.equals(var3)) {
         var4 = ChatFormatting.GREEN + var4;
      } else if (Boolean.FALSE.equals(var3)) {
         var4 = ChatFormatting.RED + var4;
      }

      return var2.getName() + ": " + var4;
   }

   private static long bytesToMegabytes(long var0) {
      return var0 / 1024L / 1024L;
   }

   public boolean showDebugScreen() {
      return this.renderDebug && !this.minecraft.options.hideGui;
   }

   public boolean showProfilerChart() {
      return this.showDebugScreen() && this.renderProfilerChart;
   }

   public boolean showNetworkCharts() {
      return this.showDebugScreen() && this.renderNetworkCharts;
   }

   public boolean showFpsCharts() {
      return this.showDebugScreen() && this.renderFpsCharts;
   }

   public void toggleOverlay() {
      this.renderDebug = !this.renderDebug;
   }

   public void toggleNetworkCharts() {
      this.renderNetworkCharts = !this.renderDebug || !this.renderNetworkCharts;
      if (this.renderNetworkCharts) {
         this.renderDebug = true;
         this.renderFpsCharts = false;
      }
   }

   public void toggleFpsCharts() {
      this.renderFpsCharts = !this.renderDebug || !this.renderFpsCharts;
      if (this.renderFpsCharts) {
         this.renderDebug = true;
         this.renderNetworkCharts = false;
      }
   }

   public void toggleProfilerChart() {
      this.renderProfilerChart = !this.renderDebug || !this.renderProfilerChart;
      if (this.renderProfilerChart) {
         this.renderDebug = true;
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
      LocalSampleLogger var3 = this.remoteSupportingLoggers.get(var2);
      if (var3 != null) {
         var3.logFullSample(var1);
      }
   }

   public void reset() {
      this.renderDebug = false;
      this.tickTimeLogger.reset();
      this.pingLogger.reset();
      this.bandwidthLogger.reset();
   }

   static class AllocationRateCalculator {
      private static final int UPDATE_INTERVAL_MS = 500;
      private static final List<GarbageCollectorMXBean> GC_MBEANS = ManagementFactory.getGarbageCollectorMXBeans();
      private long lastTime = 0L;
      private long lastHeapUsage = -1L;
      private long lastGcCounts = -1L;
      private long lastRate = 0L;

      AllocationRateCalculator() {
         super();
      }

      long bytesAllocatedPerSecond(long var1) {
         long var3 = System.currentTimeMillis();
         if (var3 - this.lastTime < 500L) {
            return this.lastRate;
         } else {
            long var5 = gcCounts();
            if (this.lastTime != 0L && var5 == this.lastGcCounts) {
               double var7 = (double)TimeUnit.SECONDS.toMillis(1L) / (double)(var3 - this.lastTime);
               long var9 = var1 - this.lastHeapUsage;
               this.lastRate = Math.round((double)var9 * var7);
            }

            this.lastTime = var3;
            this.lastHeapUsage = var1;
            this.lastGcCounts = var5;
            return this.lastRate;
         }
      }

      private static long gcCounts() {
         long var0 = 0L;

         for (GarbageCollectorMXBean var3 : GC_MBEANS) {
            var0 += var3.getCollectionCount();
         }

         return var0;
      }
   }
}
