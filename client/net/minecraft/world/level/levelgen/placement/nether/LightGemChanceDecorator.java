package net.minecraft.world.level.levelgen.placement.nether;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecoratorFrequency;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class LightGemChanceDecorator extends SimpleFeatureDecorator<DecoratorFrequency> {
   public LightGemChanceDecorator(Function<Dynamic<?>, ? extends DecoratorFrequency> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, DecoratorFrequency var2, BlockPos var3) {
      return IntStream.range(0, var1.nextInt(var1.nextInt(var2.count) + 1)).mapToObj((var2x) -> {
         int var3x = var1.nextInt(16);
         int var4 = var1.nextInt(120) + 4;
         int var5 = var1.nextInt(16);
         return var3.offset(var3x, var4, var5);
      });
   }
}
