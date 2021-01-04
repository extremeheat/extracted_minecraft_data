package net.minecraft.world.level.levelgen.placement.nether;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.DecoratorChanceRange;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class ChanceRangeDecorator extends SimpleFeatureDecorator<DecoratorChanceRange> {
   public ChanceRangeDecorator(Function<Dynamic<?>, ? extends DecoratorChanceRange> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, DecoratorChanceRange var2, BlockPos var3) {
      if (var1.nextFloat() < var2.chance) {
         int var4 = var1.nextInt(16);
         int var5 = var1.nextInt(var2.top - var2.topOffset) + var2.bottomOffset;
         int var6 = var1.nextInt(16);
         return Stream.of(var3.offset(var4, var5, var6));
      } else {
         return Stream.empty();
      }
   }
}
