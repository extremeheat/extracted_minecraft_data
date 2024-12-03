package net.minecraft.client.telemetry;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.serialization.Codec;

public record TelemetryEventInstance(TelemetryEventType type, TelemetryPropertyMap properties) {
   public static final Codec<TelemetryEventInstance> CODEC;

   public TelemetryEventInstance(TelemetryEventType var1, TelemetryPropertyMap var2) {
      super();
      var2.propertySet().forEach((var1x) -> {
         if (!var1.contains(var1x)) {
            String var10002 = var1x.id();
            throw new IllegalArgumentException("Property '" + var10002 + "' not expected for event: '" + var1.id() + "'");
         }
      });
      this.type = var1;
      this.properties = var2;
   }

   public TelemetryEvent export(TelemetrySession var1) {
      return this.type.export(var1, this.properties);
   }

   static {
      CODEC = TelemetryEventType.CODEC.dispatchStable(TelemetryEventInstance::type, TelemetryEventType::codec);
   }
}
