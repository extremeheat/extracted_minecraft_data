package net.minecraft.world.level.levelgen.feature.stateproviders;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Serializable;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockStateProvider implements Serializable {
   protected final BlockStateProviderType type;

   protected BlockStateProvider(BlockStateProviderType var1) {
      this.type = var1;
   }

   public abstract BlockState getState(Random var1, BlockPos var2);
}
