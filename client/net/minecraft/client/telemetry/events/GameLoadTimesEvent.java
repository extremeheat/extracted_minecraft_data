package net.minecraft.client.telemetry.events;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import org.slf4j.Logger;

public class GameLoadTimesEvent {
   public static final GameLoadTimesEvent INSTANCE = new GameLoadTimesEvent(Ticker.systemTicker());
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Ticker timeSource;
   private final Map<TelemetryProperty<Measurement>, Stopwatch> measurements = new HashMap();
   private OptionalLong bootstrapTime = OptionalLong.empty();

   protected GameLoadTimesEvent(final Ticker timeSource) {
      super();
      this.timeSource = timeSource;
   }

   public synchronized void beginStep(final TelemetryProperty<Measurement> property) {
      this.beginStep(property, (Function)((p) -> Stopwatch.createStarted(this.timeSource)));
   }

   public synchronized void beginStep(final TelemetryProperty<Measurement> property, final Stopwatch measurement) {
      this.beginStep(property, (Function)((p) -> measurement));
   }

   private synchronized void beginStep(final TelemetryProperty<Measurement> property, final Function<TelemetryProperty<Measurement>, Stopwatch> measurement) {
      this.measurements.computeIfAbsent(property, measurement);
   }

   public synchronized void endStep(final TelemetryProperty<Measurement> property) {
      Stopwatch stepMeasurement = (Stopwatch)this.measurements.get(property);
      if (stepMeasurement == null) {
         LOGGER.warn("Attempted to end step for {} before starting it", property.id());
      } else {
         if (stepMeasurement.isRunning()) {
            stepMeasurement.stop();
         }

      }
   }

   public void send(final TelemetryEventSender eventSender) {
      eventSender.send(TelemetryEventType.GAME_LOAD_TIMES, (properties) -> {
         synchronized(this) {
            this.measurements.forEach((key, stepMeasurement) -> {
               if (!stepMeasurement.isRunning()) {
                  long elapsed = stepMeasurement.elapsed(TimeUnit.MILLISECONDS);
                  properties.put(key, new Measurement((int)elapsed));
               } else {
                  LOGGER.warn("Measurement {} was discarded since it was still ongoing when the event {} was sent.", key.id(), TelemetryEventType.GAME_LOAD_TIMES.id());
               }

            });
            this.bootstrapTime.ifPresent((duration) -> properties.put(TelemetryProperty.LOAD_TIME_BOOTSTRAP_MS, new Measurement((int)duration)));
            this.measurements.clear();
         }
      });
   }

   public synchronized void setBootstrapTime(final long duration) {
      this.bootstrapTime = OptionalLong.of(duration);
   }

   public static record Measurement(int millis) {
      public static final Codec<Measurement> CODEC;

      public Measurement {
         super();
      }

      static {
         CODEC = Codec.INT.xmap(Measurement::new, (o) -> o.millis);
      }
   }
}
