package net.minecraft.client.telemetry.events;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.mojang.logging.LogUtils;
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
   private final Map<TelemetryProperty<GameLoadTimesEvent.Measurement>, Stopwatch> measurements = new HashMap<>();
   private OptionalLong bootstrapTime = OptionalLong.empty();

   protected GameLoadTimesEvent(Ticker var1) {
      super();
      this.timeSource = var1;
   }

   public synchronized void beginStep(TelemetryProperty<GameLoadTimesEvent.Measurement> var1) {
      this.beginStep(var1, (Function<TelemetryProperty<GameLoadTimesEvent.Measurement>, Stopwatch>)(var1x -> Stopwatch.createStarted(this.timeSource)));
   }

   public synchronized void beginStep(TelemetryProperty<GameLoadTimesEvent.Measurement> var1, Stopwatch var2) {
      this.beginStep(var1, (Function<TelemetryProperty<GameLoadTimesEvent.Measurement>, Stopwatch>)(var1x -> var2));
   }

   private synchronized void beginStep(
      TelemetryProperty<GameLoadTimesEvent.Measurement> var1, Function<TelemetryProperty<GameLoadTimesEvent.Measurement>, Stopwatch> var2
   ) {
      this.measurements.computeIfAbsent(var1, var2);
   }

   public synchronized void endStep(TelemetryProperty<GameLoadTimesEvent.Measurement> var1) {
      Stopwatch var2 = this.measurements.get(var1);
      if (var2 == null) {
         LOGGER.warn("Attempted to end step for {} before starting it", var1.id());
      } else {
         if (var2.isRunning()) {
            var2.stop();
         }
      }
   }

   public void send(TelemetryEventSender var1) {
      var1.send(
         TelemetryEventType.GAME_LOAD_TIMES,
         var1x -> {
            synchronized (this) {
               this.measurements
                  .forEach(
                     (var1xx, var2) -> {
                        if (!var2.isRunning()) {
                           long var3 = var2.elapsed(TimeUnit.MILLISECONDS);
                           var1x.put((TelemetryProperty<GameLoadTimesEvent.Measurement>)var1xx, new GameLoadTimesEvent.Measurement((int)var3));
                        } else {
                           LOGGER.warn(
                              "Measurement {} was discarded since it was still ongoing when the event {} was sent.",
                              var1xx.id(),
                              TelemetryEventType.GAME_LOAD_TIMES.id()
                           );
                        }
                     }
                  );
               this.bootstrapTime.ifPresent(var1xx -> var1x.put(TelemetryProperty.LOAD_TIME_BOOTSTRAP_MS, new GameLoadTimesEvent.Measurement((int)var1xx)));
               this.measurements.clear();
            }
         }
      );
   }

   public synchronized void setBootstrapTime(long var1) {
      this.bootstrapTime = OptionalLong.of(var1);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
