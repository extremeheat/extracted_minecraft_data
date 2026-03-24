package net.minecraft.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.ClockNetworkState;
import net.minecraft.world.clock.WorldClock;

public class ClientClockManager implements ClockManager {
   private final Map<Holder<WorldClock>, ClockInstance> clocks = new HashMap();
   private long lastTickGameTime;

   public ClientClockManager() {
      super();
   }

   private ClockInstance getInstance(final Holder<WorldClock> definition) {
      return (ClockInstance)this.clocks.computeIfAbsent(definition, (var0) -> new ClockInstance());
   }

   public void tick(final long gameTime) {
      long gameTimeDelta = gameTime - this.lastTickGameTime;
      this.lastTickGameTime = gameTime;

      for(ClockInstance instance : this.clocks.values()) {
         double newPartialTicks = (double)instance.partialTick + (double)gameTimeDelta * (double)instance.rate;
         long fullTicks = (long)Mth.floor(newPartialTicks);
         instance.partialTick = (float)(newPartialTicks - (double)fullTicks);
         instance.totalTicks += fullTicks;
      }

   }

   public void handleUpdates(final long gameTime, final Map<Holder<WorldClock>, ClockNetworkState> updates) {
      this.tick(gameTime);
      updates.forEach((definition, state) -> {
         ClockInstance clock = this.getInstance(definition);
         clock.totalTicks = state.totalTicks();
         clock.partialTick = state.partialTick();
         clock.rate = state.rate();
      });
   }

   public long getTotalTicks(final Holder<WorldClock> definition) {
      return this.getInstance(definition).totalTicks;
   }

   private static class ClockInstance {
      private long totalTicks;
      private float partialTick;
      private float rate = 1.0F;

      private ClockInstance() {
         super();
      }
   }
}
