package net.minecraft.locale;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Language {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
   private static volatile Language instance = loadDefault();

   public Language() {
      super();
   }

   private static Language loadDefault() {
      ImmutableMap.Builder var0 = ImmutableMap.builder();
      BiConsumer var1 = var0::put;

      try {
         InputStream var2 = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
         Throwable var3 = null;

         try {
            loadFromJson(var2, var1);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  var2.close();
               }
            }

         }
      } catch (JsonParseException | IOException var15) {
         LOGGER.error((String)"Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)var15);
      }

      final ImmutableMap var16 = var0.build();
      return new Language() {
         public String getOrDefault(String var1) {
            return (String)var16.getOrDefault(var1, var1);
         }

         public boolean has(String var1) {
            return var16.containsKey(var1);
         }
      };
   }

   public static void loadFromJson(InputStream var0, BiConsumer<String, String> var1) {
      JsonObject var2 = (JsonObject)GSON.fromJson((Reader)(new InputStreamReader(var0, StandardCharsets.UTF_8)), (Class)JsonObject.class);
      Iterator var3 = var2.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         String var5 = UNSUPPORTED_FORMAT_PATTERN.matcher(GsonHelper.convertToString((JsonElement)var4.getValue(), (String)var4.getKey())).replaceAll("%$1s");
         var1.accept(var4.getKey(), var5);
      }

   }

   public static Language getInstance() {
      return instance;
   }

   public abstract String getOrDefault(String var1);

   public abstract boolean has(String var1);
}
