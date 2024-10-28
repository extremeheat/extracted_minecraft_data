package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
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

   public boolean place(FeaturePlaceContext<ProbabilityFeatureConfiguration> var1) {
      int var2 = 0;
      BlockPos var3 = var1.origin();
      WorldGenLevel var4 = var1.level();
      RandomSource var5 = var1.random();
      ProbabilityFeatureConfiguration var6 = (ProbabilityFeatureConfiguration)var1.config();
      BlockPos.MutableBlockPos var7 = var3.mutable();
      BlockPos.MutableBlockPos var8 = var3.mutable();
      if (var4.isEmptyBlock(var7)) {
         if (Blocks.BAMBOO.defaultBlockState().canSurvive(var4, var7)) {
            int var9 = var5.nextInt(12) + 5;
            int var10;
            if (var5.nextFloat() < var6.probability) {
               var10 = var5.nextInt(4) + 1;

               for(int var11 = var3.getX() - var10; var11 <= var3.getX() + var10; ++var11) {
                  for(int var12 = var3.getZ() - var10; var12 <= var3.getZ() + var10; ++var12) {
                     int var13 = var11 - var3.getX();
                     int var14 = var12 - var3.getZ();
                     if (var13 * var13 + var14 * var14 <= var10 * var10) {
                        var8.set(var11, var4.getHeight(Heightmap.Types.WORLD_SURFACE, var11, var12) - 1, var12);
                        if (isDirt(var4.getBlockState(var8))) {
                           var4.setBlock(var8, Blocks.PODZOL.defaultBlockState(), 2);
                        }
                     }
                  }
               }
            }

            for(var10 = 0; var10 < var9 && var4.isEmptyBlock(var7); ++var10) {
               var4.setBlock(var7, BAMBOO_TRUNK, 2);
               var7.move(Direction.UP, 1);
            }

            if (var7.getY() - var3.getY() >= 3) {
               var4.setBlock(var7, BAMBOO_FINAL_LARGE, 2);
               var4.setBlock(var7.move(Direction.DOWN, 1), BAMBOO_TOP_LARGE, 2);
               var4.setBlock(var7.move(Direction.DOWN, 1), BAMBOO_TOP_SMALL, 2);
            }
         }

         ++var2;
      }

      return var2 > 0;
   }

   static {
      BAMBOO_TRUNK = (BlockState)((BlockState)((BlockState)Blocks.BAMBOO.defaultBlockState().setValue(BambooStalkBlock.AGE, 1)).setValue(BambooStalkBlock.LEAVES, BambooLeaves.NONE)).setValue(BambooStalkBlock.STAGE, 0);
      BAMBOO_FINAL_LARGE = (BlockState)((BlockState)BAMBOO_TRUNK.setValue(BambooStalkBlock.LEAVES, BambooLeaves.LARGE)).setValue(BambooStalkBlock.STAGE, 1);
      BAMBOO_TOP_LARGE = (BlockState)BAMBOO_TRUNK.setValue(BambooStalkBlock.LEAVES, BambooLeaves.LARGE);
      BAMBOO_TOP_SMALL = (BlockState)BAMBOO_TRUNK.setValue(BambooStalkBlock.LEAVES, BambooLeaves.SMALL);
   }
}
