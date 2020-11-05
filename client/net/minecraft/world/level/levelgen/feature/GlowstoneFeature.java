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

public class GlowstoneFeature extends Feature<NoneFeatureConfiguration> {
   public GlowstoneFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      if (!var1.isEmptyBlock(var4)) {
         return false;
      } else {
         BlockState var6 = var1.getBlockState(var4.above());
         if (!var6.is(Blocks.NETHERRACK) && !var6.is(Blocks.BASALT) && !var6.is(Blocks.BLACKSTONE)) {
            return false;
         } else {
            var1.setBlock(var4, Blocks.GLOWSTONE.defaultBlockState(), 2);

            for(int var7 = 0; var7 < 1500; ++var7) {
               BlockPos var8 = var4.offset(var3.nextInt(8) - var3.nextInt(8), -var3.nextInt(12), var3.nextInt(8) - var3.nextInt(8));
               if (var1.getBlockState(var8).isAir()) {
                  int var9 = 0;
                  Direction[] var10 = Direction.values();
                  int var11 = var10.length;

                  for(int var12 = 0; var12 < var11; ++var12) {
                     Direction var13 = var10[var12];
                     if (var1.getBlockState(var8.relative(var13)).is(Blocks.GLOWSTONE)) {
                        ++var9;
                     }

                     if (var9 > 1) {
                        break;
                     }
                  }

                  if (var9 == 1) {
                     var1.setBlock(var8, Blocks.GLOWSTONE.defaultBlockState(), 2);
                  }
               }
            }

            return true;
         }
      }
   }
}
