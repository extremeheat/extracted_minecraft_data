package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DesertWellFeature extends Feature<NoneFeatureConfiguration> {
   private static final BlockStatePredicate IS_SAND;
   private final BlockState sandSlab;
   private final BlockState sandstone;
   private final BlockState water;

   public DesertWellFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
      this.sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
      this.sandstone = Blocks.SANDSTONE.defaultBlockState();
      this.water = Blocks.WATER.defaultBlockState();
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();

      for(var3 = var3.above(); var2.isEmptyBlock(var3) && var3.getY() > var2.getMinBuildHeight() + 2; var3 = var3.below()) {
      }

      if (!IS_SAND.test(var2.getBlockState(var3))) {
         return false;
      } else {
         int var4;
         int var5;
         for(var4 = -2; var4 <= 2; ++var4) {
            for(var5 = -2; var5 <= 2; ++var5) {
               if (var2.isEmptyBlock(var3.offset(var4, -1, var5)) && var2.isEmptyBlock(var3.offset(var4, -2, var5))) {
                  return false;
               }
            }
         }

         for(var4 = -1; var4 <= 0; ++var4) {
            for(var5 = -2; var5 <= 2; ++var5) {
               for(int var6 = -2; var6 <= 2; ++var6) {
                  var2.setBlock(var3.offset(var5, var4, var6), this.sandstone, 2);
               }
            }
         }

         var2.setBlock(var3, this.water, 2);
         Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

         while(var7.hasNext()) {
            Direction var8 = (Direction)var7.next();
            var2.setBlock(var3.relative(var8), this.water, 2);
         }

         for(var4 = -2; var4 <= 2; ++var4) {
            for(var5 = -2; var5 <= 2; ++var5) {
               if (var4 == -2 || var4 == 2 || var5 == -2 || var5 == 2) {
                  var2.setBlock(var3.offset(var4, 1, var5), this.sandstone, 2);
               }
            }
         }

         var2.setBlock(var3.offset(2, 1, 0), this.sandSlab, 2);
         var2.setBlock(var3.offset(-2, 1, 0), this.sandSlab, 2);
         var2.setBlock(var3.offset(0, 1, 2), this.sandSlab, 2);
         var2.setBlock(var3.offset(0, 1, -2), this.sandSlab, 2);

         for(var4 = -1; var4 <= 1; ++var4) {
            for(var5 = -1; var5 <= 1; ++var5) {
               if (var4 == 0 && var5 == 0) {
                  var2.setBlock(var3.offset(var4, 4, var5), this.sandstone, 2);
               } else {
                  var2.setBlock(var3.offset(var4, 4, var5), this.sandSlab, 2);
               }
            }
         }

         for(var4 = 1; var4 <= 3; ++var4) {
            var2.setBlock(var3.offset(-1, var4, -1), this.sandstone, 2);
            var2.setBlock(var3.offset(-1, var4, 1), this.sandstone, 2);
            var2.setBlock(var3.offset(1, var4, -1), this.sandstone, 2);
            var2.setBlock(var3.offset(1, var4, 1), this.sandstone, 2);
         }

         return true;
      }
   }

   static {
      IS_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
   }
}
