package net.minecraft.world.level.gameevent;

import net.minecraft.core.Holder;
import net.minecraft.world.phys.Vec3;

public interface GameEventListenerRegistry {
   GameEventListenerRegistry NOOP = new GameEventListenerRegistry() {
      @Override
      public boolean isEmpty() {
         return true;
      }

      @Override
      public void register(GameEventListener var1) {
      }

      @Override
      public void unregister(GameEventListener var1) {
      }

      @Override
      public boolean visitInRangeListeners(Holder<GameEvent> var1, Vec3 var2, GameEvent.Context var3, GameEventListenerRegistry.ListenerVisitor var4) {
         return false;
      }
   };

   boolean isEmpty();

   void register(GameEventListener var1);

   void unregister(GameEventListener var1);

   boolean visitInRangeListeners(Holder<GameEvent> var1, Vec3 var2, GameEvent.Context var3, GameEventListenerRegistry.ListenerVisitor var4);

   @FunctionalInterface
   public interface ListenerVisitor {
      void visit(GameEventListener var1, Vec3 var2);
   }
}