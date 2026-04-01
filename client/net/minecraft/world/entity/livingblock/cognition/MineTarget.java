package net.minecraft.world.entity.livingblock.cognition;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public record MineTarget(BlockPos pos, Block block) {
   public MineTarget {
      super();
   }
}
