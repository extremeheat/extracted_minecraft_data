package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class DepthAverageDecorator extends SimpleFeatureDecorator<DepthAverageConfigation> {
   public DepthAverageDecorator(Codec<DepthAverageConfigation> var1) {
      super(var1);
   }

   public Stream<BlockPos> place(Random var1, DepthAverageConfigation var2, BlockPos var3) {
      int var4 = var2.baseline;
      int var5 = var2.spread;
      int var6 = var3.getX();
      int var7 = var3.getZ();
      int var8 = var1.nextInt(var5) + var1.nextInt(var5) - var5 + var4;
      return Stream.of(new BlockPos(var6, var8, var7));
   }
}
