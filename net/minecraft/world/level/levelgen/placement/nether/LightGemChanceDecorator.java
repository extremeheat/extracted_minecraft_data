package net.minecraft.world.level.levelgen.placement.nether;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.FrequencyDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class LightGemChanceDecorator extends SimpleFeatureDecorator {
   public LightGemChanceDecorator(Function var1) {
      super(var1);
   }

   public Stream place(Random var1, FrequencyDecoratorConfiguration var2, BlockPos var3) {
      return IntStream.range(0, var1.nextInt(var1.nextInt(var2.count) + 1)).mapToObj((var2x) -> {
         int var3x = var1.nextInt(16) + var3.getX();
         int var4 = var1.nextInt(16) + var3.getZ();
         int var5 = var1.nextInt(120) + 4;
         return new BlockPos(var3x, var5, var4);
      });
   }
}
