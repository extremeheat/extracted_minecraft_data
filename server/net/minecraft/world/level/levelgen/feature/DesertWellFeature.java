package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.chunk.ChunkGenerator;
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

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      for(var4 = var4.above(); var1.isEmptyBlock(var4) && var4.getY() > 2; var4 = var4.below()) {
      }

      if (!IS_SAND.test(var1.getBlockState(var4))) {
         return false;
      } else {
         int var6;
         int var7;
         for(var6 = -2; var6 <= 2; ++var6) {
            for(var7 = -2; var7 <= 2; ++var7) {
               if (var1.isEmptyBlock(var4.offset(var6, -1, var7)) && var1.isEmptyBlock(var4.offset(var6, -2, var7))) {
                  return false;
               }
            }
         }

         for(var6 = -1; var6 <= 0; ++var6) {
            for(var7 = -2; var7 <= 2; ++var7) {
               for(int var8 = -2; var8 <= 2; ++var8) {
                  var1.setBlock(var4.offset(var7, var6, var8), this.sandstone, 2);
               }
            }
         }

         var1.setBlock(var4, this.water, 2);
         Iterator var9 = Direction.Plane.HORIZONTAL.iterator();

         while(var9.hasNext()) {
            Direction var10 = (Direction)var9.next();
            var1.setBlock(var4.relative(var10), this.water, 2);
         }

         for(var6 = -2; var6 <= 2; ++var6) {
            for(var7 = -2; var7 <= 2; ++var7) {
               if (var6 == -2 || var6 == 2 || var7 == -2 || var7 == 2) {
                  var1.setBlock(var4.offset(var6, 1, var7), this.sandstone, 2);
               }
            }
         }

         var1.setBlock(var4.offset(2, 1, 0), this.sandSlab, 2);
         var1.setBlock(var4.offset(-2, 1, 0), this.sandSlab, 2);
         var1.setBlock(var4.offset(0, 1, 2), this.sandSlab, 2);
         var1.setBlock(var4.offset(0, 1, -2), this.sandSlab, 2);

         for(var6 = -1; var6 <= 1; ++var6) {
            for(var7 = -1; var7 <= 1; ++var7) {
               if (var6 == 0 && var7 == 0) {
                  var1.setBlock(var4.offset(var6, 4, var7), this.sandstone, 2);
               } else {
                  var1.setBlock(var4.offset(var6, 4, var7), this.sandSlab, 2);
               }
            }
         }

         for(var6 = 1; var6 <= 3; ++var6) {
            var1.setBlock(var4.offset(-1, var6, -1), this.sandstone, 2);
            var1.setBlock(var4.offset(-1, var6, 1), this.sandstone, 2);
            var1.setBlock(var4.offset(1, var6, -1), this.sandstone, 2);
            var1.setBlock(var4.offset(1, var6, 1), this.sandstone, 2);
         }

         return true;
      }
   }

   static {
      IS_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
   }
}
