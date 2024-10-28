package net.minecraft.world.level.gameevent;

import net.minecraft.core.Holder;
import net.minecraft.world.phys.Vec3;

public interface GameEventListenerRegistry {
   GameEventListenerRegistry NOOP = new GameEventListenerRegistry() {
      public boolean isEmpty() {
         return true;
      }

      public void register(GameEventListener var1) {
      }

      public void unregister(GameEventListener var1) {
      }

      public boolean visitInRangeListeners(Holder<GameEvent> var1, Vec3 var2, GameEvent.Context var3, ListenerVisitor var4) {
         return false;
      }
   };

   boolean isEmpty();

   void register(GameEventListener var1);

   void unregister(GameEventListener var1);

   boolean visitInRangeListeners(Holder<GameEvent> var1, Vec3 var2, GameEvent.Context var3, ListenerVisitor var4);

   @FunctionalInterface
   public interface ListenerVisitor {
      void visit(GameEventListener var1, Vec3 var2);
   }
}
