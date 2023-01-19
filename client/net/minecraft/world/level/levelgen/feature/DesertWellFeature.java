package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DesertWellFeature extends Feature<NoneFeatureConfiguration> {
   private static final BlockStatePredicate IS_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
   private final BlockState sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
   private final BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
   private final BlockState water = Blocks.WATER.defaultBlockState();

   public DesertWellFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      var3 = var3.above();

      while(var2.isEmptyBlock(var3) && var3.getY() > var2.getMinBuildHeight() + 2) {
         var3 = var3.below();
      }

      if (!IS_SAND.test(var2.getBlockState(var3))) {
         return false;
      } else {
         for(int var4 = -2; var4 <= 2; ++var4) {
            for(int var5 = -2; var5 <= 2; ++var5) {
               if (var2.isEmptyBlock(var3.offset(var4, -1, var5)) && var2.isEmptyBlock(var3.offset(var4, -2, var5))) {
                  return false;
               }
            }
         }

         for(int var8 = -1; var8 <= 0; ++var8) {
            for(int var13 = -2; var13 <= 2; ++var13) {
               for(int var6 = -2; var6 <= 2; ++var6) {
                  var2.setBlock(var3.offset(var13, var8, var6), this.sandstone, 2);
               }
            }
         }

         var2.setBlock(var3, this.water, 2);

         for(Direction var14 : Direction.Plane.HORIZONTAL) {
            var2.setBlock(var3.relative(var14), this.water, 2);
         }

         for(int var10 = -2; var10 <= 2; ++var10) {
            for(int var15 = -2; var15 <= 2; ++var15) {
               if (var10 == -2 || var10 == 2 || var15 == -2 || var15 == 2) {
                  var2.setBlock(var3.offset(var10, 1, var15), this.sandstone, 2);
               }
            }
         }

         var2.setBlock(var3.offset(2, 1, 0), this.sandSlab, 2);
         var2.setBlock(var3.offset(-2, 1, 0), this.sandSlab, 2);
         var2.setBlock(var3.offset(0, 1, 2), this.sandSlab, 2);
         var2.setBlock(var3.offset(0, 1, -2), this.sandSlab, 2);

         for(int var11 = -1; var11 <= 1; ++var11) {
            for(int var16 = -1; var16 <= 1; ++var16) {
               if (var11 == 0 && var16 == 0) {
                  var2.setBlock(var3.offset(var11, 4, var16), this.sandstone, 2);
               } else {
                  var2.setBlock(var3.offset(var11, 4, var16), this.sandSlab, 2);
               }
            }
         }

         for(int var12 = 1; var12 <= 3; ++var12) {
            var2.setBlock(var3.offset(-1, var12, -1), this.sandstone, 2);
            var2.setBlock(var3.offset(-1, var12, 1), this.sandstone, 2);
            var2.setBlock(var3.offset(1, var12, -1), this.sandstone, 2);
            var2.setBlock(var3.offset(1, var12, 1), this.sandstone, 2);
         }

         return true;
      }
   }
}
