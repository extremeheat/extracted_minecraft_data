package net.minecraft.gametest.framework;

import javax.annotation.Nullable;

class GameTestEvent {
   @Nullable
   public final Long expectedDelay;
   public final Runnable assertion;

   private GameTestEvent(@Nullable Long var1, Runnable var2) {
      super();
      this.expectedDelay = var1;
      this.assertion = var2;
   }

   static GameTestEvent create(Runnable var0) {
      return new GameTestEvent((Long)null, var0);
   }

   static GameTestEvent create(long var0, Runnable var2) {
      return new GameTestEvent(var0, var2);
   }
}
