package net.minecraft.world.level.levelgen.placement.nether;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class GlowstoneDecorator extends SimpleFeatureDecorator<CountConfiguration> {
   public GlowstoneDecorator(Codec<CountConfiguration> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, CountConfiguration var2, BlockPos var3) {
      return IntStream.range(0, var1.nextInt(var1.nextInt(var2.count().sample(var1)) + 1)).mapToObj((var2x) -> {
         int var3x = var1.nextInt(16) + var3.getX();
         int var4 = var1.nextInt(16) + var3.getZ();
         int var5 = var1.nextInt(120) + 4;
         return new BlockPos(var3x, var5, var4);
      });
   }
}
