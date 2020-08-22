package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureRadiusConfiguration;

public class IcePatchFeature extends Feature {
   private final Block block;

   public IcePatchFeature(Function var1) {
      super(var1);
      this.block = Blocks.PACKED_ICE;
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, FeatureRadiusConfiguration var5) {
      while(var1.isEmptyBlock(var4) && var4.getY() > 2) {
         var4 = var4.below();
      }

      if (var1.getBlockState(var4).getBlock() != Blocks.SNOW_BLOCK) {
         return false;
      } else {
         int var6 = var3.nextInt(var5.radius) + 2;
         boolean var7 = true;

         for(int var8 = var4.getX() - var6; var8 <= var4.getX() + var6; ++var8) {
            for(int var9 = var4.getZ() - var6; var9 <= var4.getZ() + var6; ++var9) {
               int var10 = var8 - var4.getX();
               int var11 = var9 - var4.getZ();
               if (var10 * var10 + var11 * var11 <= var6 * var6) {
                  for(int var12 = var4.getY() - 1; var12 <= var4.getY() + 1; ++var12) {
                     BlockPos var13 = new BlockPos(var8, var12, var9);
                     Block var14 = var1.getBlockState(var13).getBlock();
                     if (isDirt(var14) || var14 == Blocks.SNOW_BLOCK || var14 == Blocks.ICE) {
                        var1.setBlock(var13, this.block.defaultBlockState(), 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
