package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Material;

public class BlueIceFeature extends Feature<NoneFeatureConfiguration> {
   public BlueIceFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      if (var4.getY() > var1.getSeaLevel() - 1) {
         return false;
      } else if (!var1.getBlockState(var4).is(Blocks.WATER) && !var1.getBlockState(var4.below()).is(Blocks.WATER)) {
         return false;
      } else {
         boolean var6 = false;
         Direction[] var7 = Direction.values();
         int var8 = var7.length;

         int var9;
         for(var9 = 0; var9 < var8; ++var9) {
            Direction var10 = var7[var9];
            if (var10 != Direction.DOWN && var1.getBlockState(var4.relative(var10)).is(Blocks.PACKED_ICE)) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            return false;
         } else {
            var1.setBlock(var4, Blocks.BLUE_ICE.defaultBlockState(), 2);

            for(int var17 = 0; var17 < 200; ++var17) {
               var8 = var3.nextInt(5) - var3.nextInt(6);
               var9 = 3;
               if (var8 < 2) {
                  var9 += var8 / 2;
               }

               if (var9 >= 1) {
                  BlockPos var18 = var4.offset(var3.nextInt(var9) - var3.nextInt(var9), var8, var3.nextInt(var9) - var3.nextInt(var9));
                  BlockState var11 = var1.getBlockState(var18);
                  if (var11.getMaterial() == Material.AIR || var11.is(Blocks.WATER) || var11.is(Blocks.PACKED_ICE) || var11.is(Blocks.ICE)) {
                     Direction[] var12 = Direction.values();
                     int var13 = var12.length;

                     for(int var14 = 0; var14 < var13; ++var14) {
                        Direction var15 = var12[var14];
                        BlockState var16 = var1.getBlockState(var18.relative(var15));
                        if (var16.is(Blocks.BLUE_ICE)) {
                           var1.setBlock(var18, Blocks.BLUE_ICE.defaultBlockState(), 2);
                           break;
                        }
                     }
                  }
               }
            }

            return true;
         }
      }
   }
}
