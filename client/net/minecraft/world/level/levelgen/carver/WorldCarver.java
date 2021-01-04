package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public abstract class WorldCarver<C extends CarverConfiguration> {
   public static final WorldCarver<ProbabilityFeatureConfiguration> CAVE = register("cave", new CaveWorldCarver(ProbabilityFeatureConfiguration::deserialize, 256));
   public static final WorldCarver<ProbabilityFeatureConfiguration> HELL_CAVE = register("hell_cave", new HellCaveWorldCarver(ProbabilityFeatureConfiguration::deserialize));
   public static final WorldCarver<ProbabilityFeatureConfiguration> CANYON = register("canyon", new CanyonWorldCarver(ProbabilityFeatureConfiguration::deserialize));
   public static final WorldCarver<ProbabilityFeatureConfiguration> UNDERWATER_CANYON = register("underwater_canyon", new UnderwaterCanyonWorldCarver(ProbabilityFeatureConfiguration::deserialize));
   public static final WorldCarver<ProbabilityFeatureConfiguration> UNDERWATER_CAVE = register("underwater_cave", new UnderwaterCaveWorldCarver(ProbabilityFeatureConfiguration::deserialize));
   protected static final BlockState AIR;
   protected static final BlockState CAVE_AIR;
   protected static final FluidState WATER;
   protected static final FluidState LAVA;
   protected Set<Block> replaceableBlocks;
   protected Set<Fluid> liquids;
   private final Function<Dynamic<?>, ? extends C> configurationFactory;
   protected final int genHeight;

   private static <C extends CarverConfiguration, F extends WorldCarver<C>> F register(String var0, F var1) {
      return (WorldCarver)Registry.register(Registry.CARVER, (String)var0, var1);
   }

   public WorldCarver(Function<Dynamic<?>, ? extends C> var1, int var2) {
      super();
      this.replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE});
      this.liquids = ImmutableSet.of(Fluids.WATER);
      this.configurationFactory = var1;
      this.genHeight = var2;
   }

   public int getRange() {
      return 4;
   }

   protected boolean carveSphere(ChunkAccess var1, long var2, int var4, int var5, int var6, double var7, double var9, double var11, double var13, double var15, BitSet var17) {
      Random var18 = new Random(var2 + (long)var5 + (long)var6);
      double var19 = (double)(var5 * 16 + 8);
      double var21 = (double)(var6 * 16 + 8);
      if (var7 >= var19 - 16.0D - var13 * 2.0D && var11 >= var21 - 16.0D - var13 * 2.0D && var7 <= var19 + 16.0D + var13 * 2.0D && var11 <= var21 + 16.0D + var13 * 2.0D) {
         int var23 = Math.max(Mth.floor(var7 - var13) - var5 * 16 - 1, 0);
         int var24 = Math.min(Mth.floor(var7 + var13) - var5 * 16 + 1, 16);
         int var25 = Math.max(Mth.floor(var9 - var15) - 1, 1);
         int var26 = Math.min(Mth.floor(var9 + var15) + 1, this.genHeight - 8);
         int var27 = Math.max(Mth.floor(var11 - var13) - var6 * 16 - 1, 0);
         int var28 = Math.min(Mth.floor(var11 + var13) - var6 * 16 + 1, 16);
         if (this.hasWater(var1, var5, var6, var23, var24, var25, var26, var27, var28)) {
            return false;
         } else {
            boolean var29 = false;
            BlockPos.MutableBlockPos var30 = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos var31 = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos var32 = new BlockPos.MutableBlockPos();

            for(int var33 = var23; var33 < var24; ++var33) {
               int var34 = var33 + var5 * 16;
               double var35 = ((double)var34 + 0.5D - var7) / var13;

               for(int var37 = var27; var37 < var28; ++var37) {
                  int var38 = var37 + var6 * 16;
                  double var39 = ((double)var38 + 0.5D - var11) / var13;
                  if (var35 * var35 + var39 * var39 < 1.0D) {
                     AtomicBoolean var41 = new AtomicBoolean(false);

                     for(int var42 = var26; var42 > var25; --var42) {
                        double var43 = ((double)var42 - 0.5D - var9) / var15;
                        if (!this.skip(var35, var43, var39, var42)) {
                           var29 |= this.carveBlock(var1, var17, var18, var30, var31, var32, var4, var5, var6, var34, var38, var33, var42, var37, var41);
                        }
                     }
                  }
               }
            }

            return var29;
         }
      } else {
         return false;
      }
   }

   protected boolean carveBlock(ChunkAccess var1, BitSet var2, Random var3, BlockPos.MutableBlockPos var4, BlockPos.MutableBlockPos var5, BlockPos.MutableBlockPos var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, AtomicBoolean var15) {
      int var16 = var12 | var14 << 4 | var13 << 8;
      if (var2.get(var16)) {
         return false;
      } else {
         var2.set(var16);
         var4.set(var10, var13, var11);
         BlockState var17 = var1.getBlockState(var4);
         BlockState var18 = var1.getBlockState(var5.set((Vec3i)var4).move(Direction.UP));
         if (var17.getBlock() == Blocks.GRASS_BLOCK || var17.getBlock() == Blocks.MYCELIUM) {
            var15.set(true);
         }

         if (!this.canReplaceBlock(var17, var18)) {
            return false;
         } else {
            if (var13 < 11) {
               var1.setBlockState(var4, LAVA.createLegacyBlock(), false);
            } else {
               var1.setBlockState(var4, CAVE_AIR, false);
               if (var15.get()) {
                  var6.set((Vec3i)var4).move(Direction.DOWN);
                  if (var1.getBlockState(var6).getBlock() == Blocks.DIRT) {
                     var1.setBlockState(var6, var1.getBiome(var4).getSurfaceBuilderConfig().getTopMaterial(), false);
                  }
               }
            }

            return true;
         }
      }
   }

   public abstract boolean carve(ChunkAccess var1, Random var2, int var3, int var4, int var5, int var6, int var7, BitSet var8, C var9);

   public abstract boolean isStartChunk(Random var1, int var2, int var3, C var4);

   protected boolean canReplaceBlock(BlockState var1) {
      return this.replaceableBlocks.contains(var1.getBlock());
   }

   protected boolean canReplaceBlock(BlockState var1, BlockState var2) {
      Block var3 = var1.getBlock();
      return this.canReplaceBlock(var1) || (var3 == Blocks.SAND || var3 == Blocks.GRAVEL) && !var2.getFluidState().is(FluidTags.WATER);
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
      AIR = Blocks.AIR.defaultBlockState();
      CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
      WATER = Fluids.WATER.defaultFluidState();
      LAVA = Fluids.LAVA.defaultFluidState();
   }
}
