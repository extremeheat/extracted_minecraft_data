package net.minecraft.client.telemetry;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.serialization.Codec;

public record TelemetryEventInstance(TelemetryEventType type, TelemetryPropertyMap properties) {
   public static final Codec<TelemetryEventInstance> CODEC;

   public TelemetryEventInstance(TelemetryEventType type, TelemetryPropertyMap properties) {
      super();
      properties.propertySet().forEach((var1) -> {
         if (!type.contains(var1)) {
            String var10002 = var1.id();
            throw new IllegalArgumentException("Property '" + var10002 + "' not expected for event: '" + type.id() + "'");
         }
      });
      this.type = type;
      this.properties = properties;
   }

   public TelemetryEvent export(TelemetrySession var1) {
      return this.type.export(var1, this.properties);
   }

   public TelemetryEventType type() {
      return this.type;
   }

   public TelemetryPropertyMap properties() {
      return this.properties;
   }

   static {
      CODEC = TelemetryEventType.CODEC.dispatchStable(TelemetryEventInstance::type, TelemetryEventType::codec);
   }
}
