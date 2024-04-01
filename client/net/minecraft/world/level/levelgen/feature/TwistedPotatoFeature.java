package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowerfulPotatoBlock;
import net.minecraft.world.level.block.StrongRootsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TwistedPotatoFeature extends Feature<NoneFeatureConfiguration> {
   public TwistedPotatoFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      RandomSource var4 = var1.random();
      if (canReplace(var2, var3)) {
         var2.setBlock(var3, passiveCorePotato(), 2);
         generatePlant(var2, var3.below(), var4, 16);
         return true;
      } else {
         return false;
      }
   }

   private static BlockState passiveCorePotato() {
      return Blocks.POWERFUL_POTATO.defaultBlockState().setValue(PowerfulPotatoBlock.SPROUTS, Integer.valueOf(3));
   }

   public static void generatePlant(LevelAccessor var0, BlockPos var1, RandomSource var2, int var3) {
      var0.setBlock(var1, StrongRootsBlock.getStateWithConnections(var0, var1, Blocks.WEAK_ROOTS.defaultBlockState()), 2);
      growTreeRecursive(var0, var1, var2, var1, var3, 0);
   }

   public static boolean canReplace(LevelAccessor var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      if (var2.is(Blocks.POWERFUL_POTATO)) {
         return false;
      } else if (var2.is(BlockTags.FEATURES_CANNOT_REPLACE)) {
         return false;
      } else {
         return !var2.is(Blocks.WEAK_ROOTS);
      }
   }

   private static void growTreeRecursive(LevelAccessor var0, BlockPos var1, RandomSource var2, BlockPos var3, int var4, int var5) {
      Block var6 = Blocks.WEAK_ROOTS;
      int var7 = var2.nextInt(4) + 1;
      if (var5 == 0) {
         ++var7;
      }

      for(int var8 = 0; var8 < var7; ++var8) {
         BlockPos var9 = var1.below(var8 + 1);
         var0.setBlock(var9, StrongRootsBlock.getStateWithConnections(var0, var9, var6.defaultBlockState()), 2);
         var0.setBlock(var9.above(), StrongRootsBlock.getStateWithConnections(var0, var9.above(), var6.defaultBlockState()), 2);
      }

      if (var5 < 4) {
         int var12 = var2.nextInt(4);
         if (var5 == 0) {
            ++var12;
         }

         for(int var13 = 0; var13 < var12; ++var13) {
            Direction var10 = Direction.Plane.HORIZONTAL.getRandomDirection(var2);
            BlockPos var11 = var1.below(var7).relative(var10);
            if (Math.abs(var11.getX() - var3.getX()) < var4
               && Math.abs(var11.getZ() - var3.getZ()) < var4
               && canReplace(var0, var11)
               && canReplace(var0, var11.below())) {
               var0.setBlock(var11, StrongRootsBlock.getStateWithConnections(var0, var11, var6.defaultBlockState()), 2);
               var0.setBlock(
                  var11.relative(var10.getOpposite()),
                  StrongRootsBlock.getStateWithConnections(var0, var11.relative(var10.getOpposite()), var6.defaultBlockState()),
                  2
               );
               growTreeRecursive(var0, var11, var2, var3, var4, var5 + 1);
            }
         }
      }
   }
}
