package net.minecraft.client.telemetry;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.serialization.Codec;

public record TelemetryEventInstance(TelemetryEventType type, TelemetryPropertyMap properties) {
   public static final Codec<TelemetryEventInstance> CODEC;

   public TelemetryEventInstance {
      super();
      properties.propertySet().forEach((property) -> {
         if (!type.contains(property)) {
            String var10002 = property.id();
            throw new IllegalArgumentException("Property '" + var10002 + "' not expected for event: '" + type.id() + "'");
         }
      });
   }

   public TelemetryEvent export(final TelemetrySession session) {
      return this.type.export(session, this.properties);
   }

   static {
      CODEC = TelemetryEventType.CODEC.dispatchStable(TelemetryEventInstance::type, TelemetryEventType::codec);
   }
}
