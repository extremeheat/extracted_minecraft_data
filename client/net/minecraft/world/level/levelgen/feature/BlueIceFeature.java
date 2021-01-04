package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.material.Material;

public class BlueIceFeature extends Feature<NoneFeatureConfiguration> {
   public BlueIceFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      if (var4.getY() > var1.getSeaLevel() - 1) {
         return false;
      } else if (var1.getBlockState(var4).getBlock() != Blocks.WATER && var1.getBlockState(var4.below()).getBlock() != Blocks.WATER) {
         return false;
      } else {
         boolean var6 = false;
         Direction[] var7 = Direction.values();
         int var8 = var7.length;

         int var9;
         for(var9 = 0; var9 < var8; ++var9) {
            Direction var10 = var7[var9];
            if (var10 != Direction.DOWN && var1.getBlockState(var4.relative(var10)).getBlock() == Blocks.PACKED_ICE) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            return false;
         } else {
            var1.setBlock(var4, Blocks.BLUE_ICE.defaultBlockState(), 2);

            for(int var18 = 0; var18 < 200; ++var18) {
               var8 = var3.nextInt(5) - var3.nextInt(6);
               var9 = 3;
               if (var8 < 2) {
                  var9 += var8 / 2;
               }

               if (var9 >= 1) {
                  BlockPos var19 = var4.offset(var3.nextInt(var9) - var3.nextInt(var9), var8, var3.nextInt(var9) - var3.nextInt(var9));
                  BlockState var11 = var1.getBlockState(var19);
                  Block var12 = var11.getBlock();
                  if (var11.getMaterial() == Material.AIR || var12 == Blocks.WATER || var12 == Blocks.PACKED_ICE || var12 == Blocks.ICE) {
                     Direction[] var13 = Direction.values();
                     int var14 = var13.length;

                     for(int var15 = 0; var15 < var14; ++var15) {
                        Direction var16 = var13[var15];
                        Block var17 = var1.getBlockState(var19.relative(var16)).getBlock();
                        if (var17 == Blocks.BLUE_ICE) {
                           var1.setBlock(var19, Blocks.BLUE_ICE.defaultBlockState(), 2);
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
