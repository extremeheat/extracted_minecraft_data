package net.minecraft.world.level.gameevent;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public interface GameEventDispatcher {
   GameEventDispatcher NOOP = new GameEventDispatcher() {
      public boolean isEmpty() {
         return true;
      }

      public void register(GameEventListener var1) {
      }

      public void unregister(GameEventListener var1) {
      }

      public void post(GameEvent var1, @Nullable Entity var2, BlockPos var3) {
      }
   };

   boolean isEmpty();

   void register(GameEventListener var1);

   void unregister(GameEventListener var1);

   void post(GameEvent var1, @Nullable Entity var2, BlockPos var3);
}
