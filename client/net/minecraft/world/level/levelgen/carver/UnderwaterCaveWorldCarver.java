package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class UnderwaterCaveWorldCarver extends CaveWorldCarver {
   public UnderwaterCaveWorldCarver(Codec<ProbabilityFeatureConfiguration> var1) {
      super(var1, 256);
      this.replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR, Blocks.PACKED_ICE});
   }

   protected boolean hasWater(ChunkAccess var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      return false;
   }

   protected boolean carveBlock(ChunkAccess var1, Function<BlockPos, Biome> var2, BitSet var3, Random var4, BlockPos.MutableBlockPos var5, BlockPos.MutableBlockPos var6, BlockPos.MutableBlockPos var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, MutableBoolean var16) {
      return carveBlock(this, var1, var3, var4, var5, var8, var9, var10, var11, var12, var13, var14, var15);
   }

   protected static boolean carveBlock(WorldCarver<?> var0, ChunkAccess var1, BitSet var2, Random var3, BlockPos.MutableBlockPos var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
      if (var11 >= var5) {
         return false;
      } else {
         int var13 = var10 | var12 << 4 | var11 << 8;
         if (var2.get(var13)) {
            return false;
         } else {
            var2.set(var13);
            var4.set(var8, var11, var9);
            BlockState var14 = var1.getBlockState(var4);
            if (!var0.canReplaceBlock(var14)) {
               return false;
            } else if (var11 == 10) {
               float var20 = var3.nextFloat();
               if ((double)var20 < 0.25D) {
                  var1.setBlockState(var4, Blocks.MAGMA_BLOCK.defaultBlockState(), false);
                  var1.getBlockTicks().scheduleTick(var4, Blocks.MAGMA_BLOCK, 0);
               } else {
                  var1.setBlockState(var4, Blocks.OBSIDIAN.defaultBlockState(), false);
               }

               return true;
            } else if (var11 < 10) {
               var1.setBlockState(var4, Blocks.LAVA.defaultBlockState(), false);
               return false;
            } else {
               boolean var15 = false;
               Iterator var16 = Direction.Plane.HORIZONTAL.iterator();

               while(var16.hasNext()) {
                  Direction var17 = (Direction)var16.next();
                  int var18 = var8 + var17.getStepX();
                  int var19 = var9 + var17.getStepZ();
                  if (var18 >> 4 != var6 || var19 >> 4 != var7 || var1.getBlockState(var4.set(var18, var11, var19)).isAir()) {
                     var1.setBlockState(var4, WATER.createLegacyBlock(), false);
                     var1.getLiquidTicks().scheduleTick(var4, WATER.getType(), 0);
                     var15 = true;
                     break;
                  }
               }

               var4.set(var8, var11, var9);
               if (!var15) {
                  var1.setBlockState(var4, WATER.createLegacyBlock(), false);
                  return true;
               } else {
                  return true;
               }
            }
         }
      }
   }
}
