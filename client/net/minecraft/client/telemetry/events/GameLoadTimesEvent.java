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

   protected GameLoadTimesEvent(Ticker var1) {
      super();
      this.timeSource = var1;
   }

   public synchronized void beginStep(TelemetryProperty<Measurement> var1) {
      this.beginStep(var1, (var1x) -> {
         return Stopwatch.createStarted(this.timeSource);
      });
   }

   public synchronized void beginStep(TelemetryProperty<Measurement> var1, Stopwatch var2) {
      this.beginStep(var1, (var1x) -> {
         return var2;
      });
   }

   private synchronized void beginStep(TelemetryProperty<Measurement> var1, Function<TelemetryProperty<Measurement>, Stopwatch> var2) {
      this.measurements.computeIfAbsent(var1, var2);
   }

   public synchronized void endStep(TelemetryProperty<Measurement> var1) {
      Stopwatch var2 = (Stopwatch)this.measurements.get(var1);
      if (var2 == null) {
         LOGGER.warn("Attempted to end step for {} before starting it", var1.id());
      } else {
         if (var2.isRunning()) {
            var2.stop();
         }

      }
   }

   public void send(TelemetryEventSender var1) {
      var1.send(TelemetryEventType.GAME_LOAD_TIMES, (var1x) -> {
         synchronized(this) {
            this.measurements.forEach((var1, var2) -> {
               if (!var2.isRunning()) {
                  long var3 = var2.elapsed(TimeUnit.MILLISECONDS);
                  var1x.put(var1, new Measurement((int)var3));
               } else {
                  LOGGER.warn("Measurement {} was discarded since it was still ongoing when the event {} was sent.", var1.id(), TelemetryEventType.GAME_LOAD_TIMES.id());
               }

            });
            this.bootstrapTime.ifPresent((var1) -> {
               var1x.put(TelemetryProperty.LOAD_TIME_BOOTSTRAP_MS, new Measurement((int)var1));
            });
            this.measurements.clear();
         }
      });
   }

   public synchronized void setBootstrapTime(long var1) {
      this.bootstrapTime = OptionalLong.of(var1);
   }

   public static record Measurement(int millis) {
      public static final Codec<Measurement> CODEC;

      public Measurement(int var1) {
         super();
         this.millis = var1;
      }

      public int millis() {
         return this.millis;
      }

      static {
         CODEC = Codec.INT.xmap(Measurement::new, (var0) -> {
            return var0.millis;
         });
      }
   }
}
