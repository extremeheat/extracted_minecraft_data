package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class HashWellFeature extends Feature<NoneFeatureConfiguration> {
   private static final BlockStatePredicate IS_SAND = BlockStatePredicate.forBlock(Blocks.GRAVTATER);
   private final BlockState sand = Blocks.GRAVTATER.defaultBlockState();
   private final BlockState sandSlab = Blocks.CHARRED_BAKED_POTATO_BRICK_SLAB.defaultBlockState();
   private final BlockState sandstone = Blocks.CHARRED_BAKED_POTATO_BRICKS.defaultBlockState();
   private final BlockState water = Blocks.LIGHT
      .defaultBlockState()
      .setValue(LightBlock.LEVEL, Integer.valueOf(15))
      .setValue(LightBlock.WATERLOGGED, Boolean.valueOf(true));

   public HashWellFeature(Codec<NoneFeatureConfiguration> var1) {
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

         for(int var8 = -2; var8 <= 0; ++var8) {
            for(int var11 = -2; var11 <= 2; ++var11) {
               for(int var6 = -2; var6 <= 2; ++var6) {
                  var2.setBlock(var3.offset(var11, var8, var6), this.sandstone, 2);
               }
            }
         }

         var2.setBlock(var3, this.water, 2);

         for(Direction var12 : Direction.Plane.HORIZONTAL) {
            var2.setBlock(var3.relative(var12), this.water, 2);
         }

         BlockPos var10 = var3.below();
         var2.setBlock(var10, this.sand, 2);

         for(Direction var17 : Direction.Plane.HORIZONTAL) {
            var2.setBlock(var10.relative(var17), this.sand, 2);
         }

         for(int var14 = -2; var14 <= 2; ++var14) {
            for(int var18 = -2; var18 <= 2; ++var18) {
               if (var14 == -2 || var14 == 2 || var18 == -2 || var18 == 2) {
                  var2.setBlock(var3.offset(var14, 1, var18), this.sandstone, 2);
               }
            }
         }

         var2.setBlock(var3.offset(2, 1, 0), this.sandSlab, 2);
         var2.setBlock(var3.offset(-2, 1, 0), this.sandSlab, 2);
         var2.setBlock(var3.offset(0, 1, 2), this.sandSlab, 2);
         var2.setBlock(var3.offset(0, 1, -2), this.sandSlab, 2);

         for(int var15 = -1; var15 <= 1; ++var15) {
            for(int var19 = -1; var19 <= 1; ++var19) {
               if (var15 == 0 && var19 == 0) {
                  var2.setBlock(var3.offset(var15, 4, var19), this.sandstone, 2);
               } else {
                  var2.setBlock(var3.offset(var15, 4, var19), this.sandSlab, 2);
               }
            }
         }

         for(int var16 = 1; var16 <= 3; ++var16) {
            var2.setBlock(var3.offset(-1, var16, -1), this.sandstone, 2);
            var2.setBlock(var3.offset(-1, var16, 1), this.sandstone, 2);
            var2.setBlock(var3.offset(1, var16, -1), this.sandstone, 2);
            var2.setBlock(var3.offset(1, var16, 1), this.sandstone, 2);
         }

         return true;
      }
   }
}
