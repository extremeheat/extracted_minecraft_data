package net.minecraft.world.level.levelgen.placement.nether;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.ChanceRangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class ChanceRangeDecorator extends SimpleFeatureDecorator {
   public ChanceRangeDecorator(Function var1) {
      super(var1);
   }

   public Stream place(Random var1, ChanceRangeDecoratorConfiguration var2, BlockPos var3) {
      if (var1.nextFloat() < var2.chance) {
         int var4 = var1.nextInt(16) + var3.getX();
         int var5 = var1.nextInt(16) + var3.getZ();
         int var6 = var1.nextInt(var2.top - var2.topOffset) + var2.bottomOffset;
         return Stream.of(new BlockPos(var4, var6, var5));
      } else {
         return Stream.empty();
      }
   }
}
