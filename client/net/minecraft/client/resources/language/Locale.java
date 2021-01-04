package net.minecraft.client.resources.language;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Locale {
   private static final Gson GSON = new Gson();
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   protected final Map<String, String> storage = Maps.newHashMap();

   public Locale() {
      super();
   }

   public synchronized void loadFrom(ResourceManager var1, List<String> var2) {
      this.storage.clear();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         String var5 = String.format("lang/%s.json", var4);
         Iterator var6 = var1.getNamespaces().iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();

            try {
               ResourceLocation var8 = new ResourceLocation(var7, var5);
               this.appendFrom(var1.getResources(var8));
            } catch (FileNotFoundException var9) {
            } catch (Exception var10) {
               LOGGER.warn("Skipped language file: {}:{} ({})", var7, var5, var10.toString());
            }
         }
      }

   }

   private void appendFrom(List<Resource> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Resource var3 = (Resource)var2.next();
         InputStream var4 = var3.getInputStream();

         try {
            this.appendFrom(var4);
         } finally {
            IOUtils.closeQuietly(var4);
         }
      }

   }

   private void appendFrom(InputStream var1) {
      JsonElement var2 = (JsonElement)GSON.fromJson(new InputStreamReader(var1, StandardCharsets.UTF_8), JsonElement.class);
      JsonObject var3 = GsonHelper.convertToJsonObject(var2, "strings");
      Iterator var4 = var3.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         String var6 = UNSUPPORTED_FORMAT_PATTERN.matcher(GsonHelper.convertToString((JsonElement)var5.getValue(), (String)var5.getKey())).replaceAll("%$1s");
         this.storage.put(var5.getKey(), var6);
      }

   }

   private String getOrDefault(String var1) {
      String var2 = (String)this.storage.get(var1);
      return var2 == null ? var1 : var2;
   }

   public String get(String var1, Object[] var2) {
      String var3 = this.getOrDefault(var1);

      try {
         return String.format(var3, var2);
      } catch (IllegalFormatException var5) {
         return "Format error: " + var3;
      }
   }

   public boolean has(String var1) {
      return this.storage.containsKey(var1);
   }
}
