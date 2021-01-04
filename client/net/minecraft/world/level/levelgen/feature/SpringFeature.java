package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class SpringFeature extends Feature<SpringConfiguration> {
   public SpringFeature(Function<Dynamic<?>, ? extends SpringConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, SpringConfiguration var5) {
      if (!Block.equalsStone(var1.getBlockState(var4.above()).getBlock())) {
         return false;
      } else if (!Block.equalsStone(var1.getBlockState(var4.below()).getBlock())) {
         return false;
      } else {
         BlockState var6 = var1.getBlockState(var4);
         if (!var6.isAir() && !Block.equalsStone(var6.getBlock())) {
            return false;
         } else {
            int var7 = 0;
            int var8 = 0;
            if (Block.equalsStone(var1.getBlockState(var4.west()).getBlock())) {
               ++var8;
            }

            if (Block.equalsStone(var1.getBlockState(var4.east()).getBlock())) {
               ++var8;
            }

            if (Block.equalsStone(var1.getBlockState(var4.north()).getBlock())) {
               ++var8;
            }

            if (Block.equalsStone(var1.getBlockState(var4.south()).getBlock())) {
               ++var8;
            }

            int var9 = 0;
            if (var1.isEmptyBlock(var4.west())) {
               ++var9;
            }

            if (var1.isEmptyBlock(var4.east())) {
               ++var9;
            }

            if (var1.isEmptyBlock(var4.north())) {
               ++var9;
            }

            if (var1.isEmptyBlock(var4.south())) {
               ++var9;
            }

            if (var8 == 3 && var9 == 1) {
               var1.setBlock(var4, var5.state.createLegacyBlock(), 2);
               var1.getLiquidTicks().scheduleTick(var4, var5.state.getType(), 0);
               ++var7;
            }

            return var7 > 0;
         }
      }
   }
}
