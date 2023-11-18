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
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
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
   private static final int RED = -65536;
   private static final int YELLOW = -256;
   private static final int GREEN = -16711936;

   public DebugScreenOverlay(Minecraft var1) {
      super();
      this.minecraft = var1;
      this.allocationRateCalculator = new DebugScreenOverlay.AllocationRateCalculator();
      this.font = var1.font;
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
         if (this.minecraft.options.renderFpsChart) {
            int var2x = var1.guiWidth();
            this.drawChart(var1, this.minecraft.getFrameTimer(), 0, var2x / 2, true);
            IntegratedServer var3 = this.minecraft.getSingleplayerServer();
            if (var3 != null) {
               this.drawChart(var1, var3.getFrameTimer(), var2x - Math.min(var2x / 2, 240), var2x / 2, false);
            }
         }
      });
      this.minecraft.getProfiler().pop();
   }

   protected void drawGameInformation(GuiGraphics var1) {
      List var2 = this.getGameInformation();
      var2.add("");
      boolean var3 = this.minecraft.getSingleplayerServer() != null;
      var2.add(
         "Debug: Pie [shift]: "
            + (this.minecraft.options.renderDebugCharts ? "visible" : "hidden")
            + (var3 ? " FPS + TPS" : " FPS")
            + " [alt]: "
            + (this.minecraft.options.renderFpsChart ? "visible" : "hidden")
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
      Connection var3 = this.minecraft.getConnection().getConnection();
      float var4 = var3.getAverageSentPackets();
      float var5 = var3.getAverageReceivedPackets();
      String var1;
      if (var2 != null) {
         var1 = String.format(Locale.ROOT, "Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", var2.getAverageTickTime(), var4, var5);
      } else {
         var1 = String.format(Locale.ROOT, "\"%s\" server, %.0f tx, %.0f rx", this.minecraft.player.getServerBrand(), var4, var5);
      }

      BlockPos var6 = this.minecraft.getCameraEntity().blockPosition();
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
               this.minecraft.levelRenderer.getChunkStatistics(),
               this.minecraft.levelRenderer.getEntityStatistics(),
               "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(),
               this.minecraft.level.gatherChunkSourceStats(),
               "",
               String.format(Locale.ROOT, "Chunk-relative: %d %d %d", var6.getX() & 15, var6.getY() & 15, var6.getZ() & 15)
            }
         );
      } else {
         Entity var7 = this.minecraft.getCameraEntity();
         Direction var8 = var7.getDirection();

         String var9 = switch(var8) {
            case NORTH -> "Towards negative Z";
            case SOUTH -> "Towards positive Z";
            case WEST -> "Towards negative X";
            case EAST -> "Towards positive X";
            default -> "Invalid";
         };
         ChunkPos var10 = new ChunkPos(var6);
         if (!Objects.equals(this.lastPos, var10)) {
            this.lastPos = var10;
            this.clearChunkCache();
         }

         Level var11 = this.getLevel();
         Object var12 = var11 instanceof ServerLevel ? ((ServerLevel)var11).getForcedChunks() : LongSets.EMPTY_SET;
         ArrayList var13 = Lists.newArrayList(
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
               this.minecraft.levelRenderer.getChunkStatistics(),
               this.minecraft.levelRenderer.getEntityStatistics(),
               "P: " + this.minecraft.particleEngine.countParticles() + ". T: " + this.minecraft.level.getEntityCount(),
               this.minecraft.level.gatherChunkSourceStats()
            }
         );
         String var14 = this.getServerChunkStats();
         if (var14 != null) {
            var13.add(var14);
         }

         var13.add(this.minecraft.level.dimension().location() + " FC: " + var12.size());
         var13.add("");
         var13.add(
            String.format(
               Locale.ROOT,
               "XYZ: %.3f / %.5f / %.3f",
               this.minecraft.getCameraEntity().getX(),
               this.minecraft.getCameraEntity().getY(),
               this.minecraft.getCameraEntity().getZ()
            )
         );
         var13.add(
            String.format(
               Locale.ROOT, "Block: %d %d %d [%d %d %d]", var6.getX(), var6.getY(), var6.getZ(), var6.getX() & 15, var6.getY() & 15, var6.getZ() & 15
            )
         );
         var13.add(
            String.format(
               Locale.ROOT,
               "Chunk: %d %d %d [%d %d in r.%d.%d.mca]",
               var10.x,
               SectionPos.blockToSectionCoord(var6.getY()),
               var10.z,
               var10.getRegionLocalX(),
               var10.getRegionLocalZ(),
               var10.getRegionX(),
               var10.getRegionZ()
            )
         );
         var13.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", var8, var9, Mth.wrapDegrees(var7.getYRot()), Mth.wrapDegrees(var7.getXRot())));
         LevelChunk var15 = this.getClientChunk();
         if (var15.isEmpty()) {
            var13.add("Waiting for chunk...");
         } else {
            int var16 = this.minecraft.level.getChunkSource().getLightEngine().getRawBrightness(var6, 0);
            int var17 = this.minecraft.level.getBrightness(LightLayer.SKY, var6);
            int var18 = this.minecraft.level.getBrightness(LightLayer.BLOCK, var6);
            var13.add("Client Light: " + var16 + " (" + var17 + " sky, " + var18 + " block)");
            LevelChunk var19 = this.getServerChunk();
            StringBuilder var20 = new StringBuilder("CH");

            for(Heightmap.Types var24 : Heightmap.Types.values()) {
               if (var24.sendToClient()) {
                  var20.append(" ").append(HEIGHTMAP_NAMES.get(var24)).append(": ").append(var15.getHeight(var24, var6.getX(), var6.getZ()));
               }
            }

            var13.add(var20.toString());
            var20.setLength(0);
            var20.append("SH");

            for(Heightmap.Types var39 : Heightmap.Types.values()) {
               if (var39.keepAfterWorldgen()) {
                  var20.append(" ").append(HEIGHTMAP_NAMES.get(var39)).append(": ");
                  if (var19 != null) {
                     var20.append(var19.getHeight(var39, var6.getX(), var6.getZ()));
                  } else {
                     var20.append("??");
                  }
               }
            }

            var13.add(var20.toString());
            if (var6.getY() >= this.minecraft.level.getMinBuildHeight() && var6.getY() < this.minecraft.level.getMaxBuildHeight()) {
               var13.add("Biome: " + printBiome(this.minecraft.level.getBiome(var6)));
               long var32 = 0L;
               float var37 = 0.0F;
               if (var19 != null) {
                  var37 = var11.getMoonBrightness();
                  var32 = var19.getInhabitedTime();
               }

               DifficultyInstance var40 = new DifficultyInstance(var11.getDifficulty(), var11.getDayTime(), var32, var37);
               var13.add(
                  String.format(
                     Locale.ROOT,
                     "Local Difficulty: %.2f // %.2f (Day %d)",
                     var40.getEffectiveDifficulty(),
                     var40.getSpecialMultiplier(),
                     this.minecraft.level.getDayTime() / 24000L
                  )
               );
            }

            if (var19 != null && var19.isOldNoiseGeneration()) {
               var13.add("Blending: Old");
            }
         }

         ServerLevel var25 = this.getServerLevel();
         if (var25 != null) {
            ServerChunkCache var26 = var25.getChunkSource();
            ChunkGenerator var28 = var26.getGenerator();
            RandomState var29 = var26.randomState();
            var28.addDebugScreenInfo(var13, var29, var6);
            Climate.Sampler var30 = var29.sampler();
            BiomeSource var33 = var28.getBiomeSource();
            var33.addDebugInfo(var13, var6, var30);
            NaturalSpawner.SpawnState var35 = var26.getLastSpawnState();
            if (var35 != null) {
               Object2IntMap var38 = var35.getMobCategoryCounts();
               int var41 = var35.getSpawnableChunkCount();
               var13.add(
                  "SC: "
                     + var41
                     + ", "
                     + (String)Stream.of(MobCategory.values())
                        .map(var1x -> Character.toUpperCase(var1x.getName().charAt(0)) + ": " + var38.getInt(var1x))
                        .collect(Collectors.joining(", "))
               );
            } else {
               var13.add("SC: N/A");
            }
         }

         PostChain var27 = this.minecraft.gameRenderer.currentEffect();
         if (var27 != null) {
            var13.add("Shader: " + var27.getName());
         }

         var13.add(
            this.minecraft.getSoundManager().getDebugString()
               + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(this.minecraft.player.getCurrentMood() * 100.0F))
         );
         return var13;
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
         if (var1 != null) {
            this.serverChunk = var1.getChunkSource()
               .getChunkFuture(this.lastPos.x, this.lastPos.z, ChunkStatus.FULL, false)
               .thenApply(var0 -> (LevelChunk)var0.map(var0x -> (LevelChunk)var0x, var0x -> null));
         }

         if (this.serverChunk == null) {
            this.serverChunk = CompletableFuture.completedFuture(this.getClientChunk());
         }
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

   private void drawChart(GuiGraphics var1, FrameTimer var2, int var3, int var4, boolean var5) {
      int var6 = var2.getLogStart();
      int var7 = var2.getLogEnd();
      long[] var8 = var2.getLog();
      int var10 = var3;
      int var11 = Math.max(0, var8.length - var4);
      int var12 = var8.length - var11;
      int var9 = var2.wrapIndex(var6 + var11);
      long var13 = 0L;
      int var15 = 2147483647;
      int var16 = -2147483648;

      for(int var17 = 0; var17 < var12; ++var17) {
         int var18 = (int)(var8[var2.wrapIndex(var9 + var17)] / 1000000L);
         var15 = Math.min(var15, var18);
         var16 = Math.max(var16, var18);
         var13 += (long)var18;
      }

      int var22 = var1.guiHeight();
      var1.fill(RenderType.guiOverlay(), var3, var22 - 60, var3 + var12, var22, -1873784752);

      while(var9 != var7) {
         int var23 = var2.scaleSampleTo(var8[var9], var5 ? 30 : 60, var5 ? 60 : 20);
         int var19 = var5 ? 100 : 60;
         int var20 = this.getSampleColor(Mth.clamp(var23, 0, var19), 0, var19 / 2, var19);
         var1.fill(RenderType.guiOverlay(), var10, var22 - var23, var10 + 1, var22, var20);
         ++var10;
         var9 = var2.wrapIndex(var9 + 1);
      }

      if (var5) {
         var1.fill(RenderType.guiOverlay(), var3 + 1, var22 - 30 + 1, var3 + 14, var22 - 30 + 10, -1873784752);
         var1.drawString(this.font, "60 FPS", var3 + 2, var22 - 30 + 2, 14737632, false);
         var1.hLine(RenderType.guiOverlay(), var3, var3 + var12 - 1, var22 - 30, -1);
         var1.fill(RenderType.guiOverlay(), var3 + 1, var22 - 60 + 1, var3 + 14, var22 - 60 + 10, -1873784752);
         var1.drawString(this.font, "30 FPS", var3 + 2, var22 - 60 + 2, 14737632, false);
         var1.hLine(RenderType.guiOverlay(), var3, var3 + var12 - 1, var22 - 60, -1);
      } else {
         var1.fill(RenderType.guiOverlay(), var3 + 1, var22 - 60 + 1, var3 + 14, var22 - 60 + 10, -1873784752);
         var1.drawString(this.font, "20 TPS", var3 + 2, var22 - 60 + 2, 14737632, false);
         var1.hLine(RenderType.guiOverlay(), var3, var3 + var12 - 1, var22 - 60, -1);
      }

      var1.hLine(RenderType.guiOverlay(), var3, var3 + var12 - 1, var22 - 1, -1);
      var1.vLine(RenderType.guiOverlay(), var3, var22 - 60, var22, -1);
      var1.vLine(RenderType.guiOverlay(), var3 + var12 - 1, var22 - 60, var22, -1);
      int var24 = this.minecraft.options.framerateLimit().get();
      if (var5 && var24 > 0 && var24 <= 250) {
         var1.hLine(RenderType.guiOverlay(), var3, var3 + var12 - 1, var22 - 1 - (int)(1800.0 / (double)var24), -16711681);
      }

      String var25 = var15 + " ms min";
      String var26 = var13 / (long)var12 + " ms avg";
      String var21 = var16 + " ms max";
      var1.drawString(this.font, var25, var3 + 2, var22 - 60 - 9, 14737632);
      var1.drawCenteredString(this.font, var26, var3 + var12 / 2, var22 - 60 - 9, 14737632);
      var1.drawString(this.font, var21, var3 + var12 - this.font.width(var21), var22 - 60 - 9, 14737632);
   }

   private int getSampleColor(int var1, int var2, int var3, int var4) {
      return var1 < var3
         ? this.colorLerp(-16711936, -256, (float)var1 / (float)var3)
         : this.colorLerp(-256, -65536, (float)(var1 - var3) / (float)(var4 - var3));
   }

   private int colorLerp(int var1, int var2, float var3) {
      int var4 = var1 >> 24 & 0xFF;
      int var5 = var1 >> 16 & 0xFF;
      int var6 = var1 >> 8 & 0xFF;
      int var7 = var1 & 0xFF;
      int var8 = var2 >> 24 & 0xFF;
      int var9 = var2 >> 16 & 0xFF;
      int var10 = var2 >> 8 & 0xFF;
      int var11 = var2 & 0xFF;
      int var12 = Mth.clamp((int)Mth.lerp(var3, (float)var4, (float)var8), 0, 255);
      int var13 = Mth.clamp((int)Mth.lerp(var3, (float)var5, (float)var9), 0, 255);
      int var14 = Mth.clamp((int)Mth.lerp(var3, (float)var6, (float)var10), 0, 255);
      int var15 = Mth.clamp((int)Mth.lerp(var3, (float)var7, (float)var11), 0, 255);
      return var12 << 24 | var13 << 16 | var14 << 8 | var15;
   }

   private static long bytesToMegabytes(long var0) {
      return var0 / 1024L / 1024L;
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
