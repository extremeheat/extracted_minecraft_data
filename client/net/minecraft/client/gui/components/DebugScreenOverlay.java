package net.minecraft.client.gui.components;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraft.util.debugchart.TpsDebugDimensions;
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
   private static final Map<Heightmap.Types, String> HEIGHTMAP_NAMES = (Map)Util.make(new EnumMap(Heightmap.Types.class), (var0) -> {
      var0.put(Heightmap.Types.WORLD_SURFACE_WG, "SW");
      var0.put(Heightmap.Types.WORLD_SURFACE, "S");
      var0.put(Heightmap.Types.OCEAN_FLOOR_WG, "OW");
      var0.put(Heightmap.Types.OCEAN_FLOOR, "O");
      var0.put(Heightmap.Types.MOTION_BLOCKING, "M");
      var0.put(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, "ML");
   });
   private final Minecraft minecraft;
   private final AllocationRateCalculator allocationRateCalculator;
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
   private final Map<RemoteDebugSampleType, LocalSampleLogger> remoteSupportingLoggers;
   private final FpsDebugChart fpsChart;
   private final TpsDebugChart tpsChart;
   private final PingDebugChart pingChart;
   private final BandwidthDebugChart bandwidthChart;

   public DebugScreenOverlay(Minecraft var1) {
      super();
      this.remoteSupportingLoggers = Map.of(RemoteDebugSampleType.TICK_TIME, this.tickTimeLogger);
      this.minecraft = var1;
      this.allocationRateCalculator = new AllocationRateCalculator();
      this.font = var1.font;
      this.fpsChart = new FpsDebugChart(this.font, this.frameTimeLogger);
      this.tpsChart = new TpsDebugChart(this.font, this.tickTimeLogger, () -> {
         return var1.level.tickRateManager().millisecondsPerTick();
      });
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
         int var2;
         int var3;
         int var4;
         if (this.renderFpsCharts) {
            var2 = var1.guiWidth();
            var3 = var2 / 2;
            this.fpsChart.drawChart(var1, 0, this.fpsChart.getWidth(var3));
            if (this.tickTimeLogger.size() > 0) {
               var4 = this.tpsChart.getWidth(var3);
               this.tpsChart.drawChart(var1, var2 - var4, var4);
            }
         }

         if (this.renderNetworkCharts) {
            var2 = var1.guiWidth();
            var3 = var2 / 2;
            if (!this.minecraft.isLocalServer()) {
               this.bandwidthChart.drawChart(var1, 0, this.bandwidthChart.getWidth(var3));
            }

            var4 = this.pingChart.getWidth(var3);
            this.pingChart.drawChart(var1, var2 - var4, var4);
         }

      });
      this.minecraft.getProfiler().pop();
   }

   protected void drawGameInformation(GuiGraphics var1) {
      List var2 = this.getGameInformation();
      var2.add("");
      boolean var3 = this.minecraft.getSingleplayerServer() != null;
      String var10001 = this.renderProfilerChart ? "visible" : "hidden";
      var2.add("Debug charts: [F3+1] Profiler " + var10001 + "; [F3+2] " + (var3 ? "FPS + TPS " : "FPS ") + (this.renderFpsCharts ? "visible" : "hidden") + "; [F3+3] " + (!this.minecraft.isLocalServer() ? "Bandwidth + Ping" : "Ping") + (this.renderNetworkCharts ? " visible" : " hidden"));
      var2.add("For help: press F3 + Q");
      this.renderLines(var1, var2, true);
   }

   protected void drawSystemInformation(GuiGraphics var1) {
      List var2 = this.getSystemInformation();
      this.renderLines(var1, var2, false);
   }

   private void renderLines(GuiGraphics var1, List<String> var2, boolean var3) {
      Objects.requireNonNull(this.font);
      byte var4 = 9;

      int var5;
      String var6;
      int var7;
      int var8;
      int var9;
      for(var5 = 0; var5 < var2.size(); ++var5) {
         var6 = (String)var2.get(var5);
         if (!Strings.isNullOrEmpty(var6)) {
            var7 = this.font.width(var6);
            var8 = var3 ? 2 : var1.guiWidth() - 2 - var7;
            var9 = 2 + var4 * var5;
            var1.fill(var8 - 1, var9 - 1, var8 + var7 + 1, var9 + var4 - 1, -1873784752);
         }
      }

      for(var5 = 0; var5 < var2.size(); ++var5) {
         var6 = (String)var2.get(var5);
         if (!Strings.isNullOrEmpty(var6)) {
            var7 = this.font.width(var6);
            var8 = var3 ? 2 : var1.guiWidth() - 2 - var7;
            var9 = 2 + var4 * var5;
            var1.drawString(this.font, var6, var8, var9, 14737632, false);
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
      String[] var10000;
      String var10003;
      if (this.minecraft.showOnlyReducedInfo()) {
         var10000 = new String[9];
         var10003 = SharedConstants.getCurrentVersion().getName();
         var10000[0] = "Minecraft " + var10003 + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ")";
         var10000[1] = this.minecraft.fpsString;
         var10000[2] = var1;
         var10000[3] = this.minecraft.levelRenderer.getSectionStatistics();
         var10000[4] = this.minecraft.levelRenderer.getEntityStatistics();
         var10003 = this.minecraft.particleEngine.countParticles();
         var10000[5] = "P: " + var10003 + ". T: " + this.minecraft.level.getEntityCount();
         var10000[6] = this.minecraft.level.gatherChunkSourceStats();
         var10000[7] = "";
         var10000[8] = String.format(Locale.ROOT, "Chunk-relative: %d %d %d", var28.getX() & 15, var28.getY() & 15, var28.getZ() & 15);
         return Lists.newArrayList(var10000);
      } else {
         Entity var29 = this.minecraft.getCameraEntity();
         Direction var30 = var29.getDirection();
         String var12;
         switch (var30) {
            case NORTH -> var12 = "Towards negative Z";
            case SOUTH -> var12 = "Towards positive Z";
            case WEST -> var12 = "Towards negative X";
            case EAST -> var12 = "Towards positive X";
            default -> var12 = "Invalid";
         }

         ChunkPos var13 = new ChunkPos(var28);
         if (!Objects.equals(this.lastPos, var13)) {
            this.lastPos = var13;
            this.clearChunkCache();
         }

         Level var14 = this.getLevel();
         Object var15 = var14 instanceof ServerLevel ? ((ServerLevel)var14).getForcedChunks() : LongSets.EMPTY_SET;
         var10000 = new String[7];
         var10003 = SharedConstants.getCurrentVersion().getName();
         var10000[0] = "Minecraft " + var10003 + " (" + this.minecraft.getLaunchedVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType()) + ")";
         var10000[1] = this.minecraft.fpsString;
         var10000[2] = var1;
         var10000[3] = this.minecraft.levelRenderer.getSectionStatistics();
         var10000[4] = this.minecraft.levelRenderer.getEntityStatistics();
         var10003 = this.minecraft.particleEngine.countParticles();
         var10000[5] = "P: " + var10003 + ". T: " + this.minecraft.level.getEntityCount();
         var10000[6] = this.minecraft.level.gatherChunkSourceStats();
         ArrayList var16 = Lists.newArrayList(var10000);
         String var17 = this.getServerChunkStats();
         if (var17 != null) {
            var16.add(var17);
         }

         String var10001 = String.valueOf(this.minecraft.level.dimension().location());
         var16.add(var10001 + " FC: " + ((LongSet)var15).size());
         var16.add("");
         var16.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.minecraft.getCameraEntity().getX(), this.minecraft.getCameraEntity().getY(), this.minecraft.getCameraEntity().getZ()));
         var16.add(String.format(Locale.ROOT, "Block: %d %d %d [%d %d %d]", var28.getX(), var28.getY(), var28.getZ(), var28.getX() & 15, var28.getY() & 15, var28.getZ() & 15));
         var16.add(String.format(Locale.ROOT, "Chunk: %d %d %d [%d %d in r.%d.%d.mca]", var13.x, SectionPos.blockToSectionCoord(var28.getY()), var13.z, var13.getRegionLocalX(), var13.getRegionLocalZ(), var13.getRegionX(), var13.getRegionZ()));
         var16.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", var30, var12, Mth.wrapDegrees(var29.getYRot()), Mth.wrapDegrees(var29.getXRot())));
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
            Heightmap.Types[] var24 = Heightmap.Types.values();
            int var25 = var24.length;

            int var26;
            Heightmap.Types var27;
            for(var26 = 0; var26 < var25; ++var26) {
               var27 = var24[var26];
               if (var27.sendToClient()) {
                  var23.append(" ").append((String)HEIGHTMAP_NAMES.get(var27)).append(": ").append(var18.getHeight(var27, var28.getX(), var28.getZ()));
               }
            }

            var16.add(var23.toString());
            var23.setLength(0);
            var23.append("SH");
            var24 = Heightmap.Types.values();
            var25 = var24.length;

            for(var26 = 0; var26 < var25; ++var26) {
               var27 = var24[var26];
               if (var27.keepAfterWorldgen()) {
                  var23.append(" ").append((String)HEIGHTMAP_NAMES.get(var27)).append(": ");
                  if (var22 != null) {
                     var23.append(var22.getHeight(var27, var28.getX(), var28.getZ()));
                  } else {
                     var23.append("??");
                  }
               }
            }

            var16.add(var23.toString());
            if (var28.getY() >= this.minecraft.level.getMinBuildHeight() && var28.getY() < this.minecraft.level.getMaxBuildHeight()) {
               Holder var31 = this.minecraft.level.getBiome(var28);
               var16.add("Biome: " + printBiome(var31));
               if (var22 != null) {
                  float var38 = var14.getMoonBrightness();
                  long var40 = var22.getInhabitedTime();
                  DifficultyInstance var43 = new DifficultyInstance(var14.getDifficulty(), var14.getDayTime(), var40, var38);
                  var16.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", var43.getEffectiveDifficulty(), var43.getSpecialMultiplier(), this.minecraft.level.getDayTime() / 24000L));
               } else {
                  var16.add("Local Difficulty: ??");
               }
            }

            if (var22 != null && var22.isOldNoiseGeneration()) {
               var16.add("Blending: Old");
            }
         }

         ServerLevel var32 = this.getServerLevel();
         if (var32 != null) {
            ServerChunkCache var33 = var32.getChunkSource();
            ChunkGenerator var35 = var33.getGenerator();
            RandomState var36 = var33.randomState();
            var35.addDebugScreenInfo(var16, var36, var28);
            Climate.Sampler var37 = var36.sampler();
            BiomeSource var39 = var35.getBiomeSource();
            var39.addDebugInfo(var16, var28, var37);
            NaturalSpawner.SpawnState var41 = var33.getLastSpawnState();
            if (var41 != null) {
               Object2IntMap var42 = var41.getMobCategoryCounts();
               int var44 = var41.getSpawnableChunkCount();
               var16.add("SC: " + var44 + ", " + (String)Stream.of(MobCategory.values()).map((var1x) -> {
                  char var10000 = Character.toUpperCase(var1x.getName().charAt(0));
                  return "" + var10000 + ": " + var42.getInt(var1x);
               }).collect(Collectors.joining(", ")));
            } else {
               var16.add("SC: N/A");
            }
         }

         PostChain var34 = this.minecraft.gameRenderer.currentEffect();
         if (var34 != null) {
            var16.add("Shader: " + var34.getName());
         }

         var10001 = this.minecraft.getSoundManager().getDebugString();
         var16.add(var10001 + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(this.minecraft.player.getCurrentMood() * 100.0F)));
         return var16;
      }
   }

   private static String printBiome(Holder<Biome> var0) {
      return (String)var0.unwrap().map((var0x) -> {
         return var0x.location().toString();
      }, (var0x) -> {
         return "[unregistered " + String.valueOf(var0x) + "]";
      });
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
      return (Level)DataFixUtils.orElse(Optional.ofNullable(this.minecraft.getSingleplayerServer()).flatMap((var1) -> {
         return Optional.ofNullable(var1.getLevel(this.minecraft.level.dimension()));
      }), this.minecraft.level);
   }

   @Nullable
   private LevelChunk getServerChunk() {
      if (this.serverChunk == null) {
         ServerLevel var1 = this.getServerLevel();
         if (var1 == null) {
            return null;
         }

         this.serverChunk = var1.getChunkSource().getChunkFuture(this.lastPos.x, this.lastPos.z, ChunkStatus.FULL, false).thenApply((var0) -> {
            return (LevelChunk)var0.orElse((Object)null);
         });
      }

      return (LevelChunk)this.serverChunk.getNow((Object)null);
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
      ArrayList var9 = Lists.newArrayList(new String[]{String.format(Locale.ROOT, "Java: %s", System.getProperty("java.version")), String.format(Locale.ROOT, "Mem: %2d%% %03d/%03dMB", var7 * 100L / var1, bytesToMegabytes(var7), bytesToMegabytes(var1)), String.format(Locale.ROOT, "Allocation rate: %03dMB/s", bytesToMegabytes(this.allocationRateCalculator.bytesAllocatedPerSecond(var7))), String.format(Locale.ROOT, "Allocated: %2d%% %03dMB", var3 * 100L / var1, bytesToMegabytes(var3)), "", String.format(Locale.ROOT, "CPU: %s", GlUtil.getCpuInfo()), "", String.format(Locale.ROOT, "Display: %dx%d (%s)", Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), GlUtil.getVendor()), GlUtil.getRenderer(), GlUtil.getOpenGLVersion()});
      if (this.minecraft.showOnlyReducedInfo()) {
         return var9;
      } else {
         BlockPos var10;
         Iterator var12;
         Map.Entry var13;
         Stream var10000;
         String var10001;
         if (this.block.getType() == HitResult.Type.BLOCK) {
            var10 = ((BlockHitResult)this.block).getBlockPos();
            BlockState var11 = this.minecraft.level.getBlockState(var10);
            var9.add("");
            var10001 = String.valueOf(ChatFormatting.UNDERLINE);
            var9.add(var10001 + "Targeted Block: " + var10.getX() + ", " + var10.getY() + ", " + var10.getZ());
            var9.add(String.valueOf(BuiltInRegistries.BLOCK.getKey(var11.getBlock())));
            var12 = var11.getValues().entrySet().iterator();

            while(var12.hasNext()) {
               var13 = (Map.Entry)var12.next();
               var9.add(this.getPropertyValueString(var13));
            }

            var10000 = var11.getTags().map((var0) -> {
               return "#" + String.valueOf(var0.location());
            });
            Objects.requireNonNull(var9);
            var10000.forEach(var9::add);
         }

         if (this.liquid.getType() == HitResult.Type.BLOCK) {
            var10 = ((BlockHitResult)this.liquid).getBlockPos();
            FluidState var15 = this.minecraft.level.getFluidState(var10);
            var9.add("");
            var10001 = String.valueOf(ChatFormatting.UNDERLINE);
            var9.add(var10001 + "Targeted Fluid: " + var10.getX() + ", " + var10.getY() + ", " + var10.getZ());
            var9.add(String.valueOf(BuiltInRegistries.FLUID.getKey(var15.getType())));
            var12 = var15.getValues().entrySet().iterator();

            while(var12.hasNext()) {
               var13 = (Map.Entry)var12.next();
               var9.add(this.getPropertyValueString(var13));
            }

            var10000 = var15.getTags().map((var0) -> {
               return "#" + String.valueOf(var0.location());
            });
            Objects.requireNonNull(var9);
            var10000.forEach(var9::add);
         }

         Entity var14 = this.minecraft.crosshairPickEntity;
         if (var14 != null) {
            var9.add("");
            var9.add(String.valueOf(ChatFormatting.UNDERLINE) + "Targeted Entity");
            var9.add(String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(var14.getType())));
         }

         return var9;
      }
   }

   private String getPropertyValueString(Map.Entry<Property<?>, Comparable<?>> var1) {
      Property var2 = (Property)var1.getKey();
      Comparable var3 = (Comparable)var1.getValue();
      String var4 = Util.getPropertyName(var2, var3);
      String var10000;
      if (Boolean.TRUE.equals(var3)) {
         var10000 = String.valueOf(ChatFormatting.GREEN);
         var4 = var10000 + var4;
      } else if (Boolean.FALSE.equals(var3)) {
         var10000 = String.valueOf(ChatFormatting.RED);
         var4 = var10000 + var4;
      }

      var10000 = var2.getName();
      return var10000 + ": " + var4;
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

   public void logRemoteSample(long[] var1, RemoteDebugSampleType var2) {
      LocalSampleLogger var3 = (LocalSampleLogger)this.remoteSupportingLoggers.get(var2);
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

   private static class AllocationRateCalculator {
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

         GarbageCollectorMXBean var3;
         for(Iterator var2 = GC_MBEANS.iterator(); var2.hasNext(); var0 += var3.getCollectionCount()) {
            var3 = (GarbageCollectorMXBean)var2.next();
         }

         return var0;
      }
   }
}
