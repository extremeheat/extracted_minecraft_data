package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public abstract class SimpleFeatureDecorator<DC extends DecoratorConfiguration> extends FeatureDecorator<DC> {
   public SimpleFeatureDecorator(Function<Dynamic<?>, ? extends DC> var1) {
      super(var1);
   }

   public final Stream<BlockPos> getPositions(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, DC var4, BlockPos var5) {
      return this.place(var3, var4, var5);
   }

   protected abstract Stream<BlockPos> place(Random var1, DC var2, BlockPos var3);
}
