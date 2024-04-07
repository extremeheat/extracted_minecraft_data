package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;

public interface EntityBlock {
   @Nullable
   BlockEntity newBlockEntity(BlockPos var1, BlockState var2);

   @Nullable
   default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return null;
   }

   @Nullable
   default <T extends BlockEntity> GameEventListener getListener(ServerLevel var1, T var2) {
      return var2 instanceof GameEventListener.Provider var3 ? var3.getListener() : null;
   }
}
