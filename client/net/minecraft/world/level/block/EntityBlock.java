package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.jspecify.annotations.Nullable;

public interface EntityBlock {
   @Nullable BlockEntity newBlockEntity(BlockPos var1, BlockState var2);

   default <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return null;
   }

   default <T extends BlockEntity> @Nullable GameEventListener getListener(ServerLevel var1, T var2) {
      if (var2 instanceof GameEventListener.Provider var3) {
         return var3.getListener();
      } else {
         return null;
      }
   }
}
