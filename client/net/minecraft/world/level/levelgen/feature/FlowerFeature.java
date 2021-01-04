package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public abstract class FlowerFeature extends Feature<NoneFeatureConfiguration> {
   public FlowerFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1, false);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      BlockState var6 = this.getRandomFlower(var3, var4);
      int var7 = 0;

      for(int var8 = 0; var8 < 64; ++var8) {
         BlockPos var9 = var4.offset(var3.nextInt(8) - var3.nextInt(8), var3.nextInt(4) - var3.nextInt(4), var3.nextInt(8) - var3.nextInt(8));
         if (var1.isEmptyBlock(var9) && var9.getY() < 255 && var6.canSurvive(var1, var9)) {
            var1.setBlock(var9, var6, 2);
            ++var7;
         }
      }

      return var7 > 0;
   }

   public abstract BlockState getRandomFlower(Random var1, BlockPos var2);
}
