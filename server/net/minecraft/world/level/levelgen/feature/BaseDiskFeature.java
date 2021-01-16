package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;

public class BaseDiskFeature extends Feature<DiskConfiguration> {
   public BaseDiskFeature(Codec<DiskConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, DiskConfiguration var5) {
      boolean var6 = false;
      int var7 = var5.radius.sample(var3);

      for(int var8 = var4.getX() - var7; var8 <= var4.getX() + var7; ++var8) {
         for(int var9 = var4.getZ() - var7; var9 <= var4.getZ() + var7; ++var9) {
            int var10 = var8 - var4.getX();
            int var11 = var9 - var4.getZ();
            if (var10 * var10 + var11 * var11 <= var7 * var7) {
               for(int var12 = var4.getY() - var5.halfHeight; var12 <= var4.getY() + var5.halfHeight; ++var12) {
                  BlockPos var13 = new BlockPos(var8, var12, var9);
                  Block var14 = var1.getBlockState(var13).getBlock();
                  Iterator var15 = var5.targets.iterator();

                  while(var15.hasNext()) {
                     BlockState var16 = (BlockState)var15.next();
                     if (var16.is(var14)) {
                        var1.setBlock(var13, var5.state, 2);
                        var6 = true;
                        break;
                     }
                  }
               }
            }
         }
      }

      return var6;
   }
}
