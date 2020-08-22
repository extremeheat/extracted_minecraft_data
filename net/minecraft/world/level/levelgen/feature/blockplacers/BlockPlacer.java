package net.minecraft.world.level.levelgen.feature.blockplacers;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Serializable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockPlacer implements Serializable {
   protected final BlockPlacerType type;

   protected BlockPlacer(BlockPlacerType var1) {
      this.type = var1;
   }

   public abstract void place(LevelAccessor var1, BlockPos var2, BlockState var3, Random var4);
}
