package com.mojang.realmsclient.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public enum ServiceQuality {
   GREAT(1, "icon/ping_5"),
   GOOD(2, "icon/ping_4"),
   OKAY(3, "icon/ping_3"),
   POOR(4, "icon/ping_2"),
   UNKNOWN(5, "icon/ping_unknown");

   final int value;
   private final Identifier icon;

   private ServiceQuality(final int var3, final String var4) {
      this.value = var3;
      this.icon = Identifier.withDefaultNamespace(var4);
   }

   public static @Nullable ServiceQuality byValue(int var0) {
      for(ServiceQuality var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      return null;
   }

   public int getValue() {
      return this.value;
   }

   public Identifier getIcon() {
      return this.icon;
   }

   // $FF: synthetic method
   private static ServiceQuality[] $values() {
      return new ServiceQuality[]{GREAT, GOOD, OKAY, POOR, UNKNOWN};
   }

   public static class RealmsServiceQualityJsonAdapter extends TypeAdapter<ServiceQuality> {
      private static final Logger LOGGER = LogUtils.getLogger();

      public RealmsServiceQualityJsonAdapter() {
         super();
      }

      public void write(JsonWriter var1, ServiceQuality var2) throws IOException {
         var1.value((long)var2.value);
      }

      public ServiceQuality read(JsonReader var1) throws IOException {
         int var2 = var1.nextInt();
         ServiceQuality var3 = ServiceQuality.byValue(var2);
         if (var3 == null) {
            LOGGER.warn("Unsupported ServiceQuality {}", var2);
            return ServiceQuality.UNKNOWN;
         } else {
            return var3;
         }
      }

      // $FF: synthetic method
      public Object read(final JsonReader var1) throws IOException {
         return this.read(var1);
      }

      // $FF: synthetic method
      public void write(final JsonWriter var1, final Object var2) throws IOException {
         this.write(var1, (ServiceQuality)var2);
      }
   }
}
