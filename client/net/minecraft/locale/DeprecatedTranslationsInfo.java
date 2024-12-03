package net.minecraft.locale;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public record DeprecatedTranslationsInfo(List<String> removed, Map<String, String> renamed) {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final DeprecatedTranslationsInfo EMPTY = new DeprecatedTranslationsInfo(List.of(), Map.of());
   public static final Codec<DeprecatedTranslationsInfo> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.STRING.listOf().fieldOf("removed").forGetter(DeprecatedTranslationsInfo::removed), Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("renamed").forGetter(DeprecatedTranslationsInfo::renamed)).apply(var0, DeprecatedTranslationsInfo::new));

   public DeprecatedTranslationsInfo(List<String> var1, Map<String, String> var2) {
      super();
      this.removed = var1;
      this.renamed = var2;
   }

   public static DeprecatedTranslationsInfo loadFromJson(InputStream var0) {
      JsonElement var1 = JsonParser.parseReader(new InputStreamReader(var0, StandardCharsets.UTF_8));
      return (DeprecatedTranslationsInfo)CODEC.parse(JsonOps.INSTANCE, var1).getOrThrow((var0x) -> new IllegalStateException("Failed to parse deprecated language data: " + var0x));
   }

   public static DeprecatedTranslationsInfo loadFromResource(String var0) {
      try {
         InputStream var1 = Language.class.getResourceAsStream(var0);

         DeprecatedTranslationsInfo var2;
         label50: {
            try {
               if (var1 != null) {
                  var2 = loadFromJson(var1);
                  break label50;
               }
            } catch (Throwable var5) {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (var1 != null) {
               var1.close();
            }

            return EMPTY;
         }

         if (var1 != null) {
            var1.close();
         }

         return var2;
      } catch (Exception var6) {
         LOGGER.error("Failed to read {}", var0, var6);
         return EMPTY;
      }
   }

   public static DeprecatedTranslationsInfo loadFromDefaultResource() {
      return loadFromResource("/assets/minecraft/lang/deprecated.json");
   }

   public void applyToMap(Map<String, String> var1) {
      for(String var3 : this.removed) {
         var1.remove(var3);
      }

      this.renamed.forEach((var1x, var2) -> {
         String var3 = (String)var1.remove(var1x);
         if (var3 == null) {
            LOGGER.warn("Missing translation key for rename: {}", var1x);
            var1.remove(var2);
         } else {
            var1.put(var2, var3);
         }

      });
   }
}
