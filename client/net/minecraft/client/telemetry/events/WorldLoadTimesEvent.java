package net.minecraft.client.telemetry.events;

import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;

public class WorldLoadTimesEvent {
   private final boolean newWorld;
   @Nullable
   private final Duration worldLoadDuration;

   public WorldLoadTimesEvent(boolean var1, @Nullable Duration var2) {
      super();
      this.worldLoadDuration = var2;
      this.newWorld = var1;
   }

   public void send(TelemetryEventSender var1) {
      if (this.worldLoadDuration != null) {
         var1.send(TelemetryEventType.WORLD_LOAD_TIMES, (var1x) -> {
            var1x.put(TelemetryProperty.WORLD_LOAD_TIME_MS, (int)this.worldLoadDuration.toMillis());
            var1x.put(TelemetryProperty.NEW_WORLD, this.newWorld);
         });
      }

   }
}
