package net.minecraft.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.ClockState;
import net.minecraft.world.clock.WorldClock;

public class ClientClockManager implements ClockManager {
   private final Map<Holder<WorldClock>, ClockInstance> clocks = new HashMap();
   private long lastTickGameTime;

   public ClientClockManager() {
      super();
   }

   private ClockInstance getInstance(final Holder<WorldClock> definition) {
      return (ClockInstance)this.clocks.computeIfAbsent(definition, (d) -> new ClockInstance());
   }

   public void tick(final long gameTime) {
      int gameTimeDelta = Math.toIntExact(gameTime - this.lastTickGameTime);
      this.lastTickGameTime = gameTime;

      for(ClockInstance instance : this.clocks.values()) {
         if (!instance.paused) {
            instance.totalTicks += (long)gameTimeDelta;
         }
      }

   }

   public void handleUpdates(final long gameTime, final Map<Holder<WorldClock>, ClockState> updates) {
      this.tick(gameTime);
      updates.forEach((definition, state) -> {
         ClockInstance clock = this.getInstance(definition);
         clock.totalTicks = state.totalTicks();
         clock.paused = state.paused();
      });
   }

   public long getTotalTicks(final Holder<WorldClock> definition) {
      return this.getInstance(definition).totalTicks;
   }

   private static class ClockInstance {
      private long totalTicks;
      private boolean paused;

      private ClockInstance() {
         super();
      }
   }
}
