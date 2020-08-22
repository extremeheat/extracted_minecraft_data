package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public abstract class SimpleFeatureDecorator extends FeatureDecorator {
   public SimpleFeatureDecorator(Function var1) {
      super(var1);
   }

   public final Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, DecoratorConfiguration var4, BlockPos var5) {
      return this.place(var3, var4, var5);
   }

   protected abstract Stream place(Random var1, DecoratorConfiguration var2, BlockPos var3);
}
