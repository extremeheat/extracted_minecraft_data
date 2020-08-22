package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;

public class DecoratedFeature extends Feature {
   public DecoratedFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, DecoratedFeatureConfiguration var5) {
      return var5.decorator.place(var1, var2, var3, var4, var5.feature);
   }

   public String toString() {
      return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getKey(this));
   }
}
