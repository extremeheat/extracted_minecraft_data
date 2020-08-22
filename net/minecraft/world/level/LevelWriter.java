package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

public interface LevelWriter {
   boolean setBlock(BlockPos var1, BlockState var2, int var3);

   boolean removeBlock(BlockPos var1, boolean var2);

   default boolean destroyBlock(BlockPos var1, boolean var2) {
      return this.destroyBlock(var1, var2, (Entity)null);
   }

   boolean destroyBlock(BlockPos var1, boolean var2, @Nullable Entity var3);

   default boolean addFreshEntity(Entity var1) {
      return false;
   }
}
