package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class WorldCarver<C extends CarverConfiguration> {
   public static final WorldCarver<ProbabilityFeatureConfiguration> CAVE;
   public static final WorldCarver<ProbabilityFeatureConfiguration> NETHER_CAVE;
   public static final WorldCarver<ProbabilityFeatureConfiguration> CANYON;
   public static final WorldCarver<ProbabilityFeatureConfiguration> UNDERWATER_CANYON;
   public static final WorldCarver<ProbabilityFeatureConfiguration> UNDERWATER_CAVE;
   protected static final BlockState AIR;
   protected static final BlockState CAVE_AIR;
   protected static final FluidState WATER;
   protected static final FluidState LAVA;
   protected Set<Block> replaceableBlocks;
   protected Set<Fluid> liquids;
   private final Codec<ConfiguredWorldCarver<C>> configuredCodec;
   protected final int genHeight;

   private static <C extends CarverConfiguration, F extends WorldCarver<C>> F register(String var0, F var1) {
      return (WorldCarver)Registry.register(Registry.CARVER, (String)var0, var1);
   }

   public WorldCarver(Codec<C> var1, int var2) {
      super();
      this.replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE});
      this.liquids = ImmutableSet.of(Fluids.WATER);
      this.genHeight = var2;
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

   protected boolean carveSphere(ChunkAccess var1, Function<BlockPos, Biome> var2, long var3, int var5, int var6, int var7, double var8, double var10, double var12, double var14, double var16, BitSet var18) {
      Random var19 = new Random(var3 + (long)var6 + (long)var7);
      double var20 = (double)(var6 * 16 + 8);
      double var22 = (double)(var7 * 16 + 8);
      if (var8 >= var20 - 16.0D - var14 * 2.0D && var12 >= var22 - 16.0D - var14 * 2.0D && var8 <= var20 + 16.0D + var14 * 2.0D && var12 <= var22 + 16.0D + var14 * 2.0D) {
         int var24 = Math.max(Mth.floor(var8 - var14) - var6 * 16 - 1, 0);
         int var25 = Math.min(Mth.floor(var8 + var14) - var6 * 16 + 1, 16);
         int var26 = Math.max(Mth.floor(var10 - var16) - 1, 1);
         int var27 = Math.min(Mth.floor(var10 + var16) + 1, this.genHeight - 8);
         int var28 = Math.max(Mth.floor(var12 - var14) - var7 * 16 - 1, 0);
         int var29 = Math.min(Mth.floor(var12 + var14) - var7 * 16 + 1, 16);
         if (this.hasWater(var1, var6, var7, var24, var25, var26, var27, var28, var29)) {
            return false;
         } else {
            boolean var30 = false;
            BlockPos.MutableBlockPos var31 = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos var32 = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos var33 = new BlockPos.MutableBlockPos();

            for(int var34 = var24; var34 < var25; ++var34) {
               int var35 = var34 + var6 * 16;
               double var36 = ((double)var35 + 0.5D - var8) / var14;

               for(int var38 = var28; var38 < var29; ++var38) {
                  int var39 = var38 + var7 * 16;
                  double var40 = ((double)var39 + 0.5D - var12) / var14;
                  if (var36 * var36 + var40 * var40 < 1.0D) {
                     MutableBoolean var42 = new MutableBoolean(false);

                     for(int var43 = var27; var43 > var26; --var43) {
                        double var44 = ((double)var43 - 0.5D - var10) / var16;
                        if (!this.skip(var36, var44, var40, var43)) {
                           var30 |= this.carveBlock(var1, var2, var18, var19, var31, var32, var33, var5, var6, var7, var35, var39, var34, var43, var38, var42);
                        }
                     }
                  }
               }
            }

            return var30;
         }
      } else {
         return false;
      }
   }

   protected boolean carveBlock(ChunkAccess var1, Function<BlockPos, Biome> var2, BitSet var3, Random var4, BlockPos.MutableBlockPos var5, BlockPos.MutableBlockPos var6, BlockPos.MutableBlockPos var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, MutableBoolean var16) {
      int var17 = var13 | var15 << 4 | var14 << 8;
      if (var3.get(var17)) {
         return false;
      } else {
         var3.set(var17);
         var5.set(var11, var14, var12);
         BlockState var18 = var1.getBlockState(var5);
         BlockState var19 = var1.getBlockState(var6.setWithOffset(var5, Direction.UP));
         if (var18.is(Blocks.GRASS_BLOCK) || var18.is(Blocks.MYCELIUM)) {
            var16.setTrue();
         }

         if (!this.canReplaceBlock(var18, var19)) {
            return false;
         } else {
            if (var14 < 11) {
               var1.setBlockState(var5, LAVA.createLegacyBlock(), false);
            } else {
               var1.setBlockState(var5, CAVE_AIR, false);
               if (var16.isTrue()) {
                  var7.setWithOffset(var5, Direction.DOWN);
                  if (var1.getBlockState(var7).is(Blocks.DIRT)) {
                     var1.setBlockState(var7, ((Biome)var2.apply(var5)).getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial(), false);
                  }
               }
            }

            return true;
         }
      }
   }

   public abstract boolean carve(ChunkAccess var1, Function<BlockPos, Biome> var2, Random var3, int var4, int var5, int var6, int var7, int var8, BitSet var9, C var10);

   public abstract boolean isStartChunk(Random var1, int var2, int var3, C var4);

   protected boolean canReplaceBlock(BlockState var1) {
      return this.replaceableBlocks.contains(var1.getBlock());
   }

   protected boolean canReplaceBlock(BlockState var1, BlockState var2) {
      return this.canReplaceBlock(var1) || (var1.is(Blocks.SAND) || var1.is(Blocks.GRAVEL)) && !var2.getFluidState().is(FluidTags.WATER);
   }

   protected boolean hasWater(ChunkAccess var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();

      for(int var11 = var4; var11 < var5; ++var11) {
         for(int var12 = var8; var12 < var9; ++var12) {
            for(int var13 = var6 - 1; var13 <= var7 + 1; ++var13) {
               if (this.liquids.contains(var1.getFluidState(var10.set(var11 + var2 * 16, var13, var12 + var3 * 16)).getType())) {
                  return true;
               }

               if (var13 != var7 + 1 && !this.isEdge(var4, var5, var8, var9, var11, var12)) {
                  var13 = var7;
               }
            }
         }
      }

      return false;
   }

   private boolean isEdge(int var1, int var2, int var3, int var4, int var5, int var6) {
      return var5 == var1 || var5 == var2 - 1 || var6 == var3 || var6 == var4 - 1;
   }

   protected boolean canReach(int var1, int var2, double var3, double var5, int var7, int var8, float var9) {
      double var10 = (double)(var1 * 16 + 8);
      double var12 = (double)(var2 * 16 + 8);
      double var14 = var3 - var10;
      double var16 = var5 - var12;
      double var18 = (double)(var8 - var7);
      double var20 = (double)(var9 + 2.0F + 16.0F);
      return var14 * var14 + var16 * var16 - var18 * var18 <= var20 * var20;
   }

   protected abstract boolean skip(double var1, double var3, double var5, int var7);

   static {
      CAVE = register("cave", new CaveWorldCarver(ProbabilityFeatureConfiguration.CODEC, 256));
      NETHER_CAVE = register("nether_cave", new NetherWorldCarver(ProbabilityFeatureConfiguration.CODEC));
      CANYON = register("canyon", new CanyonWorldCarver(ProbabilityFeatureConfiguration.CODEC));
      UNDERWATER_CANYON = register("underwater_canyon", new UnderwaterCanyonWorldCarver(ProbabilityFeatureConfiguration.CODEC));
      UNDERWATER_CAVE = register("underwater_cave", new UnderwaterCaveWorldCarver(ProbabilityFeatureConfiguration.CODEC));
      AIR = Blocks.AIR.defaultBlockState();
      CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
      WATER = Fluids.WATER.defaultFluidState();
      LAVA = Fluids.LAVA.defaultFluidState();
   }
}
