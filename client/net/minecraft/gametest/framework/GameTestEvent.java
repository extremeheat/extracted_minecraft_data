package net.minecraft.gametest.framework;

import org.jspecify.annotations.Nullable;

class GameTestEvent {
   public final @Nullable Long expectedDelay;
   public final @Nullable Long minimumDelay;
   public final Runnable assertion;

   private GameTestEvent(final @Nullable Long expectedDelay, final @Nullable Long minimumDelay, final Runnable assertion) {
      super();
      this.expectedDelay = expectedDelay;
      this.minimumDelay = minimumDelay;
      this.assertion = assertion;
   }

   static GameTestEvent create(final Runnable runnable) {
      return new GameTestEvent((Long)null, (Long)null, runnable);
   }

   static GameTestEvent create(final long expectedTick, final Runnable runnable) {
      return new GameTestEvent(expectedTick, (Long)null, runnable);
   }

   static GameTestEvent createWithMinimumDelay(final long minimumDelay, final Runnable runnable) {
      return new GameTestEvent((Long)null, minimumDelay, runnable);
   }
}
