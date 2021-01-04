package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class ChancePassthroughDecorator extends SimpleFeatureDecorator<DecoratorChance> {
   public ChancePassthroughDecorator(Function<Dynamic<?>, ? extends DecoratorChance> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, DecoratorChance var2, BlockPos var3) {
      return var1.nextFloat() < 1.0F / (float)var2.chance ? Stream.of(var3) : Stream.empty();
   }
}
