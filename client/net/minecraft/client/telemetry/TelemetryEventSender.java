package net.minecraft.client.telemetry;

import java.util.function.Consumer;

@FunctionalInterface
public interface TelemetryEventSender {
   TelemetryEventSender DISABLED = (var0, var1) -> {
   };

   default TelemetryEventSender decorate(Consumer<TelemetryPropertyMap.Builder> var1) {
      return (var2, var3) -> {
         this.send(var2, (var2x) -> {
            var3.accept(var2x);
            var1.accept(var2x);
         });
      };
   }

   void send(TelemetryEventType var1, Consumer<TelemetryPropertyMap.Builder> var2);
}
