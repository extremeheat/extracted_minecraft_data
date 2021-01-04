package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class BushFeature extends Feature<BushConfiguration> {
   public BushFeature(Function<Dynamic<?>, ? extends BushConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, BushConfiguration var5) {
      int var6 = 0;
      BlockState var7 = var5.state;

      for(int var8 = 0; var8 < 64; ++var8) {
         BlockPos var9 = var4.offset(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.isEmptyBlock(var9) && (!var1.getDimension().isHasCeiling() || var9.getY() < 255) && var7.canSurvive(var1, var9)) {
            var1.setBlock(var9, var7, 2);
            ++var6;
         }
      }

      return var6 > 0;
   }
}
