package net.minecraft.world.level.gameevent;

import net.minecraft.core.Holder;
import net.minecraft.world.phys.Vec3;

public interface GameEventListenerRegistry {
   GameEventListenerRegistry NOOP = new GameEventListenerRegistry() {
      public boolean isEmpty() {
         return true;
      }

      public void register(final GameEventListener listener) {
      }

      public void unregister(final GameEventListener listener) {
      }

      public boolean visitInRangeListeners(final Holder<GameEvent> event, final Vec3 sourcePosition, final GameEvent.Context context, final ListenerVisitor action) {
         return false;
      }
   };

   boolean isEmpty();

   void register(GameEventListener listener);

   void unregister(GameEventListener listener);

   boolean visitInRangeListeners(Holder<GameEvent> event, Vec3 sourcePosition, GameEvent.Context context, ListenerVisitor action);

   @FunctionalInterface
   public interface ListenerVisitor {
      void visit(GameEventListener listener, Vec3 position);
   }
}
