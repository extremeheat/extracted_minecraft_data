package net.minecraft.world.level.levelgen.placement;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public abstract class RepeatingPlacement extends PlacementModifier {
   public RepeatingPlacement() {
      super();
   }

   protected abstract int count(Random var1, BlockPos var2);

   public Stream<BlockPos> getPositions(PlacementContext var1, Random var2, BlockPos var3) {
      return IntStream.range(0, this.count(var2, var3)).mapToObj((var1x) -> {
         return var3;
      });
   }
}
