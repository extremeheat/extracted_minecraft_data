package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class WorldCarver<C extends CarverConfiguration> {
   public static final WorldCarver<CaveCarverConfiguration> CAVE;
   public static final WorldCarver<CaveCarverConfiguration> NETHER_CAVE;
   public static final WorldCarver<CanyonCarverConfiguration> CANYON;
   protected static final BlockState AIR;
   protected static final BlockState CAVE_AIR;
   protected static final FluidState WATER;
   protected static final FluidState LAVA;
   protected Set<Block> replaceableBlocks;
   protected Set<Fluid> liquids;
   private final Codec<ConfiguredWorldCarver<C>> configuredCodec;

   private static <C extends CarverConfiguration, F extends WorldCarver<C>> F register(String var0, F var1) {
      return (WorldCarver)Registry.register(Registry.CARVER, (String)var0, var1);
   }

   public WorldCarver(Codec<C> var1) {
      super();
      this.replaceableBlocks = ImmutableSet.of(Blocks.WATER, Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, new Block[]{Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE, Blocks.DEEPSLATE, Blocks.CALCITE, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.TUFF, Blocks.GRANITE, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.RAW_IRON_BLOCK, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.RAW_COPPER_BLOCK});
      this.liquids = ImmutableSet.of(Fluids.WATER);
      this.configuredCodec = var1.fieldOf("config").xmap(this::configured, ConfiguredWorldCarver::config).codec();
   }

   public ConfiguredWorldCarver<C> configured(C var1) {
      return new ConfiguredWorldCarver(this, var1);
   }

   public Codec<ConfiguredWorldCarver<C>> configuredCodec() {
      return this.configuredCodec;
   }

   public int getRange() {
      return 4;
   }

   protected boolean carveEllipsoid(CarvingContext var1, C var2, ChunkAccess var3, Function<BlockPos, Biome> var4, Aquifer var5, double var6, double var8, double var10, double var12, double var14, CarvingMask var16, WorldCarver.CarveSkipChecker var17) {
      ChunkPos var18 = var3.getPos();
      double var19 = (double)var18.getMiddleBlockX();
      double var21 = (double)var18.getMiddleBlockZ();
      double var23 = 16.0D + var12 * 2.0D;
      if (!(Math.abs(var6 - var19) > var23) && !(Math.abs(var10 - var21) > var23)) {
         int var25 = var18.getMinBlockX();
         int var26 = var18.getMinBlockZ();
         int var27 = Math.max(Mth.floor(var6 - var12) - var25 - 1, 0);
         int var28 = Math.min(Mth.floor(var6 + var12) - var25, 15);
         int var29 = Math.max(Mth.floor(var8 - var14) - 1, var1.getMinGenY() + 1);
         int var30 = var3.isUpgrading() ? 0 : 7;
         int var31 = Math.min(Mth.floor(var8 + var14) + 1, var1.getMinGenY() + var1.getGenDepth() - 1 - var30);
         int var32 = Math.max(Mth.floor(var10 - var12) - var26 - 1, 0);
         int var33 = Math.min(Mth.floor(var10 + var12) - var26, 15);
         boolean var34 = false;
         BlockPos.MutableBlockPos var35 = new BlockPos.MutableBlockPos();
         BlockPos.MutableBlockPos var36 = new BlockPos.MutableBlockPos();

         for(int var37 = var27; var37 <= var28; ++var37) {
            int var38 = var18.getBlockX(var37);
            double var39 = ((double)var38 + 0.5D - var6) / var12;

            for(int var41 = var32; var41 <= var33; ++var41) {
               int var42 = var18.getBlockZ(var41);
               double var43 = ((double)var42 + 0.5D - var10) / var12;
               if (!(var39 * var39 + var43 * var43 >= 1.0D)) {
                  MutableBoolean var45 = new MutableBoolean(false);

                  for(int var46 = var31; var46 > var29; --var46) {
                     double var47 = ((double)var46 - 0.5D - var8) / var14;
                     if (!var17.shouldSkip(var1, var39, var47, var43, var46) && (!var16.get(var37, var46, var41) || isDebugEnabled(var2))) {
                        var16.set(var37, var46, var41);
                        var35.set(var38, var46, var42);
                        var34 |= this.carveBlock(var1, var2, var3, var4, var16, var35, var36, var5, var45);
                     }
                  }
               }
            }
         }

         return var34;
      } else {
         return false;
      }
   }

   protected boolean carveBlock(CarvingContext var1, C var2, ChunkAccess var3, Function<BlockPos, Biome> var4, CarvingMask var5, BlockPos.MutableBlockPos var6, BlockPos.MutableBlockPos var7, Aquifer var8, MutableBoolean var9) {
      BlockState var10 = var3.getBlockState(var6);
      if (var10.is(Blocks.GRASS_BLOCK) || var10.is(Blocks.MYCELIUM)) {
         var9.setTrue();
      }

      if (!this.canReplaceBlock(var10) && !isDebugEnabled(var2)) {
         return false;
      } else {
         BlockState var11 = this.getCarveState(var1, var2, var6, var8);
         if (var11 == null) {
            return false;
         } else {
            var3.setBlockState(var6, var11, false);
            if (var8.shouldScheduleFluidUpdate() && !var11.getFluidState().isEmpty()) {
               var3.markPosForPostprocessing(var6);
            }

            if (var9.isTrue()) {
               var7.setWithOffset(var6, (Direction)Direction.DOWN);
               if (var3.getBlockState(var7).is(Blocks.DIRT)) {
                  var1.topMaterial(var4, var3, var7, !var11.getFluidState().isEmpty()).ifPresent((var2x) -> {
                     var3.setBlockState(var7, var2x, false);
                     if (!var2x.getFluidState().isEmpty()) {
                        var3.markPosForPostprocessing(var7);
                     }

                  });
               }
            }

            return true;
         }
      }
   }

   @Nullable
   private BlockState getCarveState(CarvingContext var1, C var2, BlockPos var3, Aquifer var4) {
      if (var3.getY() <= var2.lavaLevel.resolveY(var1)) {
         return LAVA.createLegacyBlock();
      } else {
         BlockState var5 = var4.computeSubstance(var3.getX(), var3.getY(), var3.getZ(), 0.0D, 0.0D);
         if (var5 == null) {
            return isDebugEnabled(var2) ? var2.debugSettings.getBarrierState() : null;
         } else {
            return isDebugEnabled(var2) ? getDebugState(var2, var5) : var5;
         }
      }
   }

   private static BlockState getDebugState(CarverConfiguration var0, BlockState var1) {
      if (var1.is(Blocks.AIR)) {
         return var0.debugSettings.getAirState();
      } else if (var1.is(Blocks.WATER)) {
         BlockState var2 = var0.debugSettings.getWaterState();
         return var2.hasProperty(BlockStateProperties.WATERLOGGED) ? (BlockState)var2.setValue(BlockStateProperties.WATERLOGGED, true) : var2;
      } else {
         return var1.is(Blocks.LAVA) ? var0.debugSettings.getLavaState() : var1;
      }
   }

   public abstract boolean carve(CarvingContext var1, C var2, ChunkAccess var3, Function<BlockPos, Biome> var4, Random var5, Aquifer var6, ChunkPos var7, CarvingMask var8);

   public abstract boolean isStartChunk(C var1, Random var2);

   protected boolean canReplaceBlock(BlockState var1) {
      return this.replaceableBlocks.contains(var1.getBlock());
   }

   protected static boolean canReach(ChunkPos var0, double var1, double var3, int var5, int var6, float var7) {
      double var8 = (double)var0.getMiddleBlockX();
      double var10 = (double)var0.getMiddleBlockZ();
      double var12 = var1 - var8;
      double var14 = var3 - var10;
      double var16 = (double)(var6 - var5);
      double var18 = (double)(var7 + 2.0F + 16.0F);
      return var12 * var12 + var14 * var14 - var16 * var16 <= var18 * var18;
   }

   private static boolean isDebugEnabled(CarverConfiguration var0) {
      return var0.debugSettings.isDebugMode();
   }

   static {
      CAVE = register("cave", new CaveWorldCarver(CaveCarverConfiguration.CODEC));
      NETHER_CAVE = register("nether_cave", new NetherWorldCarver(CaveCarverConfiguration.CODEC));
      CANYON = register("canyon", new CanyonWorldCarver(CanyonCarverConfiguration.CODEC));
      AIR = Blocks.AIR.defaultBlockState();
      CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
      WATER = Fluids.WATER.defaultFluidState();
      LAVA = Fluids.LAVA.defaultFluidState();
   }

   public interface CarveSkipChecker {
      boolean shouldSkip(CarvingContext var1, double var2, double var4, double var6, int var8);
   }
}
