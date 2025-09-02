package net.minecraft.util.datafix;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.StrictJsonParser;

public class LegacyComponentDataFixUtils {
   private static final String EMPTY_CONTENTS = createTextComponentJson("");

   public LegacyComponentDataFixUtils() {
      super();
   }

   public static <T> Dynamic<T> createPlainTextComponent(DynamicOps<T> var0, String var1) {
      String var2 = createTextComponentJson(var1);
      return new Dynamic(var0, var0.createString(var2));
   }

   public static <T> Dynamic<T> createEmptyComponent(DynamicOps<T> var0) {
      return new Dynamic(var0, var0.createString(EMPTY_CONTENTS));
   }

   public static String createTextComponentJson(String var0) {
      JsonObject var1 = new JsonObject();
      var1.addProperty("text", var0);
      return GsonHelper.toStableString(var1);
   }

   public static String createTranslatableComponentJson(String var0) {
      JsonObject var1 = new JsonObject();
      var1.addProperty("translate", var0);
      return GsonHelper.toStableString(var1);
   }

   public static <T> Dynamic<T> createTranslatableComponent(DynamicOps<T> var0, String var1) {
      String var2 = createTranslatableComponentJson(var1);
      return new Dynamic(var0, var0.createString(var2));
   }

   public static String rewriteFromLenient(String var0) {
      if (!var0.isEmpty() && !var0.equals("null")) {
         char var1 = var0.charAt(0);
         char var2 = var0.charAt(var0.length() - 1);
         if (var1 == '"' && var2 == '"' || var1 == '{' && var2 == '}' || var1 == '[' && var2 == ']') {
            try {
               JsonElement var3 = LenientJsonParser.parse(var0);
               if (var3.isJsonPrimitive()) {
                  return createTextComponentJson(var3.getAsString());
               }

               return GsonHelper.toStableString(var3);
            } catch (JsonParseException var4) {
            }
         }

         return createTextComponentJson(var0);
      } else {
         return EMPTY_CONTENTS;
      }
   }

   public static boolean isStrictlyValidJson(Dynamic<?> var0) {
      return var0.asString().result().filter((var0x) -> {
         try {
            StrictJsonParser.parse(var0x);
            return true;
         } catch (JsonParseException var2) {
            return false;
         }
      }).isPresent();
   }

   public static Optional<String> extractTranslationString(String var0) {
      try {
         JsonElement var1 = LenientJsonParser.parse(var0);
         if (var1.isJsonObject()) {
            JsonObject var2 = var1.getAsJsonObject();
            JsonElement var3 = var2.get("translate");
            if (var3 != null && var3.isJsonPrimitive()) {
               return Optional.of(var3.getAsString());
            }
         }
      } catch (JsonParseException var4) {
      }

      return Optional.empty();
   }
}
