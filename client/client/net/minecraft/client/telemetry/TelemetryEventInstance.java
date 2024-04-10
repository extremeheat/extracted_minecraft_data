package net.minecraft.client.telemetry;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.serialization.Codec;

public record TelemetryEventInstance(TelemetryEventType type, TelemetryPropertyMap properties) {
   public static final Codec<TelemetryEventInstance> CODEC = TelemetryEventType.CODEC.dispatchStable(TelemetryEventInstance::type, TelemetryEventType::codec);

   public TelemetryEventInstance(TelemetryEventType type, TelemetryPropertyMap properties) {
      super();
      properties.propertySet().forEach(var1 -> {
         if (!typex.contains((TelemetryProperty<?>)var1)) {
            throw new IllegalArgumentException("Property '" + var1.id() + "' not expected for event: '" + typex.id() + "'");
         }
      });
      this.type = type;
      this.properties = properties;
   }

   public TelemetryEvent export(TelemetrySession var1) {
      return this.type.export(var1, this.properties);
   }
}
