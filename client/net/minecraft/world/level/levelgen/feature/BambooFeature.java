package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class BambooFeature extends Feature<ProbabilityFeatureConfiguration> {
   private static final BlockState BAMBOO_TRUNK;
   private static final BlockState BAMBOO_FINAL_LARGE;
   private static final BlockState BAMBOO_TOP_LARGE;
   private static final BlockState BAMBOO_TOP_SMALL;

   public BambooFeature(Codec<ProbabilityFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, ProbabilityFeatureConfiguration var5) {
      int var6 = 0;
      BlockPos.MutableBlockPos var7 = var4.mutable();
      BlockPos.MutableBlockPos var8 = var4.mutable();
      if (var1.isEmptyBlock(var7)) {
         if (Blocks.BAMBOO.defaultBlockState().canSurvive(var1, var7)) {
            int var9 = var3.nextInt(12) + 5;
            int var10;
            if (var3.nextFloat() < var5.probability) {
               var10 = var3.nextInt(4) + 1;

               for(int var11 = var4.getX() - var10; var11 <= var4.getX() + var10; ++var11) {
                  for(int var12 = var4.getZ() - var10; var12 <= var4.getZ() + var10; ++var12) {
                     int var13 = var11 - var4.getX();
                     int var14 = var12 - var4.getZ();
                     if (var13 * var13 + var14 * var14 <= var10 * var10) {
                        var8.set(var11, var1.getHeight(Heightmap.Types.WORLD_SURFACE, var11, var12) - 1, var12);
                        if (isDirt(var1.getBlockState(var8))) {
                           var1.setBlock(var8, Blocks.PODZOL.defaultBlockState(), 2);
                        }
                     }
                  }
               }
            }

            for(var10 = 0; var10 < var9 && var1.isEmptyBlock(var7); ++var10) {
               var1.setBlock(var7, BAMBOO_TRUNK, 2);
               var7.move(Direction.UP, 1);
            }

            if (var7.getY() - var4.getY() >= 3) {
               var1.setBlock(var7, BAMBOO_FINAL_LARGE, 2);
               var1.setBlock(var7.move(Direction.DOWN, 1), BAMBOO_TOP_LARGE, 2);
               var1.setBlock(var7.move(Direction.DOWN, 1), BAMBOO_TOP_SMALL, 2);
            }
         }

         ++var6;
      }

      return var6 > 0;
   }

   static {
      BAMBOO_TRUNK = (BlockState)((BlockState)((BlockState)Blocks.BAMBOO.defaultBlockState().setValue(BambooBlock.AGE, 1)).setValue(BambooBlock.LEAVES, BambooLeaves.NONE)).setValue(BambooBlock.STAGE, 0);
      BAMBOO_FINAL_LARGE = (BlockState)((BlockState)BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.LARGE)).setValue(BambooBlock.STAGE, 1);
      BAMBOO_TOP_LARGE = (BlockState)BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.LARGE);
      BAMBOO_TOP_SMALL = (BlockState)BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.SMALL);
   }
}
