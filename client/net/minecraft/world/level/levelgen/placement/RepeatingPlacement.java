package net.minecraft.world.level.levelgen.placement;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public abstract class RepeatingPlacement extends PlacementModifier {
   public RepeatingPlacement() {
      super();
   }

   protected abstract int count(RandomSource var1, BlockPos var2);

   public Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3) {
      return IntStream.range(0, this.count(var2, var3)).mapToObj((var1x) -> {
         return var3;
      });
   }
}
