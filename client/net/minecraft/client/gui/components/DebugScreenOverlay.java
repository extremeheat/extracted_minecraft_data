package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
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
import net.minecraft.client.gui.components.debugchart.TpsDebugChart;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.SampleLogger;
import net.minecraft.world.DifficultyInstance;
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
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
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
   private final SampleLogger frameTimeLogger = new SampleLogger();
   private final SampleLogger tickTimeLogger = new SampleLogger();
   private final SampleLogger pingLogger = new SampleLogger();
   private final SampleLogger bandwidthLogger = new SampleLogger();
   private final FpsDebugChart fpsChart;
   private final TpsDebugChart tpsChart;
   private final PingDebugChart pingChart;
   private final BandwidthDebugChart bandwidthChart;

   public DebugScreenOverlay(Minecraft var1) {
      super();
      this.minecraft = var1;
      this.allocationRateCalculator = new DebugScreenOverlay.AllocationRateCalculator();
      this.font = var1.font;
      this.fpsChart = new FpsDebugChart(this.font, this.frameTimeLogger);
      this.tpsChart = new TpsDebugChart(this.font, this.tickTimeLogger);
      this.pingChart = new PingDebugChart(this.font, this.pingLogger);
      this.bandwidthChart = new BandwidthDebugChart(this.font, this.bandwidthLogger);
   }

   public void clearChunkCache() {
      this.serverChunk = null;
      this.clientChunk = null;
   }

   public void render(GuiGraphics var1) {
      this.minecraft.getProfiler().push("debug");
      Entity var2 = this.minecraft.getCameraEntity();
      this.block = var2.pick(20.0, 0.0F, false);
      this.liquid = var2.pick(20.0, 0.0F, true);
      var1.drawManaged(() -> {
         this.drawGameInformation(var1);
         this.drawSystemInformation(var1);
         if (this.renderFpsCharts) {
            int var2x = var1.guiWidth();
            int var3 = var2x / 2;
            this.fpsChart.drawChart(var1, 0, this.fpsChart.getWidth(var3));
            if (this.minecraft.getSingleplayerServer() != null) {
               int var4 = this.tpsChart.getWidth(var3);
               this.tpsChart.drawChart(var1, var2x - var4, var4);
            }
         }

         if (this.renderNetworkCharts) {
            int var5 = var1.guiWidth();
            int var6 = var5 / 2;
            if (!this.minecraft.isLocalServer()) {
               this.bandwidthChart.drawChart(var1, 0, this.bandwidthChart.getWidth(var6));
            }

            int var7 = this.pingChart.getWidth(var6);
            this.pingChart.drawChart(var1, var5 - var7, var7);
         }
      });
      this.minecraft.getProfiler().pop();
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
      String var1;
      if (var2 != null) {
         var1 = String.format(Locale.ROOT, "Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", var2.getAverageTickTime(), var5, var6);
      } else {
         var1 = String.format(Locale.ROOT, "\"%s\" server, %.0f tx, %.0f rx", var3.serverBrand(), var5, var6);
      }

      BlockPos var7 = this.minecraft.getCameraEntity().blockPosition();
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
               String.format(Locale.ROOT, "Chunk-relative: %d %d %d", var7.getX() & 15, var7.getY() & 15, var7.getZ() & 15)
            }
         );
      } else {
         Entity var8 = this.minecraft.getCameraEntity();
         Direction var9 = var8.getDirection();

         String var10 = switch(var9) {
            case NORTH -> "Towards negative Z";
            case SOUTH -> "Towards positive Z";
            case WEST -> "Towards negative X";
            case EAST -> "Towards positive X";
            default -> "Invalid";
         };
         ChunkPos var11 = new ChunkPos(var7);
         if (!Objects.equals(this.lastPos, var11)) {
            this.lastPos = var11;
            this.clearChunkCache();
         }

         Level var12 = this.getLevel();
         Object var13 = var12 instanceof ServerLevel ? ((ServerLevel)var12).getForcedChunks() : LongSets.EMPTY_SET;
         ArrayList var14 = Lists.newArrayList(
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
         String var15 = this.getServerChunkStats();
         if (var15 != null) {
            var14.add(var15);
         }

         var14.add(this.minecraft.level.dimension().location() + " FC: " + var13.size());
         var14.add("");
         var14.add(
            String.format(
               Locale.ROOT,
               "XYZ: %.3f / %.5f / %.3f",
               this.minecraft.getCameraEntity().getX(),
               this.minecraft.getCameraEntity().getY(),
               this.minecraft.getCameraEntity().getZ()
            )
         );
         var14.add(
            String.format(
               Locale.ROOT, "Block: %d %d %d [%d %d %d]", var7.getX(), var7.getY(), var7.getZ(), var7.getX() & 15, var7.getY() & 15, var7.getZ() & 15
            )
         );
         var14.add(
            String.format(
               Locale.ROOT,
               "Chunk: %d %d %d [%d %d in r.%d.%d.mca]",
               var11.x,
               SectionPos.blockToSectionCoord(var7.getY()),
               var11.z,
               var11.getRegionLocalX(),
               var11.getRegionLocalZ(),
               var11.getRegionX(),
               var11.getRegionZ()
            )
         );
         var14.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", var9, var10, Mth.wrapDegrees(var8.getYRot()), Mth.wrapDegrees(var8.getXRot())));
         LevelChunk var16 = this.getClientChunk();
         if (var16.isEmpty()) {
            var14.add("Waiting for chunk...");
         } else {
            int var17 = this.minecraft.level.getChunkSource().getLightEngine().getRawBrightness(var7, 0);
            int var18 = this.minecraft.level.getBrightness(LightLayer.SKY, var7);
            int var19 = this.minecraft.level.getBrightness(LightLayer.BLOCK, var7);
            var14.add("Client Light: " + var17 + " (" + var18 + " sky, " + var19 + " block)");
            LevelChunk var20 = this.getServerChunk();
            StringBuilder var21 = new StringBuilder("CH");

            for(Heightmap.Types var25 : Heightmap.Types.values()) {
               if (var25.sendToClient()) {
                  var21.append(" ").append(HEIGHTMAP_NAMES.get(var25)).append(": ").append(var16.getHeight(var25, var7.getX(), var7.getZ()));
               }
            }

            var14.add(var21.toString());
            var21.setLength(0);
            var21.append("SH");

            for(Heightmap.Types var40 : Heightmap.Types.values()) {
               if (var40.keepAfterWorldgen()) {
                  var21.append(" ").append(HEIGHTMAP_NAMES.get(var40)).append(": ");
                  if (var20 != null) {
                     var21.append(var20.getHeight(var40, var7.getX(), var7.getZ()));
                  } else {
                     var21.append("??");
                  }
               }
            }

            var14.add(var21.toString());
            if (var7.getY() >= this.minecraft.level.getMinBuildHeight() && var7.getY() < this.minecraft.level.getMaxBuildHeight()) {
               var14.add("Biome: " + printBiome(this.minecraft.level.getBiome(var7)));
               if (var20 != null) {
                  float var33 = var12.getMoonBrightness();
                  long var36 = var20.getInhabitedTime();
                  DifficultyInstance var41 = new DifficultyInstance(var12.getDifficulty(), var12.getDayTime(), var36, var33);
                  var14.add(
                     String.format(
                        Locale.ROOT,
                        "Local Difficulty: %.2f // %.2f (Day %d)",
                        var41.getEffectiveDifficulty(),
                        var41.getSpecialMultiplier(),
                        this.minecraft.level.getDayTime() / 24000L
                     )
                  );
               } else {
                  var14.add("Local Difficulty: ??");
               }
            }

            if (var20 != null && var20.isOldNoiseGeneration()) {
               var14.add("Blending: Old");
            }
         }

         ServerLevel var26 = this.getServerLevel();
         if (var26 != null) {
            ServerChunkCache var27 = var26.getChunkSource();
            ChunkGenerator var29 = var27.getGenerator();
            RandomState var30 = var27.randomState();
            var29.addDebugScreenInfo(var14, var30, var7);
            Climate.Sampler var31 = var30.sampler();
            BiomeSource var34 = var29.getBiomeSource();
            var34.addDebugInfo(var14, var7, var31);
            NaturalSpawner.SpawnState var37 = var27.getLastSpawnState();
            if (var37 != null) {
               Object2IntMap var39 = var37.getMobCategoryCounts();
               int var42 = var37.getSpawnableChunkCount();
               var14.add(
                  "SC: "
                     + var42
                     + ", "
                     + (String)Stream.of(MobCategory.values())
                        .map(var1x -> Character.toUpperCase(var1x.getName().charAt(0)) + ": " + var39.getInt(var1x))
                        .collect(Collectors.joining(", "))
               );
            } else {
               var14.add("SC: N/A");
            }
         }

         PostChain var28 = this.minecraft.gameRenderer.currentEffect();
         if (var28 != null) {
            var14.add("Shader: " + var28.getName());
         }

         var14.add(
            this.minecraft.getSoundManager().getDebugString()
               + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(this.minecraft.player.getCurrentMood() * 100.0F))
         );
         return var14;
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
            .thenApply(var0 -> (LevelChunk)var0.map(var0x -> (LevelChunk)var0x, var0x -> null));
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
            String.format(Locale.ROOT, "Java: %s %dbit", System.getProperty("java.version"), this.minecraft.is64Bit() ? 64 : 32),
            String.format(Locale.ROOT, "Mem: % 2d%% %03d/%03dMB", var7 * 100L / var1, bytesToMegabytes(var7), bytesToMegabytes(var1)),
            String.format(Locale.ROOT, "Allocation rate: %03dMB /s", bytesToMegabytes(this.allocationRateCalculator.bytesAllocatedPerSecond(var7))),
            String.format(Locale.ROOT, "Allocated: % 2d%% %03dMB", var3 * 100L / var1, bytesToMegabytes(var3)),
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
            UnmodifiableIterator var12 = var11.getValues().entrySet().iterator();

            while(var12.hasNext()) {
               Entry var13 = (Entry)var12.next();
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
            UnmodifiableIterator var17 = var16.getValues().entrySet().iterator();

            while(var17.hasNext()) {
               Entry var18 = (Entry)var17.next();
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
      String var4 = Util.getPropertyName(var2, var3);
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

   public void logTickDuration(long var1) {
      this.tickTimeLogger.logSample(var1);
   }

   public SampleLogger getPingLogger() {
      return this.pingLogger;
   }

   public SampleLogger getBandwidthLogger() {
      return this.bandwidthLogger;
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

         for(GarbageCollectorMXBean var3 : GC_MBEANS) {
            var0 += var3.getCollectionCount();
         }

         return var0;
      }
   }
}
