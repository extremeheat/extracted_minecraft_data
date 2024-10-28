package net.minecraft.client.telemetry.events;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;

public class WorldUnloadEvent {
   private static final int NOT_TRACKING_TIME = -1;
   private Optional<Instant> worldLoadedTime = Optional.empty();
   private long totalTicks;
   private long lastGameTime;

   public WorldUnloadEvent() {
      super();
   }

   public void onPlayerInfoReceived() {
      this.lastGameTime = -1L;
      if (this.worldLoadedTime.isEmpty()) {
         this.worldLoadedTime = Optional.of(Instant.now());
      }

   }

   public void setTime(long var1) {
      if (this.lastGameTime != -1L) {
         this.totalTicks += Math.max(0L, var1 - this.lastGameTime);
      }

      this.lastGameTime = var1;
   }

   private int getTimeInSecondsSinceLoad(Instant var1) {
      Duration var2 = Duration.between(var1, Instant.now());
      return (int)var2.toSeconds();
   }

   public void send(TelemetryEventSender var1) {
      this.worldLoadedTime.ifPresent((var2) -> {
         var1.send(TelemetryEventType.WORLD_UNLOADED, (var2x) -> {
            var2x.put(TelemetryProperty.SECONDS_SINCE_LOAD, this.getTimeInSecondsSinceLoad(var2));
            var2x.put(TelemetryProperty.TICKS_SINCE_LOAD, (int)this.totalTicks);
         });
      });
   }
}
