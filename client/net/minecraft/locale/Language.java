package net.minecraft.locale;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Language {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   private static final Language SINGLETON = new Language();
   private final Map<String, String> storage = Maps.newHashMap();
   private long lastUpdateTime;

   public Language() {
      super();

      try {
         InputStream var1 = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
         Throwable var2 = null;

         try {
            JsonElement var3 = (JsonElement)(new Gson()).fromJson(new InputStreamReader(var1, StandardCharsets.UTF_8), JsonElement.class);
            JsonObject var4 = GsonHelper.convertToJsonObject(var3, "strings");
            Iterator var5 = var4.entrySet().iterator();

            while(var5.hasNext()) {
               Entry var6 = (Entry)var5.next();
               String var7 = UNSUPPORTED_FORMAT_PATTERN.matcher(GsonHelper.convertToString((JsonElement)var6.getValue(), (String)var6.getKey())).replaceAll("%$1s");
               this.storage.put(var6.getKey(), var7);
            }

            this.lastUpdateTime = Util.getMillis();
         } catch (Throwable var16) {
            var2 = var16;
            throw var16;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var15) {
                     var2.addSuppressed(var15);
                  }
               } else {
                  var1.close();
               }
            }

         }
      } catch (JsonParseException | IOException var18) {
         LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", var18);
      }

   }

   public static Language getInstance() {
      return SINGLETON;
   }

   public static synchronized void forceData(Map<String, String> var0) {
      SINGLETON.storage.clear();
      SINGLETON.storage.putAll(var0);
      SINGLETON.lastUpdateTime = Util.getMillis();
   }

   public synchronized String getElement(String var1) {
      return this.getProperty(var1);
   }

   private String getProperty(String var1) {
      String var2 = (String)this.storage.get(var1);
      return var2 == null ? var1 : var2;
   }

   public synchronized boolean exists(String var1) {
      return this.storage.containsKey(var1);
   }

   public long getLastUpdateTime() {
      return this.lastUpdateTime;
   }
}
