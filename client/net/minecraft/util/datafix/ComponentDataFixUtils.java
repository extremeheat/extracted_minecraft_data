package net.minecraft.util.datafix;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.util.GsonHelper;

public class ComponentDataFixUtils {
   private static final String EMPTY_CONTENTS = createTextComponentJson("");

   public ComponentDataFixUtils() {
      super();
   }

   public static <T> Dynamic<T> createPlainTextComponent(DynamicOps<T> var0, String var1) {
      String var2 = createTextComponentJson(var1);
      return new Dynamic(var0, var0.createString(var2));
   }

   public static <T> Dynamic<T> createEmptyComponent(DynamicOps<T> var0) {
      return new Dynamic(var0, var0.createString(EMPTY_CONTENTS));
   }

   private static String createTextComponentJson(String var0) {
      JsonObject var1 = new JsonObject();
      var1.addProperty("text", var0);
      return GsonHelper.toStableString(var1);
   }

   public static <T> Dynamic<T> createTranslatableComponent(DynamicOps<T> var0, String var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("translate", var1);
      return new Dynamic(var0, var0.createString(GsonHelper.toStableString(var2)));
   }

   public static <T> Dynamic<T> wrapLiteralStringAsComponent(Dynamic<T> var0) {
      return (Dynamic)DataFixUtils.orElse(var0.asString().map((var1) -> {
         return createPlainTextComponent(var0.getOps(), var1);
      }).result(), var0);
   }

   public static Dynamic<?> rewriteFromLenient(Dynamic<?> var0) {
      Optional var1 = var0.asString().result();
      if (var1.isEmpty()) {
         return var0;
      } else {
         String var2 = (String)var1.get();
         if (!var2.isEmpty() && !var2.equals("null")) {
            char var3 = var2.charAt(0);
            char var4 = var2.charAt(var2.length() - 1);
            if (var3 == '"' && var4 == '"' || var3 == '{' && var4 == '}' || var3 == '[' && var4 == ']') {
               try {
                  JsonElement var5 = JsonParser.parseString(var2);
                  if (var5.isJsonPrimitive()) {
                     return createPlainTextComponent(var0.getOps(), var5.getAsString());
                  }

                  return var0.createString(GsonHelper.toStableString(var5));
               } catch (JsonParseException var6) {
               }
            }

            return createPlainTextComponent(var0.getOps(), var2);
         } else {
            return createEmptyComponent(var0.getOps());
         }
      }
   }

   public static Optional<String> extractTranslationString(String var0) {
      try {
         JsonElement var1 = JsonParser.parseString(var0);
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
