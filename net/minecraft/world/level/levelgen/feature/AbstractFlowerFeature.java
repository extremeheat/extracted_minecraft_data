package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public abstract class AbstractFlowerFeature extends Feature {
   public AbstractFlowerFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, FeatureConfiguration var5) {
      BlockState var6 = this.getRandomFlower(var3, var4, var5);
      int var7 = 0;

      for(int var8 = 0; var8 < this.getCount(var5); ++var8) {
         BlockPos var9 = this.getPos(var3, var4, var5);
         if (var1.isEmptyBlock(var9) && var9.getY() < 255 && var6.canSurvive(var1, var9) && this.isValid(var1, var9, var5)) {
            var1.setBlock(var9, var6, 2);
            ++var7;
         }
      }

      return var7 > 0;
   }

   public abstract boolean isValid(LevelAccessor var1, BlockPos var2, FeatureConfiguration var3);

   public abstract int getCount(FeatureConfiguration var1);

   public abstract BlockPos getPos(Random var1, BlockPos var2, FeatureConfiguration var3);

   public abstract BlockState getRandomFlower(Random var1, BlockPos var2, FeatureConfiguration var3);
}
