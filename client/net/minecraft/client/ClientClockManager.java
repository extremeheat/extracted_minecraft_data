package net.minecraft.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.clock.ClockInstance;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.ClockNetworkState;
import net.minecraft.world.clock.WorldClock;

public class ClientClockManager implements ClockManager {
   private final Map<Holder<WorldClock>, ClientClockInstance> clocks = new HashMap();
   private long lastTickGameTime;

   public ClientClockManager() {
      super();
   }

   public ClientClockInstance getInstance(final Holder<WorldClock> definition) {
      return (ClientClockInstance)this.clocks.computeIfAbsent(definition, (var0) -> new ClientClockInstance());
   }

   public void tick(final long gameTime) {
      long gameTimeDelta = gameTime - this.lastTickGameTime;
      this.lastTickGameTime = gameTime;

      for(ClientClockInstance instance : this.clocks.values()) {
         double newPartialTicks = (double)instance.partialTick + (double)gameTimeDelta * (double)instance.rate;
         long fullTicks = (long)Mth.floor(newPartialTicks);
         instance.partialTick = (float)(newPartialTicks - (double)fullTicks);
         instance.totalTicks += fullTicks;
      }

   }

   public void handleUpdates(final long gameTime, final Map<Holder<WorldClock>, ClockNetworkState> updates) {
      this.tick(gameTime);
      updates.forEach((definition, state) -> {
         ClientClockInstance clock = this.getInstance(definition);
         clock.totalTicks = state.totalTicks();
         clock.partialTick = state.partialTick();
         clock.rate = state.rate();
      });
   }

   public static class ClientClockInstance implements ClockInstance {
      private long totalTicks;
      private float partialTick;
      private float rate = 1.0F;

      public ClientClockInstance() {
         super();
      }

      public long totalTicks() {
         return this.totalTicks;
      }

      public float partialTick() {
         return this.partialTick;
      }

      public float rate() {
         return this.rate;
      }

      public boolean isPaused() {
         return this.rate == 0.0F;
      }
   }
}
