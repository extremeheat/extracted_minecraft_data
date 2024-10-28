package net.minecraft.world.level.levelgen.placement;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public abstract class PlacementFilter extends PlacementModifier {
   public PlacementFilter() {
      super();
   }

   public final Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3) {
      return this.shouldPlace(var1, var2, var3) ? Stream.of(var3) : Stream.of();
   }

   protected abstract boolean shouldPlace(PlacementContext var1, RandomSource var2, BlockPos var3);
}
