package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GlowstoneFeature extends Feature {
   public GlowstoneFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      if (!var1.isEmptyBlock(var4)) {
         return false;
      } else if (var1.getBlockState(var4.above()).getBlock() != Blocks.NETHERRACK) {
         return false;
      } else {
         var1.setBlock(var4, Blocks.GLOWSTONE.defaultBlockState(), 2);

         for(int var6 = 0; var6 < 1500; ++var6) {
            BlockPos var7 = var4.offset(var3.nextInt(8) - var3.nextInt(8), -var3.nextInt(12), var3.nextInt(8) - var3.nextInt(8));
            if (var1.getBlockState(var7).isAir()) {
               int var8 = 0;
               Direction[] var9 = Direction.values();
               int var10 = var9.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  Direction var12 = var9[var11];
                  if (var1.getBlockState(var7.relative(var12)).getBlock() == Blocks.GLOWSTONE) {
                     ++var8;
                  }

                  if (var8 > 1) {
                     break;
                  }
               }

               if (var8 == 1) {
                  var1.setBlock(var7, Blocks.GLOWSTONE.defaultBlockState(), 2);
               }
            }
         }

         return true;
      }
   }
}
