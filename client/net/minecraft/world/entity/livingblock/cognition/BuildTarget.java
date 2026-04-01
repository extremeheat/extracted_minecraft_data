package net.minecraft.world.entity.livingblock.cognition;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record BuildTarget(BlockPos pos, Direction direction) {
   public BuildTarget {
      super();
   }
}
