package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class GrassFeature extends Feature<GrassConfiguration> {
   public GrassFeature(Function<Dynamic<?>, ? extends GrassConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, GrassConfiguration var5) {
      for(BlockState var6 = var1.getBlockState(var4); (var6.isAir() || var6.is(BlockTags.LEAVES)) && var4.getY() > 0; var6 = var1.getBlockState(var4)) {
         var4 = var4.below();
      }

      int var7 = 0;

      for(int var8 = 0; var8 < 128; ++var8) {
         BlockPos var9 = var4.offset(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.isEmptyBlock(var9) && var5.state.canSurvive(var1, var9)) {
            var1.setBlock(var9, var5.state, 2);
            ++var7;
         }
      }

      return var7 > 0;
   }
}
