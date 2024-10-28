package com.mojang.realmsclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.util.UndashedUuid;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;

public class JsonUtils {
   public JsonUtils() {
      super();
   }

   public static <T> T getRequired(String var0, JsonObject var1, Function<JsonObject, T> var2) {
      JsonElement var3 = var1.get(var0);
      if (var3 != null && !var3.isJsonNull()) {
         if (!var3.isJsonObject()) {
            throw new IllegalStateException("Required property " + var0 + " was not a JsonObject as espected");
         } else {
            return var2.apply(var3.getAsJsonObject());
         }
      } else {
         throw new IllegalStateException("Missing required property: " + var0);
      }
   }

   @Nullable
   public static <T> T getOptional(String var0, JsonObject var1, Function<JsonObject, T> var2) {
      JsonElement var3 = var1.get(var0);
      if (var3 != null && !var3.isJsonNull()) {
         if (!var3.isJsonObject()) {
            throw new IllegalStateException("Required property " + var0 + " was not a JsonObject as espected");
         } else {
            return var2.apply(var3.getAsJsonObject());
         }
      } else {
         return null;
      }
   }

   public static String getRequiredString(String var0, JsonObject var1) {
      String var2 = getStringOr(var0, var1, (String)null);
      if (var2 == null) {
         throw new IllegalStateException("Missing required property: " + var0);
      } else {
         return var2;
      }
   }

   public static String getRequiredStringOr(String var0, JsonObject var1, String var2) {
      JsonElement var3 = var1.get(var0);
      if (var3 != null) {
         return var3.isJsonNull() ? var2 : var3.getAsString();
      } else {
         return var2;
      }
   }

   @Nullable
   public static String getStringOr(String var0, JsonObject var1, @Nullable String var2) {
      JsonElement var3 = var1.get(var0);
      if (var3 != null) {
         return var3.isJsonNull() ? var2 : var3.getAsString();
      } else {
         return var2;
      }
   }

   @Nullable
   public static UUID getUuidOr(String var0, JsonObject var1, @Nullable UUID var2) {
      String var3 = getStringOr(var0, var1, (String)null);
      return var3 == null ? var2 : UndashedUuid.fromStringLenient(var3);
   }

   public static int getIntOr(String var0, JsonObject var1, int var2) {
      JsonElement var3 = var1.get(var0);
      if (var3 != null) {
         return var3.isJsonNull() ? var2 : var3.getAsInt();
      } else {
         return var2;
      }
   }

   public static long getLongOr(String var0, JsonObject var1, long var2) {
      JsonElement var4 = var1.get(var0);
      if (var4 != null) {
         return var4.isJsonNull() ? var2 : var4.getAsLong();
      } else {
         return var2;
      }
   }

   public static boolean getBooleanOr(String var0, JsonObject var1, boolean var2) {
      JsonElement var3 = var1.get(var0);
      if (var3 != null) {
         return var3.isJsonNull() ? var2 : var3.getAsBoolean();
      } else {
         return var2;
      }
   }

   public static Date getDateOr(String var0, JsonObject var1) {
      JsonElement var2 = var1.get(var0);
      return var2 != null ? new Date(Long.parseLong(var2.getAsString())) : new Date();
   }
}
