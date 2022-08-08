package net.minecraft.world.level.gameevent;

import java.util.function.BiConsumer;
import net.minecraft.world.phys.Vec3;

public interface GameEventDispatcher {
   GameEventDispatcher NOOP = new GameEventDispatcher() {
      public boolean isEmpty() {
         return true;
      }

      public void register(GameEventListener var1) {
      }

      public void unregister(GameEventListener var1) {
      }

      public boolean walkListeners(GameEvent var1, Vec3 var2, GameEvent.Context var3, BiConsumer<GameEventListener, Vec3> var4) {
         return false;
      }
   };

   boolean isEmpty();

   void register(GameEventListener var1);

   void unregister(GameEventListener var1);

   boolean walkListeners(GameEvent var1, Vec3 var2, GameEvent.Context var3, BiConsumer<GameEventListener, Vec3> var4);
}
