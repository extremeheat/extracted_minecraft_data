package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class DiskReplaceFeature extends Feature<DiskConfiguration> {
   public DiskReplaceFeature(Function<Dynamic<?>, ? extends DiskConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, DiskConfiguration var5) {
      if (!var1.getFluidState(var4).is(FluidTags.WATER)) {
         return false;
      } else {
         int var6 = 0;
         int var7 = var3.nextInt(var5.radius - 2) + 2;

         for(int var8 = var4.getX() - var7; var8 <= var4.getX() + var7; ++var8) {
            for(int var9 = var4.getZ() - var7; var9 <= var4.getZ() + var7; ++var9) {
               int var10 = var8 - var4.getX();
               int var11 = var9 - var4.getZ();
               if (var10 * var10 + var11 * var11 <= var7 * var7) {
                  for(int var12 = var4.getY() - var5.ySize; var12 <= var4.getY() + var5.ySize; ++var12) {
                     BlockPos var13 = new BlockPos(var8, var12, var9);
                     BlockState var14 = var1.getBlockState(var13);
                     Iterator var15 = var5.targets.iterator();

                     while(var15.hasNext()) {
                        BlockState var16 = (BlockState)var15.next();
                        if (var16.getBlock() == var14.getBlock()) {
                           var1.setBlock(var13, var5.state, 2);
                           ++var6;
                           break;
                        }
                     }
                  }
               }
            }
         }

         return var6 > 0;
      }
   }
}
